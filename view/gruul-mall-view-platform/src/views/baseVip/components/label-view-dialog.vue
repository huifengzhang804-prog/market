<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { useVipTagsStore } from '@/store/modules/vipSetting'
import { doPostUserTag, doGetUserTag } from '@/apis/vip'
import uuid from '@/utils/uuid'
import type { ParamsUserTag } from '@/apis/vip'
import type { PropType } from 'vue'
type labelItem = { tagName: string; option: boolean; id: string }
type labelViewItme = { id?: string } & labelItem

const $props = defineProps({
    labelView: { type: Boolean, default: false },
    userIds: { type: Array as PropType<string[]>, required: true },
    currentUserTagIds: { type: Array as PropType<string[]>, required: true },
})
const $emit = defineEmits(['update:labelView', 'reset'])
const labelViewList = ref<labelViewItme[]>([{ id: uuid(10), tagName: '老客户', option: false }])
const $vipTagsStore = useVipTagsStore()
let metaLabelIds: string[] = []
const labelViewData = reactive({
    labelViewVal: '',
    isAddLabelView: false,
})
const { labelViewVal, isAddLabelView } = toRefs(labelViewData)

onUnmounted(() => {
    $vipTagsStore.SET_TAGS([])
})

watch(
    () => $props.labelView,
    (val) => {
        if (val) {
            initGetUserTag()
        }
    },
)

async function initGetUserTag() {
    const { code, data } = (await doGetUserTag()) as any
    if (code !== 200) return ElMessage.error('会员标签获取失败')
    $vipTagsStore.SET_TAGS(data)
    metaLabelIds = data?.map((item: labelItem) => item.id) || []
    labelViewList.value =
        data.map((item: labelViewItme) => {
            if ($props.currentUserTagIds.includes(item.id)) {
                item.option = true
            }
            return item
        }) || []
}
const handleInputHide = (item: labelViewItme) => {
    if (!item.tagName) {
        labelViewList.value = labelViewList.value.filter((labelItem) => labelItem.id !== item.id)
    }
}
const handleAdd = () => {
    if (!labelViewVal.value) {
        ElMessage.info('请输入标签名')
        return
    }
    const currentLabel = labelViewList.value.find((item) => item.tagName === labelViewVal.value)
    if (currentLabel) {
        ElMessage.error('标签重复')
        return
    }
    labelViewList.value.push({ id: uuid(10), tagName: labelViewVal.value, option: false })
    labelViewVal.value = ''
}
/**
 * 单个操作用户Tag
 */
const handleSubmit = async () => {
    const params: ParamsUserTag = { addTagList: [], delUserTagIdList: [], updateTagList: [], userIdList: [] }
    params.userIdList = $props.userIds
    // 增加
    params.addTagList = labelViewList.value
        .filter((v) => !metaLabelIds.includes(v.id))
        .map((item) => {
            return {
                tagName: item.tagName,
                option: item.option,
            }
        })
    // 删除
    params.delUserTagIdList = metaLabelIds.filter((item) => !labelViewList.value.some((v) => v.id === item))
    //更新
    params.updateTagList = labelViewList.value.filter((item) => metaLabelIds.includes(item.id))
    const { data, code } = await doPostUserTag(params)
    if (code !== 200) return ElMessage.error('更新失败')
    labelViewVal.value = ''
    ElMessage.success('更新成功')
    initGetUserTag()
    $emit('update:labelView', false)
    $emit('reset')
}
const handleClose = () => {
    labelViewVal.value = ''
    isAddLabelView.value = false
    $emit('update:labelView', false)
}
</script>

<template>
    <el-dialog :model-value="$props.labelView" class="label-view-dialog" title="设置标签" width="30%" @close="handleClose">
        <el-row :gutter="20" class="tags-body">
            <el-col v-for="item in labelViewList" :key="item.id" :span="8">
                <div class="label-view-dialog__body">
                    <el-checkbox v-model="item.option" :label="''" :value="item.id"><span></span> </el-checkbox>
                    <div style="width: 80%">
                        <el-input
                            v-model="item.tagName"
                            clearable
                            maxlength="10"
                            class="testInput"
                            type="text"
                            @blur="handleInputHide(item)"
                            @clear="handleInputHide(item)"
                        />
                    </div>
                    <!-- <div v-else style="width: 80%" @mouseover="handleInputShow(item)">
                        <div class="label-view-dialog__body--item" type="text">{{ item.tagName }}</div>
                    </div> -->
                </div>
            </el-col>
        </el-row>
        <div v-if="isAddLabelView" class="label-view-dialog__add">
            <div class="label-view-dialog__add-left">
                <el-input v-model.trim="labelViewVal" maxlength="10" placeholder="请输入标签名" />
            </div>
            <div class="label-view-dialog__add-right">
                <el-link :underline="false" type="primary" @click="handleAdd">添加</el-link>
                <el-link :underline="false" type="primary" @click="isAddLabelView = false">取消</el-link>
            </div>
        </div>
        <div v-else class="ml30 label-view-dialog__add-right-link">
            <el-link :underline="false" type="primary" @click="isAddLabelView = true">新增标签</el-link>
        </div>
        <template #footer>
            <el-button @click="$emit('update:labelView', false)">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确认</el-button>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
.testInput:deep(.el-input__inner) {
    border: 1px solid transparent;
    box-shadow: 0 0 0;
    &:hover {
        border: 1px solid #ccc;
    }
}
@include b(tags-body) {
    max-height: 130px;
    overflow-y: scroll;
}
@include b(label-view-dialog) {
    /* &::.el-dialog__body */
    @include e(body) {
        margin-bottom: 20px;
        @include flex;
        @include m(item) {
            padding: 0 11px;
        }
    }
    @include e(input) {
        border: transparent;
    }
    @include e(add) {
        height: 40px;
        @include flex(flex-start);
    }
    @include e(add-left) {
        width: 60%;
        margin: 0 5%;
    }
    @include e(add-right) {
        width: 80px;
        @include flex;
        justify-content: space-between;
    }
    @include e(add-right-link) {
        height: 40px;
        line-height: 40px;
    }
}

@include b(shadowNone) {
    & > :deep(.el-input__inner) {
        box-shadow: none !important;
        border: none;
    }
}
.ml30 {
    margin-left: 30px;
}
</style>
