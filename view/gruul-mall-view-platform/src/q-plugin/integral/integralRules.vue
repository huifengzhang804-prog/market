<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter },
            ElementPlus: { ElMessage },
            Editor,
            Request: { get, post, put, del },
        }"
        name="IntegralRules"
        service="addon-integral"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { get, post, put, del } from '@/apis/http'
import Editor from '@/components/q-editor/editor.vue'
</script>

<style scoped></style>
