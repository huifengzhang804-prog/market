// 添加收藏参数
interface DoAddAssessQuery {
  productId: Long
  productName: string
  productPic?: string
  productPrice: Long
  shopId: Long
  skuId?: Long
  supplierId?: Long
}
/**
 * 身份枚举
 * @params DEVELOPER = '开发者',
 * @params SUPER_ADMIN = '超级管理员',
 * @params USER = '用户',
 * @params FORBIDDEN_COMMENT = '禁止评论',
 * @params FORBIDDEN_ORDER = '禁止下单',
 * @params BLACK_LIST = '黑名单',
 * @params SUPER_CUSTOM_ADMIN = '子超级管理员',
 * @params ADMIN = '管理员',
 * @params CUSTOM_ADMIN = '子管理员',
 * @params SHOP_STORE = '店铺门店',
 * @params ANCHOR = '主播',
 */
enum ROLE_MENU_ROLE {
  DEVELOPER = 'DEVELOPER',
  SUPER_ADMIN = 'SUPER_ADMIN',
  USER = 'USER',
  FORBIDDEN_COMMENT = 'FORBIDDEN_COMMENT',
  FORBIDDEN_ORDER = 'FORBIDDEN_ORDER',
  BLACK_LIST = 'BLACK_LIST',
  SUPER_CUSTOM_ADMIN = 'SUPER_CUSTOM_ADMIN',
  ADMIN = 'ADMIN',
  CUSTOM_ADMIN = 'CUSTOM_ADMIN',
  SHOP_STORE = 'SHOP_STORE',
  ANCHOR = 'ANCHOR',
}
type RoleMenuRoleJointType = keyof typeof ROLE_MENU_ROLE
export { type DoAddAssessQuery, type RoleMenuRoleJointType }
