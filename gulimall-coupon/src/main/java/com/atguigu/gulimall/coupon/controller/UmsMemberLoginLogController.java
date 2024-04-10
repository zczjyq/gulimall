package com.atguigu.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gulimall.coupon.entity.UmsMemberLoginLogEntity;
import com.atguigu.gulimall.coupon.service.UmsMemberLoginLogService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 会员登录记录
 *
 * @author zczjyq
 * @email 937473303@qq.com
 * @date 2024-04-03 22:34:11
 */
@RestController
@RequestMapping("coupon/umsmemberloginlog")
public class UmsMemberLoginLogController {
    @Autowired
    private UmsMemberLoginLogService umsMemberLoginLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = umsMemberLoginLogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:umsmemberloginlog:info")
    public R info(@PathVariable("id") Long id){
		UmsMemberLoginLogEntity umsMemberLoginLog = umsMemberLoginLogService.getById(id);

        return R.ok().put("umsMemberLoginLog", umsMemberLoginLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:umsmemberloginlog:save")
    public R save(@RequestBody UmsMemberLoginLogEntity umsMemberLoginLog){
		umsMemberLoginLogService.save(umsMemberLoginLog);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:umsmemberloginlog:update")
    public R update(@RequestBody UmsMemberLoginLogEntity umsMemberLoginLog){
		umsMemberLoginLogService.updateById(umsMemberLoginLog);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:umsmemberloginlog:delete")
    public R delete(@RequestBody Long[] ids){
		umsMemberLoginLogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
