<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-26 15:57:57
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-05-23 11:08:43
-->
<template>
  <p
    :style="{
      lineHeight: '50px',
      fontSize: '20px',
      textAlign: 'center',
      background: consoleHandle[props.clientType].color,
      color: '#fff',
    }"
  >
    {{ consoleHandle[props.clientType].title }}
  </p>
  <div
    :style="{
      border: '3px dotted ' + consoleHandle[props.clientType].color,
      padding: '20px 40px',
    }"
  >
    <el-tree
      v-if="rv.treeList.length > 0"
      ref="treeRef"
      :data="rv.treeList"
      :expand-on-click-node="false"
      :props="{ class: customNodeClass[props.clientType] }"
      default-expand-all
      node-key="id"
    >
      <template #default="{ node, data }">
        <span class="custom-tree-node" @click="editTreeForm(data)">
          <span>
            <i :class="data.icon" />{{ data.name }} [{{ data.order }}]
            <el-tag
              :type="data.type === 'CATALOG' ? 'success' : 'warning'"
              size="small"
              >{{ data.type === "CATALOG" ? "目录" : "菜单" }}</el-tag
            >
          </span>
          <span>
            <el-button
              size="small"
              type="warning"
              @click.stop="editTreeForm(data)"
              >编辑</el-button
            >
            <el-button
              v-if="data.type === 'CATALOG'"
              size="small"
              type="primary"
              @click.stop="showDialogHandle(data.id, data)"
              >新增
            </el-button>
            <el-popconfirm
              v-if="
                (data.type === 'CATALOG' && data.children.length === 0) ||
                data.type === 'MENU'
              "
              :title="`确定删除[${data.name}]?`"
              cancel-button-text="取消"
              confirm-button-text="确定"
              @confirm="delTreeForm(data)"
            >
              <template #reference>
                <el-button size="small" type="danger" @click.stop="() => {}"
                  >删除</el-button
                >
              </template>
            </el-popconfirm>
          </span>
        </span>
      </template>
    </el-tree>
    <el-button
      :style="{
        background: consoleHandle[props.clientType].color,
        color: '#fff',
      }"
      @click="showDialogHandle('0')"
      >新增菜单
    </el-button>
  </div>
  <el-dialog
    v-model="newDialogVisible"
    :title="!rv.authTreeForm.id ? '新增权限' : '编辑权限'"
    @close="closeDialog"
  >
    <el-form ref="ruleFormRef" :model="rv.authTreeForm" :rules="actionRules()">
      <el-form-item
        v-if="!isCatalog"
        :label-width="80"
        label="访问路径"
        prop="path"
      >
        <el-input
          v-model="rv.authTreeForm.path"
          autocomplete="off"
          @input="generatePermHandle"
        />
      </el-form-item>
      <el-form-item
        v-if="!isCatalog"
        :label-width="80"
        label="访问权限"
        prop="perm"
      >
        <el-input v-model="rv.authTreeForm.perm" autocomplete="off" />
      </el-form-item>
      <el-form-item :label-width="80" label="名称" prop="name">
        <el-input v-model="rv.authTreeForm.name" autocomplete="off" />
      </el-form-item>
      <el-form-item :label-width="80" label="类型" prop="type">
        <el-select v-model="rv.authTreeForm.type" placeholder="请选择类型">
          <el-option label="目录" value="CATALOG" />
          <el-option label="菜单" value="MENU" />
        </el-select>
      </el-form-item>
      <el-form-item :label-width="80" label="排序" prop="order">
        <el-input
          v-model="rv.authTreeForm.order"
          autocomplete="off"
          type="number"
        />
      </el-form-item>
      <el-form-item :label-width="110" label="管理员独占?" prop="unshared">
        <el-switch v-model="rv.authTreeForm.unshared" />
      </el-form-item>
      <el-form-item :label-width="80" label="图标" prop="icon">
        <el-select
          v-model="rv.authTreeForm.icon"
          clearable
          filterable
          fit-input-width
          placeholder="请选择图标"
          style="width: 100%"
          @change="iconChange"
        >
          <template #prefix>
            <i :class="rv.authTreeForm.icon" />
          </template>
          <el-option
            v-for="item in iconfont.glyphs"
            :key="getIconClass(item.font_class)"
            :label="item.name"
            :value="getIconClass(item.font_class)"
          >
            <div>
              <i :class="getIconClass(item.font_class)" />
              <span>{{ item.name }}</span>
            </div>
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="newDialogVisible = false">关闭</el-button>
        <el-button
          id="directTag"
          v-debounce="{
            func: submitForm,
            wait: 1000,
            type: 'click',
          }"
          type="primary"
          >提交</el-button
        >
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { PropType, reactive, ref, toRaw, watch } from "vue";
import type { ElTree, FormInstance } from "element-plus";
import { ClientType, MenuTree, MenuType } from "./MenuD";
import iconfont from "../../assets/css/font/iconfont.json";

