<template>
  <scroll-view :style="{ height: (show ? height : 0) + 'px' }" scroll-y class="emoji_main_scroll">
    <view ref="dialog" class="face-main">
      <view
        v-for="(childrenItem, childrenIndex) in emojiName"
        :key="childrenIndex"
        class="face-list-item"
        @click.stop="select(childrenItem, childrenIndex)"
      >
        <image class="emo-image" :src="emojiUrl + emojiMap[childrenItem]"></image>
      </view>
    </view>
  </scroll-view>
</template>

<script lang="ts">
import { defineComponent, reactive, watchEffect, toRefs, computed, ref } from 'vue'
import { emojiUrl, emojiMap, emojiName, bigEmojiList } from '@/pluginPackage/liveModule/views/components/chatRoom/utils/emojiMap'
const Face = defineComponent({
  props: {
    isMute: {
      type: Boolean,
      default: () => false,
    },
    height: {
      type: Number,
      default: 0,
    },
    show: {
      type: Boolean,
      default: () => false,
    },
  },
  emits: ['send', 'handleSendEmoji'],
  setup(props: any, ctx: any) {
    const data = reactive({
      emojiUrl,
      emojiMap,
      emojiName,
      bigEmojiList,
      show: false,
      currentIndex: 0,
      isMute: false,
    })
    const dialog: any = ref()

    watchEffect(() => {
      data.show = props.show
      data.isMute = props.isMute
    })
    const toggleShow = () => {
      if (!data.isMute) {
        data.show = !data.show
      }
    }

    const select = async (item: string, index?: number) => {
      const options: any = {
        name: item,
      }

      if (data.currentIndex === 0) {
        options.type = 'emo'
        options.url = emojiUrl + emojiMap[item]
        options.template = `<image src="${emojiUrl + emojiMap[item]}"></image>`
        return ctx.emit('send', options)
      }
    }

    const list = computed(() => {
      const emjiList = [data.emojiName]
      return emjiList
    })
    const handleSendEmoji = () => {
      return ctx.emit('handleSendEmoji')
    }
    return {
      ...toRefs(data),
      toggleShow,
      select,
      list,
      dialog,
      handleSendEmoji,
    }
  },
})
export default Face
</script>

<style scoped>
.emoji_main_scroll {
  transition: height 0.3s;
  z-index: 10;
}
.face-main {
  background: #ffffff;
  display: flex;
  flex-direction: row;
  width: 750rpx;
  flex-wrap: wrap;
  z-index: 10;
}

.face-list-item {
  padding: 10rpx;
}
.emo-image {
  height: 60rpx;
  width: 60rpx;
}
</style>
