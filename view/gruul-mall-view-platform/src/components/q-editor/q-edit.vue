<script lang="ts" setup>
import { useVModel } from '@vueuse/core'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadFile } from './upload'
import '@wangeditor/editor/dist/css/style.css'
import type { IEditorConfig, SlateElement } from '@wangeditor/editor'

type VideoElement = SlateElement & {
    src: string
    poster?: string
}
type InsertFnType = (url: string, alt: string, href: string) => void
type ImageElement = SlateElement & {
    src: string
    alt: string
    url: string
    href: string
}

const $props = defineProps({
    content: {
        type: String,
        default: '',
    },
    height: {
        type: String,
        default: '300px',
    },
    width: {
        type: String,
        default: '100%',
    },
})
const emit = defineEmits(['update:content'])
const _content = useVModel($props, 'content', emit)

const editorRef = shallowRef()
const baseUrl = import.meta.env.VITE_BASE_URL
const editorConfig: Partial<IEditorConfig> = {
    MENU_CONF: {
        insertImage: {
            onInsertedImage(imageNode: ImageElement | null) {
                if (imageNode === null) return
                const { src, alt, url, href } = imageNode
            },
            checkImage: customCheckImageFn, // 也支持 async 函数
            parseImageSrc: customParseImageSrc, // 也支持 async 函数
        },
        editImage: {
            onUpdatedImage(imageNode: ImageElement | null) {
                // TS 语法
                // onUpdatedImage(imageNode) {                    // JS 语法
                if (imageNode === null) return

                const { src, alt, url } = imageNode
                console.log('updated image', src, alt, url)
            },
            checkImage: customCheckImageFn, // 也支持 async 函数
            parseImageSrc: customParseImageSrc, // 也支持 async 函数
        },
        uploadImage: {
            server: `${baseUrl}gruul-mall-carrier-pigeon/oss/upload`,
            // form-data fieldName ，默认值 'wangeditor-uploaded-image'
            fieldName: 'file',
            // 单个文件的最大体积限制，默认为 2M
            maxFileSize: 1 * 1024 * 1024, // 1M
            // 最多可上传几个文件，默认为 100
            maxNumberOfFiles: 10,
            // 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
            allowedFileTypes: [],
            // 自定义上传参数，例如传递验证的 token 等。参数会被添加到 formData 中，一起上传到服务端。
            meta: {},
            // 将 meta 拼接到 url 参数中，默认 false
            metaWithUrl: false,
            // 自定义增加 http  header
            headers: {},
            // 跨域是否传递 cookie ，默认为 false
            withCredentials: true,
            // 超时时间，默认为 10 秒
            timeout: 5 * 1000, // 5 秒
            // 自定义上传
            async customUpload(file: File, insertFn: InsertFnType) {
                uploadFile('gruul-mall-carrier-pigeon/oss/upload', file).then((res) => {
                    insertFn(res, '', '')
                })
            },
        },
        insertVideo: {
            onInsertedVideo(videoNode: VideoElement | null) {
                if (videoNode === null) return
                const { src } = videoNode
                console.log('inserted video', src)
            },
            checkVideo: customCheckVideoFn, // 也支持 async 函数
            parseVideoSrc: customParseVideoSrc, // 也支持 async 函数
        },
        uploadVideo: {
            // 自定义上传
            async customUpload(file: File, insertFn: InsertFnType) {
                uploadFile('gruul-mall-carrier-pigeon/oss/upload', file).then((res) => {
                    insertFn(res, '', '')
                })
            },
            // 自定义上传
            // file 即选中的文件
            // 自己实现上传，并得到视频 url poster
            // 最后插入视频
        },
    },
}

const toolbarConfig = {
    excludeKeys: ['fullScreen'],
}

// 组件销毁时，也及时销毁编辑器，重要！
onBeforeUnmount(() => {
    if (editorRef.value === null) return
    editorRef.value.destroy()
})

// 自定义校验图片
function customCheckImageFn(src: string, alt: string, url: string): boolean | undefined | string {
    // TS 语法
    // function customCheckImageFn(src, alt, url) {                                                    // JS 语法
    if (!src) {
        return
    }
    if (src.indexOf('http') !== 0) {
        return '图片网址必须以 http/https 开头'
    }
    return true

    // 返回值有三种选择：
    // 1. 返回 true ，说明检查通过，编辑器将正常插入图片
    // 2. 返回一个字符串，说明检查未通过，编辑器会阻止插入。会 alert 出错误信息（即返回的字符串）
    // 3. 返回 undefined（即没有任何返回），说明检查未通过，编辑器会阻止插入。但不会提示任何信息
}

// 转换图片链接
function customParseImageSrc(src: string): string {
    // TS 语法
    // function customParseImageSrc(src) {               // JS 语法
    if (src.indexOf('http') !== 0) {
        return `http://${src}`
    }
    return src
}
// 自定义校验视频
function customCheckVideoFn(src: string, poster: string): boolean | string | undefined {
    // TS 语法
    // function customCheckVideoFn(src, poster) {                                             // JS 语法
    if (!src) {
        return
    }
    if (src.indexOf('http') !== 0) {
        return '视频地址必须以 http/https 开头'
    }
    return true
    // 返回值有三种选择：
    // 1. 返回 true ，说明检查通过，编辑器将正常插入视频
    // 2. 返回一个字符串，说明检查未通过，编辑器会阻止插入。会 alert 出错误信息（即返回的字符串）
    // 3. 返回 undefined（即没有任何返回），说明检查未通过，编辑器会阻止插入。但不会提示任何信息
}

// 自定义转换视频
function customParseVideoSrc(src: string): string {
    // 转换 bilibili url
    // TS 语法
    // if (src.includes('.bilibili.com')) {
    //     const arr = location.pathname.split('/')
    //     const vid = arr[arr.length - 1]
    //     return `<iframe src="//player.bilibili.com/player.html?bvid=${vid}" scrolling="no" border="0" frameborder="no" framespacing="0" allowfullscreen="true"> </iframe>`
    // }
    return src
}
/**
 * 编辑器回调函数
 */
const handleCreated = (editor: any) => {
    editorRef.value = editor
}

const showEditor = ref(false)
onMounted(() => (showEditor.value = true))
</script>

<template>
    <Toolbar :default-config="toolbarConfig" :editor="editorRef" style="border-bottom: 1px solid #ccc" />
    <Editor
        v-if="showEditor"
        v-model="_content"
        :default-config="editorConfig"
        :style="{ height: $props.height, width: $props.width }"
        @on-created="handleCreated"
    />
</template>

<style lang="scss" scoped>
:deep(.w-e-textarea-video-container) {
    // 具有width="auto"时,video标签宽度100%
    video[width='auto'] {
        width: 100%;
    }
    // 不具有width属性时,video标签宽度100%
    video:not([width]) {
        width: 100%;
    }
}

:deep(.w-e-scroll p) {
    overflow-wrap: break-word;
    word-break: break-all;
}
</style>
