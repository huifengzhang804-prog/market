import type { Ref } from 'vue'
import { ServiceIdsEnum, SubmitFormInterface } from '../../types'
import { cloneDeep } from 'lodash-es'

const basicServiceAssuranceList = [
    {
        name: '包邮',
        state: false,
        text: '购买该商品无需支付运费',
        enum: 'NO_FREIGHT',
    },
    {
        name: '7天退换',
        state: false,
        text: '商家承诺7天无理由退换货',
        enum: 'SEVEN_END_BACK',
    },
    {
        name: '48小时发货',
        state: false,
        text: '商家承诺订单在48小时内发布',
        enum: 'TWO_DAY_SEND',
    },
    {
        name: '假一赔十',
        state: false,
        text: '若收到商品是假冒品牌，可获得十倍赔偿',
        enum: 'FAKE_COMPENSATE',
    },
    {
        name: '正品保证',
        state: false,
        text: '商家承诺商品正品质量',
        enum: 'ALL_ENSURE',
    },
]
const useServiceAssuranceList = (submitForm: Ref<SubmitFormInterface>) => {
    const serviceAssuranceList = ref(cloneDeep(basicServiceAssuranceList))

    function updateserviceIds() {
        if (!submitForm.value) {
            submitForm.value = {} as SubmitFormInterface
        }
        submitForm.value.serviceIds = serviceAssuranceList.value.filter((item) => item.state).map((item) => item.enum as keyof typeof ServiceIdsEnum)
    }

    function initServiceAssuranceList() {
        if (!submitForm.value) {
            submitForm.value = {} as SubmitFormInterface
        }
        if (submitForm.value.serviceIds?.length) {
            serviceAssuranceList.value = cloneDeep(serviceAssuranceList.value).map((item) => {
                if (submitForm.value.serviceIds.includes(item.enum as keyof typeof ServiceIdsEnum)) {
                    item.state = true
                }
                return item
            })
        } else {
            serviceAssuranceList.value = cloneDeep(serviceAssuranceList.value).map((item) => {
                item.state = false
                return item
            })
        }
    }
    // onActivated(() => {
    //     setTimeout(() => initServiceAssuranceList())
    // })
    const resetServiceAssuranceList = () => {
        for (let index = 0; index < serviceAssuranceList.value.length; index++) {
            serviceAssuranceList.value[index].state = false
        }
    }
    return {
        serviceAssuranceList,
        updateserviceIds,
        resetServiceAssuranceList,
        initServiceAssuranceList,
    }
}

export default useServiceAssuranceList
