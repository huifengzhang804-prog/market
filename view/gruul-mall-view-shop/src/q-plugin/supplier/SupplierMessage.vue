<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import * as Request from '@/apis/http'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { IPage } from '@/views/message/components/types'
import { stompHookMount } from '@/composables/stomp/StompHandler'
import { Channel } from '@/composables/stomp/typs'
import MessageAside from '@/views/message/components/aside/Index.vue'
import MessageMain from '@/views/message/components/main/Index.vue'
</script>

<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            ShopInfoStore: { useShopInfoStore },
            MessageTypes: { IPage },
            Request,
            StompHandler: { stompHookMount },
            StompTypes: { Channel },
            MessageAside,
            MessageMain,
        }"
        name="ShopSupplierMessage"
        service="addon-supplier"
    />
</template>
