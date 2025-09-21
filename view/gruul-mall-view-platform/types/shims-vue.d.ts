declare module '*.vue' {
    import { defineComponent } from 'vue'
    const component: ReturnType<typeof defineComponent>
    export default component
}

declare namespace JSX {
    interface IntrinsicAttributes {
        class?: any
        style?: any
    }
}
declare module '*.mjs' {
    const mjs: any
    export default mjs
}
