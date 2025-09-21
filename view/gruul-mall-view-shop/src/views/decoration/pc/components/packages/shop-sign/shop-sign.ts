export interface Default {
    newTips: boolean
    service: boolean
    follow: boolean
    img: string
    type: string
    link: any
}

const defaultData: Default = {
    newTips: false,
    service: true,
    follow: true,
    img: '',
    type: '',
    link: '',
}

export const shopType = {
    SELF_OWNED: '自营',
    PREFERRED: '优选',
    ORDINARY: '普通',
}

export default defaultData
