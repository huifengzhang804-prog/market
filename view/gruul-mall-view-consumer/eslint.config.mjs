import eslintPluginVue from 'eslint-plugin-vue'
import globals from 'globals'
import eslintPluginPrettierRecommended from 'eslint-plugin-prettier/recommended'
import js from '@eslint/js'
import tseslint from 'typescript-eslint'
import eslintrcAutoImport from './types/.eslintrc-auto-import.json' with { type: 'json' }

export default [
  /**
   * 继承第三方的推荐配置
   */
  js.configs.recommended,
  ...tseslint.configs.recommended,
  ...eslintPluginVue.configs['flat/recommended'],
  /**
   * 配置全局变量
   */
  {
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node,
        ...eslintrcAutoImport.globals, // 自动导入的变量
        // vitest 提供的一些常用函数
        describe: true,
        it: true,
        test: true,
        expect: true,
        beforeEach: true,
        afterEach: true,
        vi: true,
        /** 追加一些其他自定义全局规则 */
        Message: true,
        env: true,
        useRoute: true,
        useRouter: true,
        useStore: true,
        uni: true,
        DecimalType: true,
        ImportMeta: true,
        AnyObject: true,
        getApp: true,
        plus: true,
        ImportMetaEnv: true,
        Obj: true,
        Long: true,
        NodeJS: true,
        getCurrentPages: true,
        UniNamespace: true,
        // 'vue/setup-compiler-macros': true,
      },
      parserOptions: {
        /** typescript项目需要用到这个 */
        parser: tseslint.parser,
        ecmaVersion: 'latest',
        sourceType: 'module',
        /** 允许在.vue 文件中使用 JSX */
        ecmaFeatures: {
          // tsx: true,
          jsx: true,
        },
        project: ['./tsconfig.json'],
        extraFileExtensions: ['.vue', '.jsx', '.tsx', '.ts', '.js'],
      },
    },
  },
  {
    files: ['**/*.{js,jsx,ts,tsx,vue,mjs,cjs}'],
    rules: {
      // js eslint
      'no-console': 'off',
      'no-debugger': 'off',
      eqeqeq: 'error', // 要求使用 === 和 !==
      'no-undef': 'error', // 禁用未声明的变量
      'no-unused-vars': 'off', // 禁止出现未使用过的变量
      'vars-on-top': 'off', // 要求所有的 var 声明出现在它们所在的作用域顶部
      'prefer-destructuring': 'off', // 优先使用数组和对象解构
      'no-useless-concat': 'warn', // 禁止不必要的字符串字面量或模板字面量的连接
      'no-useless-escape': 'off', // 禁止不必要的转义字符
      'consistent-return': 'off', // 要求 return 语句要么总是指定返回的值，要么不指定
      camelcase: 'off', // 强制使用骆驼拼写法命名约定
      'no-redeclare': 'warn', // 禁止多次声明同一变量
      'array-callback-return': 'warn', // 强制数组方法的回调函数中有 return 语句,Array有几种过滤，映射和折叠的方法。如果我们忘记return在这些回调中写入语句，那可能是一个错误。
      'default-case': 'warn', // 要求 switch 语句中有 default 分支
      'no-fallthrough': 'warn', // 禁止 case 语句落空
      'no-lonely-if': 'warn', // 禁止 if 作为唯一的语句出现在 else 语句中.如果一个if陈述是该else块中唯一的陈述，那么使用一个else if表格通常会更清晰。
      'no-irregular-whitespace': 'warn', // 禁止在字符串和注释之外不规则的空白
      'prefer-const': 'off', // 要求使用 const 声明那些声明后不再被修改的变量.如果一个变量从不重新分配，使用const声明更好。const 声明告诉读者，"这个变量永远不会被重新分配，"减少认知负荷并提高可维护性。
      'no-use-before-define': 'off', // 禁止在变量定义之前使用它们

      // ts eslint
      '@typescript-eslint/consistent-type-exports': [
        'error',
        {
          fixMixedExportsWithInlineTypeSpecifier: true,
        },
      ],
      '@typescript-eslint/no-non-null-assertion': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-var-requires': 'off',
      '@typescript-eslint/no-unused-vars': 'off',
      '@typescript-eslint/ban-ts-comment': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/no-empty-function': 'off',
      '@typescript-eslint/no-empty-object-type': 'off',

      // vue eslint
      'vue/valid-define-props': 'off',
      'vue/attribute-hyphenation': 'off',
      'vue/v-on-event-hyphenation': 'off',
      'vue/eqeqeq': 'error', // 要求使用 === 和 !==
      'vue/require-v-for-key': 'warn', // 当v-for写在自定义组件上时，它需要同时使用v-bind：key。在其他元素上，v-bind：key也最好写。
      'vue/attributes-order': 'error', // vue api使用顺序
      'vue/no-side-effects-in-computed-properties': 'off',
      'vue/order-in-components': [
        'error',
        {
          order: [
            'el',
            'name',
            'key',
            'parent',
            'functional',
            ['delimiters', 'comments'],
            ['components', 'directives', 'filters'],
            'extends',
            'mixins',
            ['provide', 'inject'],
            'validate',
            'scrollToTop',
            'transition',
            'loading',
            'inheritAttrs',
            'model',
            ['props', 'propsData'],
            'emits',
            'setup',
            'asyncData',
            'data',
            'computed',
            'watch',
            'created',
            'mounted',
            'methods',
            ['template', 'render'],
            'renderError',
          ],
        },
      ],
      'vue/no-multiple-template-root': 'off',
      'vue/multi-word-component-names': 'off',
      'vue/no-deprecated-slot-attribute': 'off',
      'vue/v-on-event-hyphenation': 'off',
    },
  },

  /**
   * 忽略文件
   */
  {
    ignores: [
      '*.sh',
      'node_modules',
      '*.md',
      '*.woff',
      '*.ttf',
      '*.nvue',
      '.vscode',
      '.idea',
      'dist',
      'public',
      'docs',
      '.husky',
      '.local',
      'bin',
      'changelog-option.js',
      '*.d.ts',
      'html-parser',
      '.commitlintrc.js',
      'deletHeaderNotes.js',
      'eslint.config.mjs',
      'knip.config.ts',
      'prettier.config.mjs',
      'src/assets',
      'src/pluginPackage/goods/commodityInfo/components/poster/canvas-other.vue',
      'src/pluginPackage/liveModule/views/components/share/gcanvas/*',
      'src/pluginPackage/liveModule/views/components/chatRoom/hooks/*',
      'src/pages.json',
      'src/uni_modules/*',
      '!/src/uni_modules/vk-uview-ui/components/u-picker/u-picker.vue',
      'src/scripts/removeLive.js',
      'src/asyncPackages/picker-time/*',
      'src/asyncPackages/uqrcode/*',
      'types/auto-imports.d.ts',
      'vitest.config.ts', // 忽略vitest配置文件
      'coverage/*',
      'src/devTools/**/*',
      'src/utils/jsonUtils.js',
      'scripts/**/*', // 忽略scripts目录下的所有文件
    ],
  },
  /**
   * prettier 配置
   * 会合并根目录下的 prettier.config.js 文件
   * @see https://prettier.io/docs/en/options
   */
  eslintPluginPrettierRecommended,
]
