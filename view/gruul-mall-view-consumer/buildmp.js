/* eslint-disable @typescript-eslint/no-require-imports */
const fs = require('fs')
const path = require('path')
const { yellow, log, cyan } = require('console-log-colors')
const { parseJsonWithComments } = require('./src/utils/jsonUtils')
const prettier = require('prettier')

const isDev = process.env.NODE_ENV ? process.env.NODE_ENV.trim() === 'dev' : true
log(cyan.bold('开始处理异步化分包...'))

// 读取pages.json
const pagesConfig = (() => {
  const configPath = path.resolve(__dirname, 'src/pages.json') // @NOTE 这里要根据脚本执行的路径改一下
  const pages = fs.readFileSync(configPath, 'utf8')
  return parseJsonWithComments(pages)
})()

// 读取page.json中的异步分包（没有配置pages）
const asyncPackages = (pagesConfig.subPackages || []).filter((modulePackage) => modulePackage._asyncPackages)

// 主函数
;(async () => {
  try {
    // 写入app.json
    const distPath = path.resolve(__dirname, `dist/${isDev ? 'dev' : 'build'}/mp-weixin`) // @NOTE 这里要根据脚本执行的路径改一下
    const appJsonPath = path.resolve(distPath, 'app.json')
    const appJson = JSON.parse(fs.readFileSync(appJsonPath, 'utf8'))

    if (!appJson.subPackages) {
      appJson.subPackages = []
    }
    asyncPackages.forEach((modulePackage) => {
      const hasInject = appJson.subPackages.find((pack) => pack.root === modulePackage.root)
      if (hasInject) {
        return
      }
      appJson.subPackages.push({
        root: modulePackage.root,
        pages: [],
      })
    })

    // 使用 Prettier 格式化 JSON
    const prettierConfig = await prettier.resolveConfig(process.cwd())
    const formattedJson = await prettier.format(JSON.stringify(appJson), {
      ...prettierConfig,
      parser: 'json',
    })
    fs.writeFileSync(appJsonPath, formattedJson)

    // 寻找用到分包组件的地方，注入组件占位（不注入的话小程序会报错导致分包内组件无法使用）
    const ignorePaths = []
    let discrepancyVal = 0
    ignorePaths.push(appJsonPath) // 过滤app.json
    asyncPackages.forEach((modulePackage) => {
      ignorePaths.push(path.join(distPath, modulePackage.root)) // 过滤分包的内容
    })

    // 修改为异步处理函数
    const injectPlaceholder = async (filepath) => {
      // 判断是否用到了分包的组件
      const jsonConfig = require(filepath)
      if (!jsonConfig.usingComponents) {
        return
      }

      const subPackageComponents = []
      Object.keys(jsonConfig.usingComponents).forEach((componentName) => {
        const componentPath = jsonConfig.usingComponents[componentName]
        const targetSubPackage = asyncPackages.find((modulePackage) => componentPath.includes(`/${modulePackage.root}/`))
        if (targetSubPackage) {
          // 防止重复添加
          if (jsonConfig.componentPlaceholder && jsonConfig.componentPlaceholder[componentName]) {
            return
          }
          discrepancyVal++ // diff 分包异步组件数量
          subPackageComponents.push(componentName)
        }
      })
      if (subPackageComponents.length === 0) {
        return
      }
      log.gray(`开始处理: ${filepath}`)
      if (!jsonConfig.componentPlaceholder) {
        jsonConfig.componentPlaceholder = {}
      }
      subPackageComponents.forEach((name) => {
        jsonConfig.componentPlaceholder[name] = 'view' // 占位符全用view组件
      })

      // 使用 Prettier 格式化 JSON
      const formattedComponentJson = await prettier.format(JSON.stringify(jsonConfig), {
        ...prettierConfig,
        parser: 'json',
      })
      fs.writeFileSync(filepath, formattedComponentJson)

      log.gray(`处理完成: ${filepath}`)
      log.grey('组件名: ', subPackageComponents)
    }

    // 修改为异步递归函数
    async function findJSONAsync(folder, ignorePaths, cb) {
      const files = fs.readdirSync(folder)
      for (const filename of files) {
        const filepath = path.join(folder, filename)
        const isIgnore = ignorePaths.some((ignorePath) => filepath.startsWith(ignorePath))
        if (isIgnore) {
          continue
        }
        const stat = fs.statSync(filepath)
        if (filename.endsWith('.json')) {
          await cb(filepath)
          continue
        }
        if (stat.isDirectory()) {
          await findJSONAsync(filepath, ignorePaths, cb) // 递归查询
        }
      }
    }

    await findJSONAsync(distPath, ignorePaths, injectPlaceholder)

    if (discrepancyVal > 0) {
      log(cyan.bold('异步化分包处理完成'))
    } else {
      log(yellow.bold('没有需要处理的异步化分包'))
    }
    if (isDev) {
      log.white('Watching for changes...')
    }
  } catch (error) {
    console.error('处理 JSON 文件时出错:', error)
  }
})()
