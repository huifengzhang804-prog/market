export interface InStorageInterface {
  orderNo: string
  products: InStorageProduct[]
  supplierId: string
}

interface InStorageProduct {
  image: string
  productId: string
  productName: string
  skus: InStorageSku[]
  batchNum?: number
}

interface InStorageSku {
  num: number
  skuId: string
  specs: string[]
  used: number
  inStorageNum?: number
}
