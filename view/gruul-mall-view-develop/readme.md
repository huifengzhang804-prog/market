#异常报错
1.注：将node_modules文件夹先删除
2.清理缓存命令：
npm cache clean --force
3.重新安装一次即可
npm install
# 找被占用端口号，杀掉，重启。
1.cmd查询使用的端口号是否被占用：
netstat  -aon|findstr  "8088"
按回车显示占用8088端口对应的程序的PID号；
2.根据PID号找到对应的程序：继续输入命令：
tasklist|findstr "6376"

#Error: spawn \node_modules\esbuild\esbuild.exe ENOENT
node node_modules/esbuild/install.js