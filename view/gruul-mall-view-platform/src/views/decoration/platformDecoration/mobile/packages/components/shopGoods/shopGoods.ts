export default {
    showStyle: 'is-style-one',
    firstFew: 1,
    shopBigImg: '',
    title: '推荐店铺',
    shopInfo: [
        {
            shop: { name: '', id: '', logo: '', shopType: '', promotion: '', advertisement: '', couponList: [] as any[], o2oInfo: {} as any },
            goods: [] as any[],
        },
    ],
    validateForm: undefined as (() => Promise<boolean>) | undefined,
}
