/**
 * 直播角色
 * @param {LIVE_ADMIN} 管理员
 * @param {LIVE_ANCHOR} 主播
 * @param {LIVE_OPERATOR} 运营者
 */
enum ROOM_ROLE {
    LIVE_ADMIN,
    LIVE_ANCHOR,
    LIVE_OPERATOR,
}
export type RoomRoleJointType = keyof typeof ROOM_ROLE
/**
 * 波波间状态
 * @param {LIVE_BROADCAST} 直播中
 * @param {NOT_STARTED} 没有开始
 * @param {CLOSED} 关闭
 */
enum ROOM_STATUS {
    LIVE_BROADCAST,
    NOT_STARTED,
    CLOSED,
}
export type RoomStatusJointType = keyof typeof ROOM_STATUS

/**
 * 直播间列表
 * @param
 */
export interface ApiRoomItem {
    id: string
    endTime: string
    shopName: string
    anchorName: string
    roomName: string
    shopId: string
    startTime: string
    status: RoomStatusJointType
    type: 0 | 1 | 2
    wechatNumber: string
    wechatRoomId: string
}
