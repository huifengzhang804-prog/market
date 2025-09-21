import type { Address } from '@/apis/address/type'

type AddressItemType = Address
/**
 * 配送方式 此处与 '@/apis/good/model' DistributionType 强关联
 * @param MERCHANT 商家配送
 * @param EXPRESS 物流配送
 */
// eslint-disable-next-line @typescript-eslint/no-namespace
namespace ADD_RESS_TYPES {
  // 快递配送方式
  export enum DISTRIBUTION {
    EXPRESS = 'EXPRESS', //快递配送
    INTRA_CITY_DISTRIBUTION = 'INTRA_CITY_DISTRIBUTION', //同城配送
    SHOP_STORE = 'SHOP_STORE', //店铺门店
    VIRTUAL = 'VIRTUAL', //无需物流
  }
}
const EXPRESS_CODE = 50002 // 超出配送范围
const INTRA_CITY_DISTRIBUTION_CODE_1 = 60001 // 超出配送范围
const INTRA_CITY_DISTRIBUTION_CODE_2 = 60002 //不足配送金额

export { type AddressItemType, ADD_RESS_TYPES, EXPRESS_CODE, INTRA_CITY_DISTRIBUTION_CODE_1, INTRA_CITY_DISTRIBUTION_CODE_2 }
