# Vue 3 + TypeScript + Vite 启山科技开发规范

# 1.1、CSS 命名规范 BEM 规范

BEM 代表 块(block)，元素(element)，修饰符(modifier)，我们常用这三个实体开发组件。

- 中划线 ：仅作为连字符使用，表示某个块或者某个子元素的多单词之间的连接记号；
  \_\_ 双下划线：双下划线用来连接块和块的子元素；
  --双短横线：双短横线用来描述一个块或者块的子元素的一种状态；

示例：type-block\_\_element_modifier
CSS 中只能使用类名（不能是 ID）。
每一个块名应该有一个命名空间（前缀）
每一条 CSS 规则必须属于一个块。
BEM 解决问题:
组件之间的完全解耦，不会造成命名空间的污染，如：.mod-xxx ul li 的写法带来的潜在的嵌套风险。

# 1.2 JavaScript 编写规范 Eslint 规范

（1）本项目已配置 eslint 规范并在提交代码时做了校验，开发者必须复合规范开发。（利于团队维护）
（2）html 注释规范 <!-- 内容--start --> <!-- 内容--end -->
（3）变量注释规范 变量上方双下划线 //
（4）变量命名规范 变量名使用驼峰法来命名 常亮使用大写以\_做分割
（5）页面事件函数以 hadle 开头，用于函数区分。（便于维护）
（6）工具类置于 utils 目录下，能用工具类就不要用第三方。（优势：体积轻便。）
（7）定义的公共方法必须合理抽离，置于 hooks 文件目录下。（便于复用维护）

# 1.3 VUE 编写规范

（1）组件名 页面级别组件大写驼峰法命名，其他组件小写短横线连接（组件清晰，利于维护）
（2）公共组件置于 src 下的 components 目录下。
（3）首页面尽量精简。（结构清晰）
（4）引入资源顺序 vueAPi>vue 组件>工具函数>类型声明
（5）新建 vue 文件使用 Vue3.2+初始化 对应注释模块顺序

# 1.4 接口 编写规范

（1）接口定义 src 下的 apis 目录中，其接口文件名须与模块命名一致（便于维护）
（2）封装的请求接口必须以 "do" + "请求方法" + "具体说明" 命名。（函数区分和阐述语义）

# 1.5 提交规范

（1）代码前 run lint 无 error 项后再走提交流程
（2）代码提交遵循 commitlint 中配置对应内容 格式如:提交内容标签 提交头部描述 提交详细内容 是否存在破坏更改 是否存在副作用

# 1.6 目录格式

(1) libs 与 utils 界定为 libs 与业务逻辑相关复用，utils 只针对程序本身，外部程序一样可以使用不受影响

