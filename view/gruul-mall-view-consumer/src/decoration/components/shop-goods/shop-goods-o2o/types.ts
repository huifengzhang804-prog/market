export interface ApiSearchShop {
  [key: string]: any
  id: string
  name: string
  logo: string
  newTips: string
  status: 'REJECT' | 'FORBIDDEN' | 'UNDER_REVIEW' | 'NORMAL'
  headBackground: string
  distance: any
  salesVolume: number
  serializablePointVO: { x: number; y: number }
  address: string
  shopType: 'SELF_OWNED' | 'PREFERRED' | 'ORDINARY'
  drawPercentage: number
  mode: 'B2C' | 'B2B2C' | 'B2B' | 'S2B2C' | 'O2O'
  goodsList: any[]
  couponList: any[]
}
