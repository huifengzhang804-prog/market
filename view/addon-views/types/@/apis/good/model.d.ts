declare module "@/apis/good/model" {
  export interface ApiRetrieveComItemType {
    createTime: string;
    id: string;
    initSalesVolume: number;
    pic: string;
    categoryFirstId?: string;
    categorySecondId?: string;
    categoryThirdId?: string;
    platformCategoryFirstId: string;
    platformCategorySecondId: string;
    platformCategoryThirdId: string;
    productId: string;
    productName: string;
    salePrices: string[];
    prices: string[];
    salesVolume: string;
    shopId: string;
    shopName: string;
    specs: string[];
    skuIds: string[];
    status: "SELL_ON" | "SELL_OFF";
    stockTypes: (keyof typeof Limit)[];
    widePic: string;
    stocks: number[];
    isCheck?: boolean;
    couponList?: { sourceDesc: string }[];
  }
  export interface RetrieveParam {
    ids: string[];
    keyword: string;
    categoryFirstId: string;
    categorySecondId: string;
    categoryThirdId: string;
    platformCategoryFirstId: string;
    platformCategorySecondId: string;
    platformCategoryThirdId: string;
    productId: string[] | string;
    pageNum: number;
    size: number;
    sort: keyof typeof RETRIEVESORT | string;
    searchTotalStockGtZero?: boolean;
    minPrice: number | string;
    maxPrice: number | string;
    current: number;
    pages: number;
    shopId: string;
    activityType: string;
    startTime: string;
    endTime: string;
    showCoupon?: boolean;
    excludeProductIds?: string[];
  }
  export enum RETRIEVESORT {
    salePrice_desc = "salePrice_desc",
    salePrice_asc = "salePrice_asc",
    salesVolume_desc = "salesVolume_desc",
    salesVolume_asc = "salesVolume_asc",
    createTime_desc = "createTime_desc",
    createTime_asc = "createTime_asc",
    comprehensive_desc = "comprehensive_desc",
    comprehensive_asc = "comprehensive_asc",
  }
  export enum Limit {
    UNLIMITED,
    PRODUCT_LIMITED,
    SKU_LIMITED,
    LIMITED,
  }

  export interface ApiRetrieve {
    list: ApiRetrieveComItemType[];
    total: number;
    pageNum: number;
    pageSize: number;
    pages: number;
    size: number;
    current: number;
  }
}
