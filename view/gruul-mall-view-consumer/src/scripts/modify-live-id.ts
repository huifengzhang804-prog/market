import fs from 'fs'
import { stripJsonComments } from '../utils/jsonUtils'
import prettier from 'prettier'

export default async function replaceLiveAppId(pagesPath: string, environmentObj: ImportMetaEnv) {
  const pageStr = fs.readFileSync(pagesPath, { encoding: 'utf-8' })
  const cleanJsonContent = stripJsonComments(pageStr)
  const pagesObject = JSON.parse(cleanJsonContent)

  pagesObject.subPackages.map((item: any) => {
    if (item.plugins && item.plugins['live-player-plugin']) {
      item.plugins['live-player-plugin'] = {
        version: environmentObj.VITE_WX_LIVE_VERSION,
        provider: environmentObj.VITE_WX_LIVE_ID,
      }
    }
    return item
  })

  // 使用 Prettier 格式化 JSON
  const prettierConfig = await prettier.resolveConfig(process.cwd())
  const formattedJson = await prettier.format(JSON.stringify(pagesObject), {
    ...prettierConfig,
    parser: 'json',
  })

  fs.writeFileSync(pagesPath, formattedJson, {
    flag: 'w',
  })
}
