// 删除头部的多行注释
// 获取当前文件路径
const path = require('path')
const fs = require('fs')
function resolve(dirname = __dirname, pathStr = '') {
  return path.resolve(dirname, pathStr)
}
// 根目录
const rootPath = resolve('src')
console.log('根目录', rootPath)

function readDirRecursive(dir) {
  console.log('当前路径===============', dir)
  const files = fs.readdirSync(dir)
  for (const file of files) {
    const filePath = resolve(dir, file)
    if (fs.statSync(filePath).isDirectory()) {
      readDirRecursive(filePath)
    } else {
      if (file.endsWith('.vue') || file.endsWith('.ts') || file.endsWith('.tsx')) {
        console.log('.vue/.ts/.tsx结尾的文件:', file)
        if (file.endsWith('.ts') || file.endsWith('.tsx')) {
          fs.readFile(filePath, 'utf8', (err, data) => {
            if (err) {
              console.log(err)
              console.log(`读取文件${filePath}失败`)
              return
            }
            // 如果data 头部是多行注释('/**/')则删除该注释
            if (data.startsWith('/*')) {
              if (!data.startsWith('/** eslint') && !data.startsWith('/* eslint-')) {
                const index = data.indexOf('*/') + 2
                const newData = data.slice(index).replace(/^\s+/, '')
                fs.writeFile(filePath, newData, 'utf8', (err) => {
                  if (err) {
                    console.log(err)
                    console.log(`${filePath}删除注释失败`)
                    return
                  }
                  console.log(`${filePath}删除注释成功`)
                })
              }
            }
          })
        } else {
          fs.readFile(filePath, 'utf8', (err, data) => {
            if (err) {
              console.log(err)
              console.log(`读取文件${filePath}失败`)
              return
            }
            // 如果data 头部是多行注释('<!---->')则删除该注释
            if (data.startsWith('<!--')) {
              if (!data.startsWith('<!-- eslint') && !data.startsWith('<!--eslint')) {
                const index = data.indexOf('-->') + 3
                const newData = data.slice(index).replace(/^\s+/, '')
                fs.writeFile(filePath, newData, 'utf8', (err) => {
                  if (err) {
                    console.log(err)
                    console.log(`${filePath}删除注释失败`)
                    return
                  }
                  console.log(`${filePath}删除注释成功`)
                })
              }
            }
          })
        }
      }
    }
  }
}

readDirRecursive(rootPath)
