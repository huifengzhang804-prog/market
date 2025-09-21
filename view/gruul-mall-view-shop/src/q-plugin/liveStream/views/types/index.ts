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
 * 创建直播间
 * @param isFeedsPublic 开启收录
 */
export interface CreateRoom {
    name: string
    anchorName: string
    anchorWechat: string
    startTime: number
    endTime: number
    coverImg: string
    shareImg: string
    feedsImg: string
    isFeedsPublic: 0 | 1 // 开启收录
    type: 0 | 1 //默认手机直播
    closeLike: 0 | 1 //是否关闭点赞 0：开启，0|1：关闭
    closeGoods: 0 | 1 //是否关闭货架
    closeComment: 0 | 1 //是否关闭评论
    closeReplay: 0 | 1 // 是否关闭回放
    closeShare: 0 | 1 //是否关闭分享
    closeKf: 0 | 1 //是否关闭客服
    ossCoverImgUrl: string //oss背景图url
    ossShareImgUrl: string //  oss分享图url
    ossFeedsImgUrl: string //oss购物直播频道封面图url
}
/**
 * 直播间列表
 */
export interface ApiCreateRoom {
    createTime: string
    roomName: string
    anchorName: string
    wechatNumber: string
    startTime: string
    endTime: string
    coverImg: string
    shareImg: string
    feedsImg: string
    isFeedsPublic: 0 | 1 // 开启收录
    type: 0 | 1 //默认手机直播
    closeLike: 0 | 1 //是否关闭点赞 0：开启，0|1：关闭
    closeGoods: 0 | 1 //是否关闭货架
    closeComment: 0 | 1 //是否关闭评论
    closeReplay: 0 | 1 // 是否关闭回放
    closeShare: 0 | 1 //是否关闭分享
    closeKf: 0 | 1 //是否关闭客服
    id: string
    shopId: string
    status: RoomStatusJointType
    wechatRoomId: string
    wechatRoomJson: string
}
/**
 * 直播间角色列表
 */
export interface ApiRole {
    avatarUrl: string
    createTime: string
    id: string
    role: RoomRoleJointType
    shopId: string
    updateTime: string
    userName: string
    wechatNumber: string
}
