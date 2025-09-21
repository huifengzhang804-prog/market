import type { StoreDefinition, PiniaCustomStateProperties } from "pinia";
import { Long } from "@/apis/good";
declare module "@/store/modules/shopInfo" {
  /**
   * 店铺类型
   */
  export enum ShopType {
    //自营
    SELF_OWNED = "SELF_OWNED",
    //优选
    PREFERRED = "PREFERRED",
    //普通
    ORDINARY = "ORDINARY",
  }
  /**
   * 店铺运营模式
   */
  export enum ShopMode {
    //普通店铺
    COMMON = "COMMON",
    //供应商
    SUPPLIER = "SUPPLIER",
    //o2o 店铺
    O2O = "O2O",
  }
  /**
   * 店铺状态
   */
  export enum ShopStatus {
    //审核拒绝
    REJECT = "REJECT",
    //禁用
    FORBIDDEN = "FORBIDDEN",
    //审核中
    UNDER_REVIEW = "UNDER_REVIEW",
    //正常
    NORMAL = "NORMAL",
  }
  export interface ShopInfoStore {
    id: Long;
    logo: string;
    name: string;
    newTips?: string;
    status: keyof typeof ShopStatus;
    shopMode: keyof typeof ShopMode;
    mode: keyof typeof ShopMode;
    userId: string;
    shopId: Long;
    shopType?: keyof typeof ShopType;
    token?: string;
    refresh_token?: string;
    nickname?: string;
  }
  const useShopInfoStore: StoreDefinition<
    "shopStore",
    {
      shopInfo: ShopInfoStore;
    },
    {
      getterShopInfo: (
        state: {
          shopInfo: {
            id: Long;
            logo: string;
            name: string;
            newTips?: string | undefined;
            status: keyof typeof ShopStatus;
            shopMode: keyof typeof ShopMode;
            userId: string;
            shopId: Long;
            shopType?: keyof typeof ShopType | undefined;
            token?: string | undefined;
            refresh_token?: string | undefined;
            nickname?: string | undefined;
          };
        } & PiniaCustomStateProperties<{
          shopInfo: ShopInfoStore;
        }>
      ) => ShopInfoStore;
      token: (
        state: {
          shopInfo: {
            id: Long;
            logo: string;
            name: string;
            newTips?: string | undefined;
            status: keyof typeof ShopStatus;
            shopMode: keyof typeof ShopMode;
            userId: string;
            shopId: Long;
            shopType?: keyof typeof ShopType | undefined;
            token?: string | undefined;
            refresh_token?: string | undefined;
            nickname?: string | undefined;
          };
        } & PiniaCustomStateProperties<{
          shopInfo: ShopInfoStore;
        }>
      ) => string | undefined;
      refresh_token: (
        state: {
          shopInfo: {
            id: Long;
            logo: string;
            name: string;
            newTips?: string | undefined;
            status: keyof typeof ShopStatus;
            shopMode: keyof typeof ShopMode;
            userId: string;
            shopId: Long;
            shopType?: keyof typeof ShopType | undefined;
            token?: string | undefined;
            refresh_token?: string | undefined;
            nickname?: string | undefined;
          };
        } & PiniaCustomStateProperties<{
          shopInfo: ShopInfoStore;
        }>
      ) => string | undefined;
    },
    {
      SET_SHOP_INFO(payload: ShopInfoStore): void;
      DEL_SHOP_INFO(): void;
      SET_SHOP_TOKEN(payload: {
        access_token: string;
        refresh_token: string;
      }): string;
      SET_SHOP_ADMIN_DATA(payload: any): void;
    }
  >;
}
