/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-03-15 09:24:01
 * @LastEditors: Liu_Fei 1260324799@qq.com
 * @LastEditTime: 2024-06-11 13:10:48
 */
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";

// https://vitejs.dev/config/
export default defineConfig({
  base: "/develop/",
  server: {
    port: 7000,
  },
  resolve: {
    alias: {
      vue: "vue/dist/vue.esm-bundler.js",
      "@": path.resolve(__dirname, "src"),
      comps: path.resolve(__dirname, "src/components"),
      views: path.resolve(__dirname, "src/views"),
      apis: path.resolve(__dirname, "src/apis"),
      utils: path.resolve(__dirname, "src/utils"),
      "~": path.resolve(__dirname, "node_modules"),
    },
  },
  plugins: [vue()],
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "./src/assets/css/global.scss";`,
      },
    },
  },
});
