<!-- eslint-disable prettier/prettier -->
<template>
  <el-switch v-model="draggable" active-text="开启拖拽" inactive-text="关闭拖拽"> </el-switch>
  <el-button v-if="draggable" @click="batchSave">批量保存</el-button>
  <el-button type="danger" @click="batchDelete">批量保存</el-button>
  <el-tree
    :data="menus"
    :props="defaultProps"
    @node-click="handleNodeClick"
    :expand-on-click-node="false"
    show-checkbox
    node-key="catId"
    :default-expanded-keys="expendedKey"
    :draggable="draggable"
    :allow-drop="allowDrop"
    @node-drop="handleDrop"
  >
    <template v-slot="{ node, data }">
      <span class="custom-tree-node">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            v-if="node.level <= 2"
            type="text"
            size="mini"
            @click="append(data)"
            data-bs-toggle="modal"
            data-bs-target="#exampleModal"
          >
            添加
          </el-button>
          <el-button
            type="text"
            size="mini"
            @click="() => edit(data)"
            data-bs-toggle="modal"
            data-bs-target="#exampleModal"
          >
            修改
          </el-button>
          <el-button
            v-if="node.childNodes.length == 0"
            type="text"
            size="mini"
            @click="() => remove(node, data)"
          >
            删除
          </el-button>
        </span>
      </span>
    </template>
  </el-tree>

  <!-- Modal -->
  <div
    class="modal fade"
    id="exampleModal"
    tabindex="-1"
    aria-labelledby="exampleModalLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="exampleModalLabel">{{ title }}</h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <form :v-model="category">
            <div class="mb-3">
              <label for="recipient-name" class="col-form-label">分类名称:</label>
              <input type="text" class="form-control" id="recipient-name" v-model="category.name" />
            </div>
            <div class="mb-3">
              <label for="recipient-name" class="col-form-label">图标:</label>
              <input type="text" class="form-control" id="recipient-name" v-model="category.icon" />
            </div>
            <div class="mb-3">
              <label for="recipient-name" class="col-form-label">计量单位:</label>
              <input
                type="text"
                class="form-control"
                id="recipient-name"
                v-model="category.productUnit"
              />
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          <button
            type="button"
            class="btn btn-primary"
            data-bs-dismiss="modal"
            @click="submitData()"
          >
            确定
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import $ from "jquery";

