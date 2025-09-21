import { DefineComponent } from 'vue'

declare module 'qrcode-reader-vue3' {
  export interface QrcodeStreamProps {
    torch: boolean
    camera: 'auto' | 'rear' | 'off'
    track?: () => void
    onDecode?: (result: string) => void
    onInit?: (promise: Promise<void>) => void
  }
  export const QrcodeStream: DefineComponent<QrcodeStreamProps>
}
