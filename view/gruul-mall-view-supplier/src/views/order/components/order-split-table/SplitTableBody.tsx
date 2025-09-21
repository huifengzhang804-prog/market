import { PropType, VNode } from 'vue'
import { ColumnsType } from './table'
import type { ApiOrder } from '@/views/order/types/order'
import { ShopOrderItem } from '@/views/order/types/order'

const buildProps = () => ({
    data: {
        type: Array as PropType<ApiOrder[]>,
        default() {
            return []
        },
    },
    selection: {
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
    headerSelection: {
        type: Boolean,
        default: false,
    },
})
export default defineComponent({
    props: buildProps(),
    emits: ['check'],
    setup(prop, { slots, emit }) {
        const records = ref<ApiOrder[]>([])
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

        const tableData = computed({
            get: () => {
                const data = records.value
                if (!data) return []
                return itemsToPackageMap(data)
            },
            set(newVal) {},
        })

        /**
         * 根据包裹id进行分组
         * @param orders
         */
        const itemsToPackageMap = (orders: ApiOrder[]) => {
            if (!orders) return []
            orders.forEach((order) => {
                const shopOrder = order.shopOrders?.[0]
                if (shopOrder) {
                    const shopOrderItems = shopOrder?.shopOrderItems || []
                    shopOrder.packageMap = shopOrderItems?.reduce((map, currentItem) => {
                        // const packageId = currentItem.packageId
                        // let currentPackageItems = map.get(packageId)
                        // if (!currentPackageItems) {
                        //     currentPackageItems = []
                        //     map.set(packageId, currentPackageItems)
                        // }
                        // currentPackageItems.push(currentItem)
                        // return map
                        const key = `${currentItem.productId}-${currentItem.skuId}`
                        if (map.has(key)) {
                            map.get(key)?.push(currentItem)
                        } else {
                            map.set(key, [currentItem])
                        }
                        return map
                    }, new Map<string | undefined, ShopOrderItem[]>())
                }
            })
            return orders
        }
        watch(
            prop,
            (value) => {
                records.value = value.data
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
            Object.assign(tableData.value[index], { checked })
            emit('check', getCheckedItem())
        }
        /**
         * 全选
         * @param {boolean} v
         */
        function onCheckAllChange(v: boolean) {
            tableData.value = tableData.value.map((item) => {
                item.checked = v
                return item
            })
            emit('check', getCheckedItem())
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

        const packageMapToDom = (row: ApiOrder, index: number): any[] => {
            const tds: any[] = []
            const packageMap = (row.shopOrders?.[0].packageMap as Map<string | undefined, ShopOrderItem[]>) || []

            let isFirstDom = true
            packageMap.forEach((items, key) => {
                tds.push(
                    <tr class="body--content">
                        {slots.default &&
                            slots.default().map((child, i) => {
                                // 设置tableColumn的props
                                setPropData(child, {
                                    row,
                                    packageId: key,
                                    shopOrderItems: items,
                                })
                                const isMixed = child.props?.['is-mixed']
                                if ((isMixed && isFirstDom) || !isMixed) {
                                    return (
                                        <td
                                            class={['o_table--item', !child && 'hide', child?.props?.fixed === 'right' ? 'fixed--right' : '']}
                                            rowspan={isMixed ? packageMap.size : undefined}
                                        >
                                            <div class={['selection__checkbox', prop.selection && i === 0 && 'selection']}>
                                                <div class={['o_table--shrink']}>{child}</div>
                                            </div>
                                        </td>
                                    )
                                }
                                return null
                            })}
                    </tr>,
                )
                isFirstDom = false
            })
            return tds
        }
        return () => (
            <table class={['o_table']} cellpadding="0" cellspacing="0">
                <colgroup>
                    {prop.columns.map((item) => {
                        return <col width={item.width}></col>
                    })}
                </colgroup>
                <thead class={['o_table--head', prop.tableHeaderClass, slots.header && 'padding']}>
                    <tr class={'m__tr'}>
                        {prop.columns.map((item, i) => {
                            return (
                                <th
                                    style={item.customStyle}
                                    class={[
                                        ...(i === 0 && (prop.selection || prop.headerSelection) ? ['o_table--center'] : []),
                                        item.fixed === 'right' ? 'fixed--right' : '',
                                    ]}
                                >
                                    {i === 0 && (prop.selection || prop.headerSelection) && (
                                        <el-checkbox
                                            indeterminate={isIndeterminate.value}
                                            vModel={checkAll.value}
                                            onChange={onCheckAllChange.bind(this, checkAll.value)}
                                        ></el-checkbox>
                                    )}
                                    <div class={['o_table--shrink']}>{item.label}</div>
                                </th>
                            )
                        })}
                    </tr>
                </thead>

                {!tableData.value.length ? (
                    <tbody class="o_table--empty">
                        <tr>
                            {slots.noData ? (
                                <td class="empty__td" colspan={prop.columns.length} align="center">
                                    {slots.noData()}
                                </td>
                            ) : (
                                <td class="empty__td" colspan={prop.columns.length} align="center">
                                    暂无数据~
                                </td>
                            )}
                        </tr>
                    </tbody>
                ) : (
                    tableData.value.map((row, index) => {
                        return (
                            <tbody
                                class={[
                                    'o_table--body',
                                    prop.custom ? 'custom' : 'default',
                                    prop.needBorder && 'need--border',
                                    prop.needHoverBorder && row.close ? 'hover--class' : 'ordinary--class',
                                ]}
                            >
                                {slots.header && (
                                    <tr>
                                        <td colspan={prop.columns.length}>
                                            <div class={['body--header', prop.rowHeaderClass, { close: row.close }]}>
                                                {prop.headerSelection && (
                                                    <el-checkbox
                                                        class="header--checkbox"
                                                        vModel={row.checked}
                                                        onChange={onItemCheckChange.bind(this, row.checked, index)}
                                                    ></el-checkbox>
                                                )}
                                                {slots.header({ row, index })}
                                            </div>
                                        </td>
                                    </tr>
                                )}
                                {/* 订单拆分展示 */}
                                {packageMapToDom(row, index)}
                            </tbody>
                        )
                    })
                )}
            </table>
        )
    },
})
