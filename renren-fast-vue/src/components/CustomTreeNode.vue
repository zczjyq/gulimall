<template>
  <span class="custom-tree-node">
    <span>{{ node }}</span>
    <span>
      <el-button type="text" size="mini" @click="() => append(data)"> Append </el-button>
      <el-button type="text" size="mini" @click="() => remove(node, data)"> Delete </el-button>
    </span>
  </span>
</template>

<script>
import $ from "jquery";
export default {
  data() {
    return {
      menus: [],
      defaultProps: {
        children: "children",
        label: "name"
      }
    };
  },
  methods: {
    handleNodeClick(data) {
      console.log(data);
    },

    getMenus() {
      $.ajax({
        url: "http://localhost:88/api/product/category/list/tree",
        method: "GET",
        success: (data) => {
          // 请求成功时的处理逻辑
          console.log("菜单数据:", data.data);
          this.menus = data.data;
        },
        error: (xhr, status, error) => {
          // 请求失败时的处理逻辑
          console.error("获取菜单数据时发生错误:", error);
        }
      });
    },

    append(data) {
      console.log(data);
    },

    remove(node, data) {
      console.log(node, data);
    }
  },
  created() {
    this.getMenus();
  }
};
</script>
