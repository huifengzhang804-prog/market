import { resolve } from 'path'
import modifyAppId from './modify-appId'
import modifyLiveId from './modify-live-id'
import modifyMapKey from './modify-map-key'
import { loadEnv } from 'vite'
import fs from 'fs'
import prettier from 'prettier'

export default function modifyAssemble(mode: string) {
  return () => {
    const baseUrl = process.cwd()
    const platform = process.env.UNI_PLATFORM
    const pagesPath = resolve('./src/pages.json')
    const environmentObj = loadEnv(mode, baseUrl) as unknown as ImportMetaEnv
    if (platform === 'mp-weixin') {
      // 修改 appid
      modifyAppId(environmentObj)
      // 修改 wx 直播 id
      modifyLiveId(pagesPath, environmentObj)
    }
    // 修改 mapkey
    modifyMapKey(environmentObj)
    // 使用 prettier 格式化 manifest.json
    formatManifest()
  }
}

async function formatManifest() {
  const manifestPath = resolve(__dirname, '../manifest.json')
  const manifest = fs.readFileSync(manifestPath, 'utf-8')
  const formattedManifest = await prettier.format(manifest, {
    parser: 'json',
  })
  fs.writeFileSync(manifestPath, formattedManifest, {
    encoding: 'utf-8',
  })
}
