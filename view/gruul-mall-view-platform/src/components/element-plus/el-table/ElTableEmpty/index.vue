<script setup lang="ts">
const imgMap: Record<string, string> = {
    1: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839e8.png',
    2: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839e6.png',
    3: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839e7.png',
    4: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839eb.png',
    5: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f9aced3ee24c6839ee.png',
    6: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839ea.png',
    7: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f9aced3ee24c6839ed.png',
    8: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839e9.png',
    9: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839ec.png',
    10: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f9aced3ee24c6839ef.png',
}
defineProps({
    text: {
        type: String,
        default: '暂无数据',
    },
    imageIndex: {
        type: [Number, String],
        default: 0,
    },
})
</script>

<template>
    <el-empty :description="text" :image="imgMap[imageIndex]"></el-empty>
</template>

<style lang="scss" scoped></style>