const icon = ref(null);
import {
  doDelPermissions,
  doEditPermissions,
  doNewPermissions,
  getAuth,
} from "../../apis/setting";

const props = defineProps({
  clientType: {
    type: String as PropType<ClientType>,
    required: true,
  },
});
const consoleHandle: Record<ClientType, Object> = {
  PLATFORM_CONSOLE: {
    title: "平台端",
    color: "#7a7cfd",
  },
  SHOP_CONSOLE: {
    title: "商家端",
    color: "#409EFF",
  },
};
const customNodeClass: Record<MenuType, string> = {
  CATALOG: "is-catalog",
  MENU: "is-menu",
};
const ruleFormRef = ref<FormInstance>();

// 新增弹窗
const newDialogVisible = ref(false);

const rv = reactive({
  authTreeForm: {
    id: null,
    label: null,
    clientType: ClientType.PLATFORM_CONSOLE,
    parentId: "0",
    perm: "",
    name: "",
    type: MenuType.CATALOG,
    order: 1,
    path: "/",
    unshared: false,
    children: [],
    icon: null,
  } as MenuTree,
  treeList: [] as MenuTree[],
});

const constantRulePath = [
  {
    required: true,
    message: "请填写访问路径",
    trigger: "blur",
  },
];
const constantRulePerm = [
  {
    required: true,
    message: "请填写权限信息",
    trigger: "blur",
  },
];
const actionRules = () => {
  let tempObj = {
    name: [
      {
        required: true,
        message: "请填写菜单/目录名称",
        trigger: "blur",
      },
    ],
  };
  if (!isCatalog.value) {
    tempObj.perm = constantRulePerm;
    tempObj.path = constantRulePath;
  }
  return tempObj;
};
const treeRef = ref<InstanceType<typeof ElTree>>();

// 编辑目录标识符
const isCatalog = ref(false);

watch(
  () => props.clientType,
  (val) => {
    isCatalog.value = true;
    rv.authTreeForm.clientType = val;
    rv.treeList = [];
    initTreeHandle(val);
  },
  { immediate: true }
);
watch(
  () => rv.authTreeForm.type,
  (val) => {
    if (val === "MENU") {
      isCatalog.value = false;
      return;
    }
    isCatalog.value = true;
    rv.authTreeForm.path = "/";
    rv.authTreeForm.perm = "";
  }
);

const showDialogHandle = (parentId: string, data?: any) => {
  // 判断区分新增目录还是菜单新增
  rv.authTreeForm.id = null;
  rv.authTreeForm.type =
    data && data.type === MenuType.CATALOG ? MenuType.MENU : MenuType.CATALOG;
  rv.authTreeForm.parentId = parentId;
  if (!data) {
    rv.authTreeForm.icon = null;
  }
  newDialogVisible.value = true;
  console.log(rv.authTreeForm);
};

const generatePermHandle = () => {
  const authTreeForm = rv.authTreeForm;
  if (!authTreeForm.path) {
    return;
  }
  authTreeForm.perm = authTreeForm.path.split("/").splice(1).join(":");
};

const submitForm = async () => {
  if (!ruleFormRef.value) return;
  await ruleFormRef.value.validate((valid, fields) => {
    if (!valid) {
      return;
    }
    newDialogVisible.value = false;
    if (rv.authTreeForm.id) {
      doEditPermissions(rv.authTreeForm.id, rv.authTreeForm).then((res) => {
        if (res.code === 200) {
          initTreeHandle();
        }
      });
      return;
    }
    doNewPermissions(rv.authTreeForm).then((res) => {
      if (res.code === 200) {
        initTreeHandle();
      }
    });
  });
};
const closeDialog = () => {
  rv.authTreeForm.parentId = "0";
  rv.authTreeForm.perm = "";
  rv.authTreeForm.name = "";
  rv.authTreeForm.type = MenuType.CATALOG;
  rv.authTreeForm.order = 1;
  rv.authTreeForm.path = "/";
  rv.authTreeForm.children = [];
};
const editTreeForm = (data: MenuTree) => {
  const rawData = JSON.parse(JSON.stringify(toRaw(data)));
  if (rawData.type === "MENU") {
    rawData.perm = rawData.perms[0];
  }
  rv.authTreeForm = rawData;
  rv.authTreeForm.clientType = props.clientType;
  newDialogVisible.value = true;
};
const delTreeForm = (data: MenuTree) => {
  doDelPermissions(data.id).then((res) => {
    if (res.code === 200) {
      initTreeHandle();
    }
  });
};

function initTreeHandle(clientType: string = props.clientType) {
  getAuth(clientType).then((res) => {
    rv.treeList = res.data;
  });
}

const iconChange = (val: string) => {
  rv.authTreeForm.icon = val;
};

const getIconClass = (clazz: string) => {
  return "iconfont " + iconfont.css_prefix_text + clazz;
};
</script>

<style>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
