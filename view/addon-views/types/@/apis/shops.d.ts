import { R } from "@/apis/http";

declare module "@/apis/shops" {
  export const doReAudit: ({ shopId }: { shopId: string }) => Promise<any>;
  export const doShopAudit: (data: any) => Promise<R<unknown>>;
  export const doGetShopRejectReason: (data: any) => Promise<R<any>>;
  export const doDelShop: (params: string[]) => Promise<R<unknown>>;
  export const doChangeStatus: (
    params: string[],
    isEnable: boolean
  ) => Promise<R<unknown>>;
  export const doGetShopList: (params: any) => Promise<any>;
  export const storeStatusList: (data: any) => Promise<any>;
  export const doGetCategory: (params: any) => Promise<R<any>>;
}
