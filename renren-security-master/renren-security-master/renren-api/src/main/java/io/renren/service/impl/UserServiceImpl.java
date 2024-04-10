/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.validator.AssertUtils;
import io.renren.dao.UserDao;
import io.renren.dto.LoginDTO;
import io.renren.entity.TokenEntity;
import io.renren.entity.UserEntity;
import io.renren.service.TokenService;
import io.renren.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<UserDao, UserEntity> implements UserService {
    private final TokenService tokenService;

    @Override
    public UserEntity getByMobile(String mobile) {
        return baseDao.getUserByMobile(mobile);
    }

    @Override
    public UserEntity getUserByUserId(Long userId) {
        return baseDao.getUserByUserId(userId);
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        UserEntity user = getByMobile(dto.getMobile());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_PASSWORD_ERROR);

        //密码错误
        if (!user.getPassword().equals(DigestUtil.sha256Hex(dto.getPassword()))) {
            throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }

        //获取登录token
        TokenEntity tokenEntity = tokenService.createToken(user.getId());

        Map<String, Object> map = new HashMap<>(2);
        map.put("token", tokenEntity.getToken());
        map.put("expire", tokenEntity.getExpireDate().getTime() - System.currentTimeMillis());

        return map;
    }

}