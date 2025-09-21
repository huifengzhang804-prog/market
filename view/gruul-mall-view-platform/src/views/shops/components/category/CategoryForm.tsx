import { ElForm, type FormRules, ElFormItem, ElInput, ElInputNumber, ElUpload, ElIcon, FormInstance, ElRow, ElCol, ElMessage } from 'element-plus'
import type { Ref } from 'vue'
import 'element-plus/dist/index.css'
import './category-form.scss'
import { SelectItemType, SelectLevelType } from './type'
import SelectMaterial from '@/views/material/selectMaterial.vue'

const CategoryForm = defineComponent({
    props: {
        submitFormModel: {
            type: Object,
            default: () => ({}),
        },
        radioCategory: {
            type: Number,
            default: 0,
        },
        isEdit: {
            type: Boolean,
            default: false,
        },
        sLevelInputArray: {
            type: Array,
            default: () => [],
        },
        selectLevel: {
            type: Object,
            default: () => ({ firstArr: [], secondArr: [] }),
        },
        currentUploadIdx: {
            type: Number,
            default: 0,
        },
        firstArrList: {
            type: Array<SelectItemType>,
            default: () => [],
        },
    },
    emits: ['update:submitFormModel', 'update:formRef', 'update:sLevelInputArray', 'update:currentUploadIdx'],
    setup(props, ctx) {
        const dialogVisible = ref(false)
        const parameterId = ref<null | number>(null)
        const parameterIds = ref<null | number>(null)
        const formRef: Ref<FormInstance | null> = ref(null)
        const submitForm = computed({
            get() {
                return props.submitFormModel
            },
            set(value) {
                ctx.emit('update:submitFormModel', value)
            },
        })
        const sLevelInputArr = computed({
            get() {
                return props?.sLevelInputArray
            },
            set(value) {
                ctx.emit('update:sLevelInputArray', value)
            },
        }) as Ref<any[]>
        const currentUploadIndex = computed({
            get() {
                return props?.currentUploadIdx
            },
            set(value) {
                ctx.emit('update:currentUploadIdx', value)
            },
        })
        onMounted(() => {
            ctx.emit('update:formRef', formRef.value)
        })
        return {
            submitForm,
            sLevelInputArr,
            currentUploadIndex,
            formRef,
            dialogVisible,
            parameterId,
            parameterIds,
        }
    },
    render() {
        const rules: FormRules = {
            fLevelInput: [
                {
                    required: true,
                    message: '请输入类目',
                    trigger: 'blur',
                },
            ],
            sLevelInput: [
                { required: true, message: '请输入二级类目', trigger: ['blur', 'change'] },
                { min: 1, max: 100, message: '类目文字在1~100之间', trigger: 'blur' },
            ],
            sLevelSelect: [
                {
                    required: true,
                    message: '请选择类目',
                    trigger: 'change',
                },
            ],
        }
        const handleDelSecond = (index: number) => {
            if (this.sLevelInputArr?.length === 1) return ElMessage.error('请至少保留一项')
            this.sLevelInputArr.splice(index, 1)
        }
        const handleDeleteThird = (index: number) => {
            if (this.submitForm.tLevelTable?.length === 1) return ElMessage.error('请至少保留一项')
            this.submitForm.tLevelTable.splice(index, 1)
        }
        return (
            <>
                <ElForm ref="formRef" model={this.submitForm} rules={rules} labelWidth={this.radioCategory === 1 ? 94 : 0}>
                    {this.radioCategory >= 0 && (
                        <ElFormItem label="一级类目" prop={this.radioCategory === 0 ? 'fLevelInput' : ''}>
                            {this.radioCategory === 0 && (
                                <ElInput
                                    modelValue={this.submitForm.fLevelInput}
                                    onUpdate:modelValue={(value: string) => (this.submitForm.fLevelInput = value)}
                                    placeholder="请输入一级类目"
                                    maxlength={10}
                                    showWordLimit={true}
                                />
                            )}
                            {this.radioCategory !== 0 && (
                                <ElInput
                                    modelValue={this.firstArrList.find((item: SelectItemType) => item.id === this.submitForm.fLevelSelect)?.name}
                                    placeholder="请输入一级类目"
                                    disabled
                                />
                            )}
                        </ElFormItem>
                    )}
                    {this.radioCategory === 1 && this.isEdit ? (
                        this.sLevelInputArr.map((item: any, index: number) => (
                            <div key={index}>
                                <ElFormItem label="二级类目">
                                    <ElInput
                                        modelValue={item.sLevelInput}
                                        onUpdate:modelValue={(value: string) => (item.sLevelInput = value)}
                                        maxlength={10}
                                        showWordLimit={true}
                                        placeholder="类目名称"
                                    />
                                </ElFormItem>
                                <ElFormItem label="类目扣率(%)">
                                    <ElInputNumber
                                        modelValue={item.deductionRatio}
                                        onUpdate:modelValue={(value) => (this.sLevelInputArr[index].deductionRatio = value)}
                                        max={100}
                                        min={0}
                                        stepStrictly
                                        step={1}
                                        placeholder="0-100"
                                    />
                                </ElFormItem>
                                <ElFormItem label="图片(84*84)">
                                    {/* <ElUpload
                                        action="gruul-mall-carrier-pigeon/oss/upload"
                                        class="avatar-uploader"
                                        style="margin-left:60px;margin-right:10px;"
                                        httpRequest={elementUploadRequest}
                                        showFileList={false}
                                        onSuccess={(value) => (item.categoryImg = value.data)}
                                    >
                                        {item.categoryImg ? (
                                            <img src={item.categoryImg} class="avatar" />
                                        ) : (
                                            <div onClick={() => (this.currentUploadIndex = index)}>
                                                <ElIcon class="avatar-uploader-icon">
                                                    <IEpPlus />
                                                </ElIcon>
                                            </div>
                                        )}
                                    </ElUpload> */}

                                    {!item.categoryImg ? (
                                        <div
                                            class="selectMaterialStyle"
                                            onClick={() => {
                                                this.dialogVisible = true
                                                this.parameterId = index
                                            }}
                                        >
                                            <span class="selectMaterialStyle__span">+</span>
                                        </div>
                                    ) : (
                                        <img
                                            src={item.categoryImg}
                                            alt=""
                                            class="selectMaterialStyle"
                                            onClick={() => {
                                                this.dialogVisible = true
                                                this.parameterId = index
                                            }}
                                        />
                                    )}
                                </ElFormItem>
                            </div>
                        ))
                    ) : this.radioCategory === 1 ? (
                        <ElFormItem label="二级类目" prop={this.radioCategory === 1 ? '' : 'sLevelSelect'}>
                            <div class="dialog">
                                <div class="dialog__header">
                                    <span class="dialog__header--category">类目名称</span>
                                    <span class="dialog__header--percent">类目扣率(%)</span>
                                    <span class="dialog__header--img">图片(84*84)</span>
                                    <span>操作</span>
                                </div>
                                {this.sLevelInputArr.map((item: any, index: number) => (
                                    <div class="dialog__content">
                                        <ElInput
                                            modelValue={item.sLevelInput}
                                            onUpdate:modelValue={(value) => (item.sLevelInput = value)}
                                            maxlength={10}
                                            showWordLimit={true}
                                            placeholder="类目名称"
                                            style="width: 120px"
                                        />
                                        <ElInputNumber
                                            modelValue={item.deductionRatio}
                                            onUpdate:modelValue={(value) => (item.deductionRatio = value)}
                                            max={100}
                                            min={0}
                                            stepStrictly
                                            step={1}
                                            placeholder="0-100"
                                            style="width: 120px"
                                        />
                                        {/* <ElUpload
                                            action="gruul-mall-carrier-pigeon/oss/upload"
                                            class="avatar-uploader"
                                            style="margin-left:60px;margin-right:10px;"
                                            httpRequest={elementUploadRequest}
                                            showFileList={false}
                                            onSuccess={(value) => (item.categoryImg = value.data)}
                                        >
                                            {item.categoryImg ? (
                                                <img src={item.categoryImg} class="avatar" />
                                            ) : (
                                                <div onClick={() => (this.currentUploadIndex = index)}>
                                                    <ElIcon class="avatar-uploader-icon">
                                                        <IEpPlus />
                                                    </ElIcon>
                                                </div>
                                            )}
                                        </ElUpload> */}

                                        {!item.categoryImg ? (
                                            <div
                                                class="selectMaterialStyle"
                                                onClick={() => {
                                                    this.dialogVisible = true
                                                    this.parameterId = index
                                                }}
                                            >
                                                <span class="selectMaterialStyle__span">+</span>
                                            </div>
                                        ) : (
                                            <img
                                                src={item.categoryImg}
                                                alt=""
                                                class="selectMaterialStyle"
                                                onClick={() => {
                                                    this.dialogVisible = true
                                                    this.parameterId = index
                                                }}
                                            />
                                        )}

                                        {!this.isEdit && (
                                            <div class="dialog__content--tool">
                                                <div class="add-btn" onClick={() => this.sLevelInputArr.push({ sLevelInput: '', deductionRatio: 0 })}>
                                                    新增
                                                </div>
                                                <div class="del-btn" onClick={() => handleDelSecond(index)}>
                                                    删除
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </ElFormItem>
                    ) : this.radioCategory === 2 ? (
                        <ElRow gutter={8}>
                            <ElCol span={14}>
                                <ElFormItem label="二级类目">
                                    <ElInput
                                        modelValue={this.selectLevel.secondArr?.find((item: any) => item.id === this.submitForm.sLevelSelect)?.name}
                                        disabled
                                    />
                                </ElFormItem>
                            </ElCol>
                            <ElCol span={10}>
                                <ElFormItem label="类目扣率(%)">
                                    <ElInput modelValue={this.submitForm.deductionRatio} disabled />
                                </ElFormItem>
                            </ElCol>
                        </ElRow>
                    ) : null}
                    {this.radioCategory === 2 && !this.isEdit ? (
                        <ElFormItem label="三级类目" prop="tLevelTable">
                            <div class="dialog">
                                <div class="dialog__header">
                                    <span class="dialog__header--name">下级类目名称</span>
                                    <span class="dialog__header--img">图片(84*84)</span>
                                    <span>操作</span>
                                </div>
                                {this.submitForm.tLevelTable?.map((item: any, index: number) => (
                                    <div class="dialog__content">
                                        <ElInput
                                            modelValue={item.name}
                                            onUpdate:modelValue={(value) => (item.name = value.trim())}
                                            maxlength={10}
                                            showWordLimit={true}
                                            placeholder="类目名称"
                                            style="width: 180px"
                                        />
                                        {/* <ElUpload
                                            action="gruul-mall-carrier-pigeon/oss/upload"
                                            class="avatar-uploader"
                                            httpRequest={elementUploadRequest}
                                            showFileList={false}
                                            onSuccess={(res) => (this.submitForm.tLevelTable[this.currentUploadIndex].categoryImg = res.data)}
                                        >
                                            {item.categoryImg ? (
                                                <img src={item.categoryImg} class="avatar" />
                                            ) : (
                                                <div onClick={() => (this.currentUploadIndex = index)}>
                                                    <ElIcon class="avatar-uploader-icon">
                                                        <IEpPlus />
                                                    </ElIcon>
                                                </div>
                                            )}
                                        </ElUpload> */}

                                        {!item.categoryImg ? (
                                            <div
                                                class="selectMaterialStyle"
                                                onClick={() => {
                                                    this.dialogVisible = true
                                                    this.parameterIds = index
                                                }}
                                            >
                                                <span class="selectMaterialStyle__span">+</span>
                                            </div>
                                        ) : (
                                            <img
                                                src={item.categoryImg}
                                                alt=""
                                                class="selectMaterialStyle"
                                                onClick={() => {
                                                    this.dialogVisible = true
                                                    this.parameterIds = index
                                                }}
                                            />
                                        )}

                                        {!this.isEdit && (
                                            <div class="dialog__content--tool">
                                                <div class="add-btn" onClick={() => this.submitForm.tLevelTable.push({ name: '', categoryImg: '' })}>
                                                    新增
                                                </div>
                                                <div class="del-btn" onClick={() => handleDeleteThird(index)}>
                                                    删除
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </ElFormItem>
                    ) : (
                        this.radioCategory === 2 && (
                            <ElRow gutter={8}>
                                {this.submitForm.tLevelTable?.map((item: any, index: number) => (
                                    <ElCol span={24}>
                                        <ElFormItem label="三级类目" prop="tLevelTable">
                                            <div>
                                                {this.submitForm.tLevelTable?.map((item: any, index: number) => (
                                                    <ElInput
                                                        modelValue={item.name}
                                                        onUpdate:modelValue={(value) => (item.name = value.trim())}
                                                        maxlength={10}
                                                        showWordLimit={true}
                                                        placeholder="最多输入4个字"
                                                        style="width: 180px"
                                                    />
                                                ))}
                                            </div>
                                        </ElFormItem>
                                        <ElFormItem label="图片(84*84)">
                                            <img
                                                src={item.categoryImg}
                                                alt=""
                                                class="selectMaterialStyle"
                                                onClick={() => {
                                                    this.dialogVisible = true
                                                    this.parameterIds = index
                                                    this.parameterId = index
                                                }}
                                            />
                                        </ElFormItem>
                                    </ElCol>
                                ))}
                            </ElRow>
                        )
                    )}
                </ElForm>
                <SelectMaterial
                    uploadFiles={1}
                    dialogVisible={this.dialogVisible}
                    onSelectMaterialFn={(val: boolean) => {
                        this.dialogVisible = val
                        this.parameterId = null
                        this.parameterIds = null
                    }}
                    onCroppedFileChange={(val: string) => {
                        if (this.parameterId !== null) this.sLevelInputArr[this.parameterId].categoryImg = val[0]
                        if (this.parameterIds !== null) this.submitForm.tLevelTable[this.parameterIds].categoryImg = val[0]
                    }}
                    onCheckedFileLists={(val: string) => {
                        if (this.parameterId !== null) this.sLevelInputArr[this.parameterId].categoryImg = val[0]
                        if (this.parameterIds !== null) this.submitForm.tLevelTable[this.parameterIds].categoryImg = val[0]
                    }}
                />
            </>
        )
    },
})

export default CategoryForm
