/**
 * 客户端类型
 */
export enum ClientType {
  /**
   * 平台控制台
   */
  PLATFORM_CONSOLE = "PLATFORM_CONSOLE",
  /**
   * 商户控制台
   */
  SHOP_CONSOLE = "SHOP_CONSOLE",
}

/**
 * 菜单类型
 */
export enum MenuType {
  /**
   * 目录
   */
  CATALOG = "CATALOG",
  /**
   * 菜单
   */
  MENU = "MENU",
}

/**
 * 菜单树
 */
export interface MenuTree {
  id: string | null;
  clientType: ClientType;
  parentId: string;
  perm: string | null;
  name: string;
  type: MenuType;
  order: number;
  path: string | null;
  unshared: boolean;
  icon: string | null | undefined;
  children: MenuTree[];
}
