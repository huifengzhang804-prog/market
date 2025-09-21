/// <reference types="vite/client" />

export {}
declare global {
    type FN = (...arg: any[]) => void
    type Fn = () => void
    type Long = number | string
    type Obj = Record<string, any>
}