export default {
  data() {
    return {
      pCid: [],
      draggable: false,
      updateNodes: [],
      maxLevel: 0,
      // 菜单树数据
      title: "",
      dialogType: "", // 判断是添加还是修改
      // 菜单树数据
      category: {
        name: "",
        parentCid: 0,
        catLevel: 0,
        showStatus: 1,
        sort: 0,
        productUnit: "",
        icon: "",
        catId: null
      },
      centerDialogVisible: false,
      menus: [],
      expendedKey: [],
      defaultProps: {
        children: "children",
        label: "name"
      }
    };
  },
  methods: {
    // handleNodeClick(data) {
    //   // console.log(data);
    // },
    // 获取菜单
    getMenus() {
      $.ajax({
        url: "http://localhost:88/api/product/category/list/tree",
        method: "GET",
        success: (data) => {
          console.log("菜单数据:", data.data);
          this.menus = data.data;
        },
        error: (xhr, status, error) => {
          console.error("获取菜单数据时发生错误:", error);
        }
      });
    },

    countNodeLevel(node) {
      if (node.childNodes != null && node.childNodes.length > 0) {
        for (let i = 0; i < node.childNodes.length; i++) {
          if (node.childNodes[i].level > this.maxLevel) {
            this.maxLevel = node.childNodes[i].level;
          }
          this.countNodeLevel(node.childNodes[i]);
        }
      }
    },
    batchDelete() {},
    batchSave() {
      $.ajax({
        url: `http://localhost:88/api/product/category/update/sort`,
        method: "POST",
        data: JSON.stringify(this.updateNodes),
        contentType: "application/json",
        success: () => {
          this.$message({
            message: "菜单顺序修改成功",
            type: "success"
          });
          // 刷新菜单
          this.getMenus();
          // 保持展开
          this.expendedKey = this.pCid;
          this.updateNodes = [];
          this.maxLevel = 0;
          // this.pCid = 0;
        }
      });
    },
    handleDrop(draggingNode, dropNode, dropType, ev) {
      // 发送请求修改菜单的顺序
      let pCid = 0;
      let siblings = null;
      if (dropType == "before" || dropType == "after") {
        pCid = dropNode.parent.data.catId == undefined ? 0 : dropNode.parent.data.catId;
        siblings = dropNode.parent.childNodes;
      } else {
        pCid = dropNode.data.catId;
        siblings = dropNode.childNodes;
      }
      this.pCid.push(pCid);

      for (let i = 0; i < siblings.length; i++) {
        if (siblings[i].data.catId == draggingNode.data.catId) {
          // 如果遍历的是当前正在拖拽的节点
          let catLevel = draggingNode.level;
          if (siblings[i].level != draggingNode.level) {
            // 当前节点的层级发生改变
            catLevel = siblings[i].level;
            // 修改他子节点的层级
            this.updateChildNodeLevel(siblings[i]);
          }
          this.updateNodes.push({
            catId: siblings[i].data.catId,
            sort: i,
            parentCid: pCid,
            catLevel: catLevel
          });
        } else {
          this.updateNodes.push({
            catId: siblings[i].data.catId,
            sort: i
          });
        }
      }

      // 3.当前拖拽节点的最新层级
      console.log(this.updateNodes);
    },

    updateChildNodeLevel(node) {
      if (node.childNodes.length > 0) {
        for (let i = 0; i < node.childNodes.length; i++) {
          let cNode = node.childNodes[i].data;
          this.updateNodes.push({
            catId: cNode.catId,

            catLevel: node.childNodes[i].level
          });
          this.updateChildNodeLevel(node.childNodes[i]);
        }
      }
    },

    allowDrop(draggingNode, dropNode, type) {
      // 被拖动的当前节点以及所在的父节点总层数不大于3
      this.countNodeLevel(draggingNode.data);
      let deep = Math.abs(this.maxLevel - draggingNode.level + 1);
      if (type == "inner") {
        return deep + dropNode.level <= 3;
      } else {
        return deep + dropNode.parent.level <= 3;
      }
    },
    edit(data) {
      // 发送请求获取当前节点最新的数据
      $.ajax({
        url: `http://localhost:88/api/product/category/info/${data.catId}`,
        method: "GET",
        success: (data) => {
          console.log(data);
          this.category.name = data.category.name;
          this.category.catId = data.category.catId;
          this.category.icon = data.category.icon;
          this.category.productUnit = data.category.productUnit;
          this.category.parentCid = data.category.parentCid;
          this.category.catLevel = data.category.catLevel;
          this.category.showStatus = data.category.showStatus;
          this.category.sort = data.category.sort;
        }
      });
      this.title = "修改菜单";
      this.dialogType = "edit";
      // 打开对话框

      // this.category.catId = data.catId;
      console.log(data);
    },

    append(data) {
      this.title = "添加菜单";
      this.dialogType = "add";
      // 打开对话框
      this.centerDialogVisible = true;
      this.category.parentCid = data.catId;
      this.category.catLevel = data.catLevel * 1 + 1;
      console.log("asdfds");

      // 改回默认值
      this.category.name = "";
      this.category.catId = null;
      this.category.icon = "";
      this.category.productUnit = "";
      this.category.showStatus = 1;
      this.category.sort = 0;
    },

    submitData() {
      if (this.dialogType == "add") {
        this.addCategory();
      } else {
        this.editCategory();
      }
    },
    // 修改三级分类的方法
    editCategory() {
      console.log(this.category);
      let { catId, name, icon, productUnit } = this.category;
      $.ajax({
        url: "http://localhost:88/api/product/category/update",
        method: "post",
        contentType: "application/json",
        data: JSON.stringify({
          catId,
          name,
          icon,
          productUnit
        }),
        success: () => {
          this.$message({
            message: "菜单修改成功",
            type: "success"
          });
          // 关闭对话框
          // 刷新菜单
          this.getMenus();
          // 保持展开
          this.expendedKey = [this.category.parentCid];
        },
        error: (xhr, status, error) => {
          console.error("获取菜单数据时发生错误:", error);
        }
      });
    },
    // 添加三级分类的方法
    addCategory() {
      console.log(this.category);
      $.ajax({
        url: "http://localhost:88/api/product/category/save",
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(this.category),
        success: () => {
          this.$message({
            message: "菜单保存成功",
            type: "success"
          });
          // 关闭对话框
          // 刷新菜单
          this.getMenus();
          // 保持展开
          this.expendedKey = [this.category.parentCid];
        },
        error: (xhr, status, error) => {
          console.error("获取菜单数据时发生错误:", error);
        }
      });
    },

    remove(node, data) {
      let id = data.catId; // 单个 ID
      this.$confirm(`是否删除[${data.name}]菜单`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          // 确定删除后向后端发请求
          $.ajax({
            url: "http://localhost:88/api/product/category/delete",
            method: "POST",
            contentType: "application/json", // 设置 Content-Type 为 application/json
            data: JSON.stringify([id]), // 直接发送数组
            success: () => {
              this.$message({
                message: "菜单删除成功",
                type: "success"
              });
              // 刷新菜单
              this.getMenus();
              // 删除成功后，手动从菜单树中移除该节点
              this.expendedKey = [node.parent.data.catId];
            },
            error: (xhr, status, error) => {
              console.error("删除菜单时发生错误:", error);
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    }
  },
  created() {
    this.getMenus();
  }
};
</script>
