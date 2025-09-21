import { createUnplugin } from 'unplugin'

export const unplugin = createUnplugin((mode: string) => {
    return {
        name: 'unplugin-fixenv',
        transform(code) {
            if (code.includes('process.env.NODE_ENV')) {
                return code.replace(/globalThis\.process\.env\.NODE_ENV/, `"${mode}"`)
            }
            return code
        },
    }
})

export default unplugin.vite
