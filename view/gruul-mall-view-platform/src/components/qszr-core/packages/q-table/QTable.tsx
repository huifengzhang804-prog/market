import type { PropType } from 'vue'
import QTableBody from './QTableBody'
import '../styles/m-table.scss'
import { ColumnsType, MTableSlotsType } from './table'
import { cloneDeep } from 'lodash-es'
const buildProps = () => ({
    data: {
        type: Array as PropType<any[]>,
        default() {
            return []
        },
    },
    columnKey: { type: String, default: '' },
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
})
export default defineComponent({
    props: buildProps(),
    emits: ['update:checkedItem', 'changeSort'],
    setup(prop, { slots, emit }) {
        const tableRef = ref()
        const tableData = ref<any[]>()
        const tableContainerRef = ref<HTMLDivElement>()
        const isScrolling = ref<'left' | 'right' | 'middle' | 'none'>('left')
        nextTick(() => {
            if (tableContainerRef.value!.scrollWidth <= tableContainerRef.value!.clientWidth) {
                isScrolling.value = 'none'
            }
            tableContainerRef.value?.addEventListener('scroll', (e) => {
                const { scrollLeft, clientWidth, scrollWidth } = e.target as HTMLElement
                if (scrollWidth > clientWidth) {
                    // 出现横向滚动条
                    if (scrollLeft === 0) {
                        // 滚动到左侧
                        isScrolling.value = 'left'
                    }
                    if (Math.ceil(scrollLeft + clientWidth) >= scrollWidth) {
                        // 滚动到右侧
                        isScrolling.value = 'right'
                    } else {
                        // 滚动到中间
                        isScrolling.value = 'middle'
                    }
                } else {
                    isScrolling.value = 'none'
                }
            })
        })

        /**
         * 获取插槽表格
         */
        const getColumns = () => {
            return (
                (prop.columns.length > 0 && prop.columns) ||
                ((
                    slots.default &&
                    slots.default().map((component) => {
                        const props = component.props
                        // 筛除q-table-column 做V-IF判断节点
                        if (props) {
                            return { ...props }
                        } else {
                            return false
                        }
                    })
                )?.filter(Boolean) as ColumnsType[])
            )
        }
        /**调用QTableBody中的方法 */
        const getCheckedItem = () => {
            // return tableRef.getCheckedItem();
        }
        /**组件上使用v-model绑定checkedItem */
        const onCheck = (v: any) => {
            emit('update:checkedItem', v)
        }

        /**组件上使用排序 */
        function onSort(v: any) {
            emit('changeSort', v)
        }

        const MTableSlotsHandle = () => {
            const MTableSlots: MTableSlotsType = {
                // 筛选出V-if隐藏
                default: () => {
                    if (slots.default) {
                        return slots.default().filter((item) => {
                            return item.props !== null
                        })
                    }
                },
            }
            if (!slots.default && !prop.columns.length) {
                throw new Error('请传入MTableColumn')
            } else if (slots.header) {
                MTableSlots.header = (row: any) => slots.header && (slots.header(row) as any)
            } else if (slots.custom) {
                MTableSlots.custom = (row: any) => slots.custom && (slots.custom(row) as any)
            } else if (slots.noData) {
                MTableSlots.noData = () => slots.noData && (slots.noData() as any)
            }
            return MTableSlots
        }
        watch(
            prop,
            (val: any) => {
                tableData.value = cloneDeep(val.data)
                nextTick(() => {
                    if (tableContainerRef.value) {
                        tableContainerRef.value.scrollTop = 0
                    }
                })
            },
            {
                immediate: true,
            },
        )
        // defineExpose({
        //     tableRef,
        // })
        return () => (
            <div class={['m__table--container', `is-scrolling--${isScrolling.value}`]} ref={tableContainerRef as any}>
                <QTableBody
                    columnKey={prop.columnKey}
                    onCheck={onCheck}
                    onSort={onSort}
                    columns={getColumns()}
                    data={tableData.value}
                    custom={prop.custom}
                    selection={prop.selection}
                    headerSelection={prop.headerSelection}
                    tableHeaderClass={prop.tableHeaderClass}
                    rowHeaderClass={prop.rowHeaderClass}
                    needHoverBorder={prop.needHoverBorder}
                    multipleKey={prop.multipleKey}
                    needBorder={prop.needBorder}
                    noBorder={prop.noBorder}
                    v-slots={MTableSlotsHandle()}
                    ref={tableRef as any}
                ></QTableBody>
            </div>
        )
    },
})
