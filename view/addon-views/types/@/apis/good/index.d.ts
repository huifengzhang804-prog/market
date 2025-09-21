import type { RetrieveParam, ApiRetrieve } from "@/apis/good/model";
import { L, Long } from "@/utils/types";

declare module "@/apis/good" {
  /**
   * 响应数据格式
   */
  export type R<T> = {
    /**
     * 响应码
     */
    code: number;
    /**
     * 响应提示信息
     */
    msg: string;
    /**
     * 响应数据
     */
    data: T;
    success: boolean;
  };
  export const doGetProductSkus: (id: any) => Promise<any>;
  export const doGetRetrieveProduct: (
    retrieveParams: Partial<RetrieveParam>
  ) => Promise<R<ApiRetrieve>>;
  export const doPostShopInfo: (shopIds: string[]) => Promise<R<unknown>>;
  export const getAllRegionList: (data: any) => Promise<R<unknown>>;
  export const getAllCategory: (data: any) => Promise<R<unknown>>;
  export const getProList: (data: any) => Promise<R<unknown>>;
  export const doGetProductList: (params: any) => Promise<R<L<ProductItem>>>;
  export const doUpdateSellStatus: (
    ids: any,
    status: string
  ) => Promise<R<unknown>>;
  export const doGetSingleCommodity: (
    id: any,
    shopId: string
  ) => Promise<R<unknown>>;
  export const doGetCommoditySku: (
    shopId: string,
    productId: any
  ) => Promise<R<unknown>>;
  export const doGetCategory: (params: any) => Promise<any>;
  export const doGetHighestCategoryLevel: (params: any) => Promise<any>;
  export const getAttsList: (params: any) => Promise<any>;
  export const doGetSupplierList: (params: any) => Promise<any>;
  export const doUpdateSupplierSellStatus: (
    data: any,
    status: string
  ) => Promise<any>;
  export const doGetSeachSupplierSearchList: (params: any) => Promise<any>;
  export const doGetCommodityDetails: (id: any, params: any) => Promise<any>;
  export const doGetSupplierCommodityDetails: (
    id: any,
    params: any
  ) => Promise<any>;
  export const doGetShopExamineGoods: (params: any) => Promise<any>;
  export const doPutShopExamineGoods: (
    status: string,
    data: any
  ) => Promise<any>;
  export const doGetSupplierExamineGoods: (params: any) => Promise<any>;
  export const doPutSupplierExamineGoods: (
    status: string,
    data: any
  ) => Promise<any>;
  export const doPostSupplierRestoreSale: (
    data: DataType
  ) => Promise<R<unknown>>;
}

export type { Long };

export interface DataType {
  shopId: string;
  productId: string;
}
export interface ProductItem {
  createTime: string;
  extra: Extra;
  id: string;
  name: string;
  pic: string;
  productStatus: string;
  saleDescribe: string;
  sellType: string;
  shopId: string;
  shopName: string;
  storageSkus: StorageSkus[];
}

export interface Extra {
  auditTime: string;
  customDeductionRatio: number;
  platformCategory: PlatformCategory;
  productAttributes: any[];
  productParameters: any[];
  shopCategory: ShopCategory;
  submitTime: string;
}

export interface PlatformCategory {
  one: Long;
  three: Long;
  two: Long;
}

export interface ShopCategory {
  one: Long;
  three: Long;
  two: Long;
}

export interface StorageSkus {
  activityId: number;
  activityType: string;
  createTime: string;
  deleted: boolean;
  id: string;
  image: string;
  initSalesVolume: number;
  limitNum: number;
  limitType: string;
  minimumPurchase: number;
  price: number;
  productId: string;
  salePrice: number;
  salesVolume: number;
  shopId: string;
  sort: number;
  specs: string[];
  stock: number;
  stockType: string;
  updateTime: string;
  version: number;
  weight: number;
}
