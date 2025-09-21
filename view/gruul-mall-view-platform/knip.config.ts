import type { KnipConfig } from 'knip'

const config: KnipConfig = {
    entry: ['src/main.ts'],
    project: ['src/**/*.ts', 'src/**/*.tsx', 'src/**/*.js', 'src/**/*.jsx', '!src/**/*.d.ts', 'src/**/*.vue'],
}

export default config
