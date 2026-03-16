import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import AutoImport from 'unplugin-auto-import/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const base = env.VITE_BASE_URL || '/'

  return {
    base,
    plugins: [
      vue(),
      AutoImport({
        resolvers: [ElementPlusResolver()],
      }),
      Components({
        resolvers: [ElementPlusResolver()],
      }),
    ],
    resolve: {
      //alias 是一个用于定义路径别名的配置选项。当你的项目结构变得复杂时，路径别名可以帮助你简化 import 或 require 语句中的路径，让代码更干净、更可维护。
      alias: {
        // 定义一个别名 '@'，该别名对应于当前 JavaScript 模块文件所在目录下的 'src' 目录的绝对文件路径。
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: 5173,
      host: 'localhost',
      proxy: {
        '/api': {
          target: env.VITE_DEV_API_TARGET || 'http://localhost:8080/webLog',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
        //  代理文件
        '/upload': {
          target: env.VITE_DEV_UPLOAD_TARGET || 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path
        },
      }
    }
  }
})
