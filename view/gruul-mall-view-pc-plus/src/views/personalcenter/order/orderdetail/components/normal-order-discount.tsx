import { PropType } from 'vue'
import { DiscountDataByType } from '../types'
import useConvert from '@/composables/useConvert'
const { divTenThousand } = useConvert()

export default defineComponent({
  props: {
    discountDataByType: {
      type: Object as PropType<DiscountDataByType>,
      default: () => ({}),
    },
  },
  setup(props, ctx) {
    const totalDiscountDataCount = computed(() => {
      return divTenThousand(
        props.discountDataByType?.SHOP_COUPON.price
          .add(props.discountDataByType?.MEMBER_DEDUCTION.price)
          .add(props.discountDataByType.PLATFORM_COUPON.price)
          .add(props.discountDataByType.CONSUMPTION_REBATE.price)
          .add(props.discountDataByType?.FULL_REDUCTION.price),
      )
    })
    return () => (
      <>
        {Object.keys(props.discountDataByType).map(
          (key) =>
            props.discountDataByType[key as keyof DiscountDataByType].price.toString() !== '0' && (
              <div class="flex justify-end c-mb-9">
                <div>{props.discountDataByType[key as keyof DiscountDataByType].name}：</div>
                <div class="c-w-96 flex justify-end">
                  ￥{divTenThousand(props.discountDataByType[key as keyof DiscountDataByType].price).toString()}
                </div>
              </div>
            ),
        )}
        <div class="flex justify-end c-mb-9">
          <div>总优惠：</div>
          <div class="c-w-96 flex justify-end">-￥{totalDiscountDataCount.value.toFixed(2)}</div>
        </div>
      </>
    )
  },
})
