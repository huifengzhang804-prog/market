const fs = require('fs')
const path = require('path')
const { stripJsonComments } = require('../utils/jsonUtils')
const prettier = require('prettier')

function removeDir(dir) {
  let files = fs.readdirSync(dir)
  for (let i = 0; i < files.length; i++) {
    let newPath = path.join(dir, files[i])
    let stat = fs.statSync(newPath)
    if (stat.isDirectory()) {
      //如果是文件夹就递归下去
      removeDir(newPath)
    } else {
      //删除文件
      fs.unlinkSync(newPath)
    }
  }
  fs.rmdirSync(dir) //如果文件夹是空的，就将自己删除掉
}
// 判断文件是否存在
const exists = (filePath1) => fs.existsSync(filePath1)

const filePath = path.resolve(__dirname, '../../dist/build/mp-weixin/app.json')
const homePath = path.resolve(__dirname, '../../dist/build/mp-weixin/pages/modules/home/components/recommend/components/decorationList.json')
const filePathDev = path.resolve(__dirname, '../../dist/dev/mp-weixin/app.json')
const homePathDev = path.resolve(__dirname, '../../dist/dev/mp-weixin/pages/modules/home/components/recommend/components/decorationList.json')
const isFileExists = exists(filePath) && exists(homePath)
const isFileExistsDev = exists(filePathDev) && exists(homePathDev)

const pluginPackageLive = path.resolve(__dirname, '../../dist/build/mp-weixin/pluginPackage/live')
const pluginLive = path.resolve(__dirname, '../../dist/build/mp-weixin/pages/plugin/live')
const pluginPackageLiveDev = path.resolve(__dirname, '../../dist/dev/mp-weixin/pluginPackage/live')
const pluginLiveDev = path.resolve(__dirname, '../../dist/dev/mp-weixin/pages/plugin/live')
const isPluginExists = exists(pluginPackageLive) && exists(pluginLive)
const isPluginExistsDev = exists(pluginPackageLiveDev) && exists(pluginLiveDev)

const removeLidt = async () => {
  // 判断文件是否存在

  console.log(isFileExists, isFileExistsDev)
  if (isFileExists) {
    const fileContent = fs.readFileSync(filePath).toString()
    const data = JSON.parse(stripJsonComments(fileContent))

    const homeContent = fs.readFileSync(homePath).toString()
    const homeData = JSON.parse(stripJsonComments(homeContent))

    delete homeData.usingComponents['live-component']
    delete data.subPackages[0].plugins
    const index = data.subPackages[1].pages.findIndex((page) => page.includes('live/views/Studio'))
    data.subPackages[1].pages.splice(index, 1)

    // 使用 Prettier 格式化 JSON
    const prettierConfig = await prettier.resolveConfig(process.cwd())
    const formattedAppJson = await prettier.format(JSON.stringify(data), {
      ...prettierConfig,
      parser: 'json',
    })
    const formattedHomeJson = await prettier.format(JSON.stringify(homeData), {
      ...prettierConfig,
      parser: 'json',
    })

    fs.writeFileSync(filePath, formattedAppJson)
    fs.writeFileSync(homePath, formattedHomeJson)
    console.log('改写生产环境小程序 json 配置成功')
  }
  if (isFileExistsDev) {
    const fileContent = fs.readFileSync(filePathDev).toString()
    const data = JSON.parse(stripJsonComments(fileContent))

    const homeContent = fs.readFileSync(homePathDev).toString()
    const homeData = JSON.parse(stripJsonComments(homeContent))

    delete homeData.usingComponents['live-component']
    delete data.subPackages[0].plugins
    const index = data.subPackages[1].pages.findIndex((page) => page.includes('live/views/Studio'))
    data.subPackages[1].pages.splice(index, 1)

    // 使用 Prettier 格式化 JSON
    const prettierConfig = await prettier.resolveConfig(process.cwd())
    const formattedAppJson = await prettier.format(JSON.stringify(data), {
      ...prettierConfig,
      parser: 'json',
    })
    const formattedHomeJson = await prettier.format(JSON.stringify(homeData), {
      ...prettierConfig,
      parser: 'json',
    })

    fs.writeFileSync(filePathDev, formattedAppJson)
    fs.writeFileSync(homePathDev, formattedHomeJson)
    console.log('改写开发环境小程序 json 配置成功')
  }

  if (isPluginExists) {
    removeDir(pluginPackageLive)
    removeDir(pluginLive)
    console.log('移除生产环境小程序直播插件成功')
  }
  if (isPluginExistsDev) {
    removeDir(pluginPackageLiveDev)
    removeDir(pluginLiveDev)
    console.log('移除开发环境小程序直播插件成功')
  }
}

removeLidt()
