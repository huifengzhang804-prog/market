export interface Default {
    img: string
    title: string
    type: string
    link: any
    id: number
}

export const getDefault = (): Default => ({
    img: '',
    title: '',
    type: '',
    link: '',
    id: Date.now(),
})

export default [getDefault()]
