<template>
    <router-view v-slot="{ Component, route }">
        <keep-alive>
            <component :is="Component" v-if="route.meta.keepAlive" :key="route.name" />
        </keep-alive>
        <component :is="Component" v-if="!route.meta.keepAlive" />
    </router-view>
</template>
