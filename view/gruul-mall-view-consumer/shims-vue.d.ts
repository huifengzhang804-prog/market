/// <reference types="vite/client" />
/// <reference types="vitest/globals" />

declare module '*.nvue' {
  import { defineComponent } from 'vue'
  const component: ReturnType<typeof defineComponent>
  export default component
}
