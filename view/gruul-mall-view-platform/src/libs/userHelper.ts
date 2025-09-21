// @ts-ignore
import avatarDefault from '@/assets/image/avatar/avatar.webp'

/**
 * 获取用户昵称 用户昵称为空 则取id后六位做用户昵称
 * @param userId 用户id
 * @param nickname 用户昵称
 * @return 用户昵称
 */
export const getUserNickname = (userId: string, nickname: string) => {
    return nickname ? nickname : '用户' + userId?.slice(-6)
}

/**
 * 获取用户头像 为空则使用默认头像
 * @param avatar
 * @return 用户头像
 */
export const getUserAvatar = (avatar: string) => {
    return avatar ? avatar : avatarDefault
}
