# 优惠券插件

## 优惠券前端插件

- 前端插件目录 ```cd ./src/views```
- 安装依赖 ```npm i```
- 运行模式
    - 打包 ```npm run build```
        - 打包后的文件输出为 ```src/views/public```目录下
        - 打包后的文件 复制到 ```src/main/resources/config/public```目录下
        - 启动服务即可正常访问到 ```{baseUrl}/addon-coupon/public/**/index.umd.js```
    - 开发模式运行 ```bash npm run dev`
        - 开发模式启动需要先打包成功
        - 目标文件在 ```src/views/public```目录下
        - 启动成功后访问 ``` http://127.0.0.1:5173/public/**/index.umd.js```