```
gruul-mall-view-consumer
├─ 📁.hbuilderx
│  └─ 📄launch.json
├─ 📁.husky
│  ├─ 📁_
│  │  ├─ 📄.gitignore
│  │  └─ 📄husky.sh
│  ├─ 📄commit-msg
│  └─ 📄pre-commit
├─ 📁dist
├─ 📁node_modules
├─ 📁plugins
│  ├─ 📄vite-plugin-buildmp.ts
│  └─ 📄vite-plugin-preload.ts
├─ 📁src
│  ├─ 📁apis    # 接口定义
│  ├─ 📁assets  # 静态资源
│  │  ├─ 📁css
│  │  ├─ 📁img
│  │  ├─ 📁json
│  │  └─ 📁media
│  ├─ 📁basePackage # 分包--基础包
│  │  ├─ 📁apis
│  │  ├─ 📁components
│  │  └─ 📁pages
│  │     ├─ 📁abnormalGoods
│  │     ├─ 📁aboutUs
│  │     ├─ 📁accountManagement
│  │     ├─ 📁addressManage
│  │     ├─ 📁applyFournisseurs
│  │     ├─ 📁applyMerchant
│  │     ├─ 📁assessList
│  │     ├─ 📁billingDetails
│  │     ├─ 📁checkLogistics
│  │     ├─ 📁concern
│  │     ├─ 📁customerService
│  │     ├─ 📁customPage
│  │     ├─ 📁diamondClassification
│  │     ├─ 📁editUserInfo
│  │     ├─ 📁footprint
│  │     ├─ 📁goodsSearch
│  │     ├─ 📁InvoiceHeader
│  │     ├─ 📁InvoiceHeaderAdd
│  │     ├─ 📁Invoicing
│  │     ├─ 📁memberCenter
│  │     ├─ 📁membershipCode
│  │     ├─ 📁merchant
│  │     ├─ 📁message
│  │     ├─ 📁negotiationHistory
│  │     ├─ 📁onlineTopUp
│  │     ├─ 📁orderAssess
│  │     ├─ 📁pay
│  │     ├─ 📁paySuccess
│  │     ├─ 📁privateAgreement
│  │     ├─ 📁releaseAssess
│  │     ├─ 📁searchPage
│  │     ├─ 📁searchRetrieve
│  │     ├─ 📁storedValue
│  │     ├─ 📁storeReturn
│  │     ├─ 📁webView
│  │     └─ 📁withdrawalMoney
│  ├─ 📁components    # 公共组件
│  │  ├─ 📁auth
│  │  ├─ 📁canvas-share
│  │  ├─ 📁count-number
│  │  ├─ 📁debounce
│  │  ├─ 📁determine-btn
│  │  ├─ 📁good-spec
│  │  ├─ 📁highlight-retrieve-com-unit
│  │  ├─ 📁lazy-load
│  │  ├─ 📁my-calendar
│  │  ├─ 📁order
│  │  ├─ 📁popup
│  │  ├─ 📁portrait
│  │  ├─ 📁preview-image
│  │  ├─ 📁q-btns
│  │  ├─ 📁q-count-down
│  │  ├─ 📁q-general-tabbar
│  │  ├─ 📁q-gotop
│  │  ├─ 📁q-icon
│  │  ├─ 📁q-loading
│  │  ├─ 📁q-marketing-popup
│  │  ├─ 📁q-nav
│  │  ├─ 📁q-price
│  │  ├─ 📁q-section
│  │  ├─ 📁q-service
│  │  ├─ 📁q-sharePage
│  │  ├─ 📁q-wx-animate-tabbar
│  │  ├─ 📁qszr-core
│  │  ├─ 📁retrieve-com-unit
│  │  ├─ 📁scan-pop
│  │  ├─ 📁slide-captcha
│  │  └─ 📁text-toggle
│  ├─ 📁composables # vue3全局组合api
│  │  ├─ 📄useConvert.ts
│  │  ├─ 📄useMember.ts
│  │  ├─ 📄usePriceRange.ts
│  │  └─ 📄useBottomSafe.ts
│  ├─ 📁config # 配置文件
│  │  ├─ 📄live-icon-https.ts
│  │  └─ 📄order-view-config.ts
│  ├─ 📁constant    # 常量
│  │  ├─ 📁global   # 全局常量
│  │  │  └─ 📄index.ts
│  │  └─ 📄index.ts
│  ├─ 📁decoration  # 装修相关组件
│  │  └─ 📁components
│  │     ├─ 📁blank-holder
│  │     ├─ 📁classification
│  │     ├─ 📁compose-location
│  │     ├─ 📁compose-swiper
│  │     ├─ 📁cube-box
│  │     ├─ 📁cus-video
│  │     ├─ 📁good
│  │     ├─ 📁positioning-style
│  │     ├─ 📁resize-image
│  │     ├─ 📁rich-text
│  │     ├─ 📁search
│  │     ├─ 📁separator
│  │     ├─ 📁shop-goods
│  │     ├─ 📁shop-nav
│  │     ├─ 📁swiper
│  │     ├─ 📁title-bar
│  │     └─ 📁types
│  ├─ 📁hooks   # vue3全局hooks
│  │  ├─ 📁stomp
│  │  ├─ 📁useCountdown
│  │  ├─ 📄index.ts
│  │  ├─ 📄useChooseAddress.ts
│  │  ├─ 📄useCountdownTime.ts
│  │  ├─ 📄useFootprintCollection.ts
│  │  ├─ 📄useNavBack.ts
│  │  ├─ 📄useOrderStatus.ts
│  │  ├─ 📄usePaymentCn.ts
│  │  ├─ 📄useSearchAddress.ts
│  │  ├─ 📄useSelector.ts
│  │  ├─ 📄useSelectorQuery.ts
│  │  └─ 📄useStatusBar.ts
│  ├─ 📁libs    # 全局库
│  │  ├─ 📁request
│  │  │  ├─ 📄bxios.ts
│  │  │  ├─ 📄bxios.type.ts
│  │  │  ├─ 📄index.ts
│  │  │  └─ 📄returnHeader.ts
│  │  ├─ 📄formatImage.ts
│  │  ├─ 📄formatTime.ts
│  │  ├─ 📄linkNavTo.ts
│  │  ├─ 📄MediaPlayer.ts
│  │  ├─ 📄orderStatus.ts
│  │  ├─ 📄pageScrollTo.ts
│  │  ├─ 📄sysConfig.ts
│  │  └─ 📄validate.ts
│  ├─ 📁pages   # 分包--主包
│  │  ├─ 📁modules  # 底部导航
│  │  │  ├─ 📁car
│  │  │  ├─ 📁classification
│  │  │  ├─ 📁consumer
│  │  │  └─ 📁home
│  │  ├─ 📁platform
│  │  │  └─ 📄Index.vue
│  │  ├─ 📁plugin
│  │  │  ├─ 📁coupon
│  │  │  ├─ 📁fullDiscount
│  │  │  ├─ 📁live
│  │  │  ├─ 📁member
│  │  │  ├─ 📁secKill
│  │  │  ├─ 📁shopStore
│  │  │  └─ 📁types
│  │  └─ 📁sign
│  ├─ 📁pluginPackage   # 分包--插件包
│  │  ├─ 📁bargain
│  │  │  ├─ 📁apis
│  │  │  ├─ 📁hooks
│  │  │  └─ 📁views
│  │  │     ├─ 📁goodsDateilComponents
│  │  │     │  ├─ 📁bargainProcess
│  │  │     │  ├─ 📁countDown
│  │  │     │  └─ 📁myBargain
│  │  │     └─ 📄bargain.vue
│  │  ├─ 📁cart-point
│  │  │  └─ 📄cart-point.vue
│  │  ├─ 📁components
│  │  ├─ 📁coupon
│  │  │  ├─ 📁apis
│  │  │  ├─ 📁components
│  │  │  ├─ 📁couponCenter
│  │  │  ├─ 📁hooks
│  │  │  └─ 📁myCoupon
│  │  ├─ 📁distribute
│  │  │  ├─ 📁alipayInformation
│  │  │  ├─ 📁apis
│  │  │  ├─ 📁bankCardInformation
│  │  │  ├─ 📁commissionDetail
│  │  │  ├─ 📁customer
│  │  │  ├─ 📁distributorCenter
│  │  │  ├─ 📁distributorGoods
│  │  │  ├─ 📁Introduction
│  │  │  ├─ 📁myTeam
│  │  │  ├─ 📁order
│  │  │  ├─ 📁promotionCode
│  │  │  ├─ 📁protocol
│  │  │  ├─ 📁ranking
│  │  │  ├─ 📁registerDistributor
│  │  │  ├─ 📁withdrawDeposit
│  │  │  └─ 📄DistributorBackground.vue
│  │  ├─ 📁goods
│  │  │  └─ 📁commodityInfo
│  │  │     ├─ 📁components
│  │  │     ├─ 📁goodsPreferentialPopup
│  │  │     ├─ 📁hooks
│  │  │     ├─ 📁module-components
│  │  │     ├─ 📁morePreferential
│  │  │     ├─ 📁types
│  │  │     ├─ 📄index.ts
│  │  │     └─ 📄InfoEntrance.vue
│  │  ├─ 📁group
│  │  │  ├─ 📁apis
│  │  │  ├─ 📁components
│  │  │  ├─ 📁hooks
│  │  │  └─ 📁views
│  │  │     ├─ 📄group-card-commodity.vue
│  │  │     ├─ 📄group-card-count.vue
│  │  │     ├─ 📄group-list-commodity.vue
│  │  │     ├─ 📄GroupList.vue
│  │  │     └─ 📄OwnGroup.vue
│  │  ├─ 📁hooks
│  │  ├─ 📁integral
│  │  │  ├─ 📁api
│  │  │  └─ 📁mall
│  │  │     ├─ 📁components
│  │  │     ├─ 📁view
│  │  │     │  ├─ 📁integralLogistics
│  │  │     │  ├─ 📁integralOrderDetail
│  │  │     │  ├─ 📁integralOrderList
│  │  │     │  └─ 📁signIn
│  │  │     └─ 📄Index.vue
│  │  ├─ 📁intracity
│  │  │  ├─ 📁apis
│  │  │  └─ 📁components
│  │  ├─ 📁live
│  │  │  ├─ 📁components
│  │  │  └─ 📁views
│  │  ├─ 📁liveModule
│  │  │  ├─ 📁apis
│  │  │  └─ 📁views
│  │  │     ├─ 📁components
│  │  │     ├─ 📁CreateLive
│  │  │     ├─ 📁FeaturedLive
│  │  │     ├─ 📁ImmediatelyStarted
│  │  │     ├─ 📁ReleaseNotice
│  │  │     ├─ 📁Search
│  │  │     ├─ 📁userWatch
│  │  │     ├─ 📄Audience.nvue
│  │  │     ├─ 📄liveAnchor.nvue
│  │  │     └─ 📄socket.ts
│  │  ├─ 📁member
│  │  │  ├─ 📁apis
│  │  │  ├─ 📁hooks
│  │  │  └─ 📁views
│  │  ├─ 📁o2o-goods
│  │  │  ├─ 📁components
│  │  │  │  ├─ 📄car-bar.vue
│  │  │  │  ├─ 📄car-popup.vue
│  │  │  │  ├─ 📄o2o-commodity-line.vue
│  │  │  │  ├─ 📄o2o-info.vue
│  │  │  │  ├─ 📄o2o-selling-info.vue
│  │  │  │  ├─ 📄o2o-swiper.vue
│  │  │  │  └─ 📄select-spec.vue
│  │  │  └─ 📄o2o-goods.vue
│  │  ├─ 📁order
│  │  │  ├─ 📁afterSales
│  │  │  ├─ 📁applyAfterSales
│  │  │  ├─ 📁appraise
│  │  │  ├─ 📁confirmOrder
│  │  │  ├─ 📁detailsRefund
│  │  │  ├─ 📁hooks
│  │  │  ├─ 📁orderDetail
│  │  │  └─ 📁orderList
│  │  ├─ 📁rebate
│  │  │  ├─ 📁apis
│  │  │  ├─ 📁hooks
│  │  │  └─ 📁views
│  │  ├─ 📁scondsKill
│  │  │  ├─ 📁apis
│  │  │  ├─ 📁components
│  │  │  ├─ 📁hooks
│  │  │  └─ 📁views
│  │  ├─ 📁setMeal
│  │  │  ├─ 📁apis
│  │  │  ├─ 📁components
│  │  │  ├─ 📁hooks
│  │  │  └─ 📁views
│  │  ├─ 📁shopSearch
│  │  ├─ 📁shopStore
│  │  │  ├─ 📁apis
│  │  │  └─ 📁components
│  │  ├─ 📁store
│  │  │  └─ 📁modules
│  │  │     └─ 📁order
│  │  │        └─ 📄index.ts
│  │  └─ 📁utils
│  │     └─ 📄formatRichText.ts
│  ├─ 📁scripts # 脚本
│  │  ├─ 📄generate-picker.mjs
│  │  ├─ 📄modify-appId.ts
│  │  ├─ 📄modify-assemble.ts
│  │  ├─ 📄modify-live-id.ts
│  │  ├─ 📄modify-map-key.ts
│  │  ├─ 📄removeLive.js
│  │  └─ 📄replaceManifestKey.ts
│  ├─ 📁store   # 状态管理
│  │  ├─ 📁dispatcher
│  │  │  ├─ 📄useAddressDispatcher.ts
│  │  │  ├─ 📄useCartDispatcher.ts
│  │  │  ├─ 📄useCollectionDispatcher.ts
│  │  │  ├─ 📄useGoodsInfoDispatcher.ts
│  │  │  ├─ 📄useOrderDispatcher.ts
│  │  │  ├─ 📄useRebateDispatcher.ts
│  │  ├─ 📁modules
│  │  │  ├─ 📁app
│  │  │  │  ├─ 📄index.ts
│  │  │  │  └─ 📄state.ts
│  │  │  ├─ 📁composedecoration
│  │  │  │  └─ 📄index.ts
│  │  │  ├─ 📁invoice
│  │  │  │  └─ 📄index.ts
│  │  │  ├─ 📁message
│  │  │  │  └─ 📄index.ts
│  │  │  ├─ 📁setting
│  │  │  │  ├─ 📄index.ts
│  │  │  │  └─ 📄state.ts
│  │  │  ├─ 📁subscribe
│  │  │  │  └─ 📄index.ts
│  │  │  └─ 📁user
│  │  │     ├─ 📄index.ts
│  │  │     └─ 📄state.ts
│  │  └─ 📄global.ts
│  ├─ 📁uni_modules # 第三方UI库
│  │  └─ 📁vk-uview-ui
│  ├─ 📁utils   # 工具函数
│  │  ├─ 📄base64ToImg.ts
│  │  ├─ 📄composePromise.ts
│  │  ├─ 📄date.ts
│  │  ├─ 📄index.ts
│  │  ├─ 📄is.ts
│  │  ├─ 📄navigateToShopInfo.ts
│  │  ├─ 📄PopUp.ts
│  │  ├─ 📄request.ts
│  │  ├─ 📄rsa.ts
│  │  ├─ 📄setShopId.ts
│  │  ├─ 📄StompSock.ts
│  │  ├─ 📄storage.ts
│  │  ├─ 📄tokenConfig.ts
│  │  ├─ 📄types.ts
│  │  └─ 📄util.ts
│  ├─ 📄App.vue # 根组件
│  ├─ 📄env.d.ts
│  ├─ 📄main.ts # 入口文件
│  ├─ 📄manifest.json   # 配置文件
│  ├─ 📄pages.json  # 页面配置文件
│  └─ 📄uni.scss
├─ 📁types  # 类型声明文件
│  ├─ 📄.eslintrc-auto-import.json
│  ├─ 📄.eslintrc-custom.json
│  ├─ 📄auto-imports.d.ts
│  ├─ 📄env.d.ts
│  └─ 📄global.d.ts
├─ 📁unpackage  # 不打包文件
├─ 📄.commitlintrc.js
├─ 📄.env.development   # 开发环境
├─ 📄.env.production    # 生产环境
├─ 📄.env.test  # 测试环境
├─ 📄.eslintignore
├─ 📄.eslintrc.js
├─ 📄.gitignore
├─ 📄.prettierignore
├─ 📄.prettierrc.js
├─ 📄buildmp.js # 打包脚本
├─ 📄cert.crt   # https证书
├─ 📄cert.key   # https证书密钥
├─ 📄changelog-option.js
├─ 📄deletHeaderNotes.js
├─ 📄index.html # 入口html
├─ 📄Manual.md
├─ 📄package-lock.json
├─ 📄package.json   # 项目依赖
├─ 📄project.config.json
├─ 📄project.private.config.json
├─ 📄README.md  # 项目说明
├─ 📄tsconfig.json  # ts配置文件
├─ 📄vite.config.ts # vite配置文件
```
