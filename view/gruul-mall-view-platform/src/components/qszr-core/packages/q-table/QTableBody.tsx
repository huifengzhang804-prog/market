import type { PropType, VNode } from 'vue'
import { ElCheckbox } from 'element-plus'
import QTableRow from './QTableRow'
import { ColumnsType } from './table'
import '../styles/m-table.scss'
import noData from '@/assets/image/no_data.png'

const buildProps = () => ({
    data: {
        type: Array as PropType<any[]>,
        default() {
            return []
        },
    },
    selection: {
        type: Boolean,
        default: false,
    },
    headerSelection: {
        type: Boolean,
        default: false,
    },
    custom: {
        type: Boolean,
        default: false,
    },
    needBorder: {
        type: Boolean,
        default: false,
    },
    noBorder: {
        type: Boolean,
        default: false,
    },
    columns: {
        type: Array as PropType<ColumnsType[]>,
        default() {
            return []
        },
    },
    tableHeaderClass: {
        type: String,
        default: '',
    },
    rowHeaderClass: {
        type: String,
        default: '',
    },
    needHoverBorder: {
        type: Boolean,
        default: false,
    },
    /*多行字段key*/
    multipleKey: {
        type: String,
        default: '',
    },
    columnKey: {
        type: String,
        default: '',
    },
})
export default defineComponent({
    props: buildProps(),
    emits: ['check', 'sort'],
    setup(prop, { slots, emit }) {
        const tableData = ref<any[]>([])
        const checkAll = computed({
            get: () => {
                return Boolean(tableData.value.length && tableData.value.every((item) => item.checked))
            },
            set: (v) => {
                onCheckAllChange(v)
                emit('check', getCheckedItem())
            },
        })
        const isIndeterminate = computed(() => {
            const len = tableData.value.filter((item) => item.checked).length
            return len !== 0 && len !== tableData.value.length
        })

        watch(
            prop,
            (value) => {
                tableData.value = value.data
            },
            {
                immediate: true,
            },
        )

        /**
         * 设置组件propdata
         * @param {VNode} component
         * @param {any} data
         */
        const setPropData = (component: VNode, data: any) => {
            Object.assign(component.props ? component.props : {}, data)
        }
        /**
         * 单选
         * @param {boolean} checked
         * @param {number} index
         */
        const onItemCheckChange = (checked = false, index: number) => {
            Object.assign(tableData.value[index], { checked: !checked })
            emit('check', getCheckedItem())
        }
        /**
         * 全选
         * @param {boolean} v
         */
        function onCheckAllChange(v: boolean | number) {
            tableData.value = tableData.value.map((item) => {
                item.checked = v
                return item
            })
        }
        /**
         * 排序
         * @param {boolean} v
         */
        function onChangeSort(label: string | boolean) {
            emit('sort', label)
        }
        /**
         * 获取选中的条目
         */
        function getCheckedItem() {
            return tableData.value.filter((item) => item.checked)
        }
        /**
         * 清除所有选项
         */
        function clearCheckedItem() {
            tableData.value = tableData.value.map((item) => {
                item.checked = false
                return item
            })
        }
        // defineExpose({
        //     clearCheckedItem,
        // })
        return () => (
            <table class={['m__table']} cellpadding="0" cellspacing="0">
                <colgroup>
                    {prop.columns.map((item) => {
                        return <col width={item.width as string | number}></col>
                    })}
                </colgroup>
                <thead class={['m__table--head', prop.tableHeaderClass, slots.header && 'padding']}>
                    <tr class={'m__tr'}>
                        {prop.columns.map((item, i) => {
                            return (
                                <th
                                    style={item.customStyle as string}
                                    class={[
                                        ...(i === 0 && (prop.selection || prop.headerSelection) ? ['m__table--center'] : []),
                                        item.fixed === 'right' ? 'fixed--right' : '',
                                    ]}
                                    colspan="1"
                                    rowspan="1"
                                >
                                    {i === 0 && (prop.selection || prop.headerSelection) && (
                                        <el-checkbox
                                            indeterminate={isIndeterminate.value}
                                            vModel={checkAll.value}
                                            onChange={onCheckAllChange.bind(this, checkAll.value)}
                                        ></el-checkbox>
                                    )}
                                    <div class={['m__table--shrink']}>
                                        {item.label}
                                        {item.sort && (
                                            <i
                                                class="iconfont icon-path-1 cup"
                                                style="position: relative;color:#333;font-size:12px;left: 3px;"
                                                onClick={onChangeSort.bind(this, item.label)}
                                            ></i>
                                        )}
                                    </div>
                                </th>
                            )
                        })}
                    </tr>
                </thead>

                {!tableData.value.length ? (
                    <tbody class="m__table--empty">
                        {slots.noData ? (
                            <td class="empty__td" colspan={prop.columns.length} align="center">
                                {slots.noData()}
                            </td>
                        ) : (
                            <td class="empty__td" colspan={prop.columns.length} align="center">
                                <div class="no_data">
                                    <img class="img" src={noData} alt="" />
                                    <p class="cont">暂无数据</p>
                                </div>
                            </td>
                        )}
                    </tbody>
                ) : (
                    tableData.value.map((row, index) => {
                        return (
                            <tbody
                                class={[
                                    'm__table--body',
                                    prop.custom ? 'custom' : 'default',
                                    prop.needBorder && 'need--border',
                                    prop.noBorder && 'no--border',
                                    prop.needHoverBorder && row.close ? 'hover--class' : 'ordinary--class',
                                ]}
                            >
                                {slots.header && (
                                    <tr>
                                        <td colspan={prop.columns.length}>
                                            <div class={['body--header', prop.rowHeaderClass, { close: row.close }]}>
                                                {(prop.selection || prop.headerSelection) && (
                                                    <ElCheckbox
                                                        key={row.checked}
                                                        checked={row.checked}
                                                        onChange={onItemCheckChange.bind(this, row.checked, index)}
                                                    ></ElCheckbox>
                                                )}
                                                {slots.header({ row, index })}
                                            </div>
                                        </td>
                                    </tr>
                                )}
                                {slots.custom ? (
                                    slots.custom({ row, index })
                                ) : (
                                    <QTableRow>
                                        {slots.default &&
                                            slots.default().map((child, i) => {
                                                // 设置tableColumn的props
                                                setPropData(child, {
                                                    row,
                                                    index,
                                                    columnKey: prop.columnKey,
                                                    hasSlots: Boolean(child.children),
                                                })
                                                return (
                                                    <td
                                                        class={[
                                                            'm__table--item',
                                                            !child && 'hide',
                                                            child?.props?.fixed === 'right' ? 'fixed--right' : '',
                                                        ]}
                                                    >
                                                        <div class={['selection__checkbox', prop.selection && i === 0 && 'selection']}>
                                                            {i === 0 && prop.selection && (
                                                                <ElCheckbox
                                                                    key={row.checked}
                                                                    checked={row.checked}
                                                                    onChange={onItemCheckChange.bind(this, row.checked, index)}
                                                                ></ElCheckbox>
                                                            )}
                                                            <div class={['m__table--shrink']}>{child}</div>
                                                        </div>
                                                    </td>
                                                )
                                            })}
                                    </QTableRow>
                                )}
                            </tbody>
                        )
                    })
                )}
            </table>
        )
    },
})
