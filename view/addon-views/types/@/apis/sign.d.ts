declare module '@/apis/sign' {
  export const doGetCaptchaSlider: (mobile: string | null, smsType: string) => Promise<R<unknown>>
}
