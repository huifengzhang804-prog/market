/*
 * 自动转换 link 标签 preload 预加载
 */
import type { Plugin } from 'vite'

export function argvExist(argv: string) {
  return process.argv.includes(argv)
}

export default function myVitePluginPreload(): Plugin {
  return {
    name: 'vite-plugin-preload',
    apply(config, { command }) {
      return command === 'build' && !config?.build?.ssr
    },
    enforce: 'post',
    transformIndexHtml(html) {
      const modifiedHtmlString = html.replace(
        /<link\s+rel=('|")stylesheet('|")\s+href=('|")([^'"]*)('|")\s*\/?>/g,
        `<link rel="preload" as="style" href="$4" onload="this.onload=null;this.rel='stylesheet';" /><noscript>$&</noscript>`,
      )
      return modifiedHtmlString
    },
  }
}
