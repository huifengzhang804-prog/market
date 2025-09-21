<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

const props = defineProps({
  isShow: {
    type: Boolean,
    default: false,
  },
  teamNo: {
    type: Object,
    required: true,
  },
  records: {
    type: Object,
    required: true,
  },
})
const emit = defineEmits(['isShowFalse'])
const isShowFalse = () => {
  emit('isShowFalse', false)
}

const getSurplus = computed(() => {
  if (props.teamNo?.totalNum && props.records?.records?.length) {
    const surplus = props.teamNo?.totalNum - props.records?.records?.length
    return surplus || 0
  }
  return 0
})

// 活动倒计时
const endTimeValue = ref(new Date(props.teamNo?.openTime).getTime() + Number(props.teamNo?.effectTimeout) * 60 * 1000 || new Date())
const targetDate = ref(new Date(props.teamNo?.openTime).getTime() + Number(props.teamNo?.effectTimeout) * 60 * 1000 || new Date())
const currentTime = ref(new Date().getTime())
const remainingHours = computed(() => {
  if (endTimeValue.value.toString() < currentTime.value.toString()) {
    return '00'
  }
  let timeDiff = +targetDate.value - currentTime.value
  if (timeDiff < 0) {
    return '00'
  }
  let hours = Math.floor((timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  return hours.toString().padStart(2, '0')
})
const remainingMinutes = computed(() => {
  if (endTimeValue.value.toString() < currentTime.value.toString()) {
    return '00'
  }
  let timeDiff = +targetDate.value - currentTime.value
  if (timeDiff < 0) {
    return '00'
  }
  let minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60))
  return minutes.toString().padStart(2, '0')
})
const remainingSeconds = computed(() => {
  if (endTimeValue.value.toString() < currentTime.value.toString()) {
    return '00'
  }
  let timeDiff = +targetDate.value - currentTime.value
  if (timeDiff < 0) {
    return '00'
  }
  let seconds = Math.floor((timeDiff % (1000 * 60)) / 1000)
  return seconds.toString().padStart(2, '0')
})
onMounted(() => {
  setInterval(() => {
    currentTime.value = new Date().getTime()
  }, 1000)
})
</script>

<template>
  <!--  xianshi 显示弹窗 -->
  <div :class="props.isShow === true ? 'all xianshi' : 'all'">
    <el-card class="el-card">
      <div class="title">
        <span>我的拼团</span>
        <i @click="isShowFalse">
          <svg
            t="1686211043354"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="8090"
            width="24"
            height="24"
          >
            <path
              d="M576 512l277.333 277.333-64 64L512 576 234.667 853.333l-64-64L448 512 170.667 234.667l64-64L512 448l277.333-277.333 64 64L576 512z"
              fill="#101010"
              p-id="8091"
            ></path>
          </svg>
        </i>
      </div>
      <div v-if="props.teamNo?.status === 'ING'">
        <div class="line">
          <i></i>
          <em>邀请好友参团</em>
          <i></i>
        </div>
        <p>
          仅剩<i>{{ props.teamNo?.totalNum - props.teamNo.currentNum || 0 }}个</i>名额<i
            >{{ remainingHours }}:{{ remainingMinutes }}:{{ remainingSeconds }}</i
          >后结束
        </p>
      </div>
      <div v-else-if="props.teamNo?.status === 'FAIL'" style="height: 93px; font-size: 16px; line-height: 93px; color: #de3224; padding-left: 70px">
        真可惜拼团未能成功！！！
      </div>
      <div v-else-if="props.teamNo?.status === 'SUCCESS'" style="height: 93px; font-size: 16px; line-height: 93px; color: #de3224">
        拼团成功！！！
      </div>
      <div class="photo">
        <div class="user-img">
          <i>团长</i><span v-for="(item, index) in props.records?.records" :key="index"><img :src="props.teamNo.commanderAvatar" alt="" /></span>
        </div>
        <!-- 未参团占位 -->
        <div v-for="index in getSurplus" :key="index" style="margin-left: 20px">
          <div class="user-imgs"><em style="font-size: 20px">+</em></div>
        </div>
      </div>
      <div class="rule">
        <p>
          <span>商品名称：</span
          ><em style="display: inline-block; width: 300px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis">{{
            props.teamNo?.productName
          }}</em>
        </p>
        <p><span>拼团规则：</span>参团人数不足，系统自动退款</p>
      </div>
    </el-card>
  </div>
</template>
<style scoped lang="scss">
@include b(user-imgs) {
  width: 58px;
  height: 58px;
  border-radius: 50%;
  border: 1px dashed #999;
  display: flex;
  justify-content: center;
  align-items: center;
}
:deep .el-card__body {
  padding: 0;
}
</style>
<style scoped lang="scss">
em,
i {
  font-style: normal;
}
.xianshi {
  display: block !important;
}
.all {
  display: none;
  width: 100%;
  height: 100%;
  background-color: rgba(16, 16, 16, 0.5);
  position: fixed;
  top: 0;
  left: 0;
  z-index: 999999;
  .el-card {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 9999999;
    width: 520px;
    border-radius: 8px 8px 8px 8px;
    background-color: #fff;
    .title {
      height: 44px;
      line-height: 44px;
      border-bottom: 1px solid #bbb;
      span {
        margin-left: 24px;
        float: left;
        font-size: 14px;
        font-weight: bold;
      }
      i {
        margin: 6px 14px 0;
        float: right;
        cursor: pointer;
      }
    }
    .line {
      margin: 24px 0 14px;
      i {
        display: inline-block;
        width: 106px;
        border-top: 1px dashed rgba(10, 10, 10, 0.3);
        transform: translateY(-4px);
      }
      em {
        width: 96px;
        height: 24px;
        font-size: 16px;
        font-weight: bold;
        display: inline-block;
        margin-left: 35px;
        margin-right: 30px;
      }
    }
    p {
      display: inline-block;
      height: 17px;
      font-size: 12px;
      font-weight: bold;
      margin-bottom: 37px;
      i {
        margin: 0 5px;
        color: #ee1e38;
      }
    }
    .photo {
      margin-bottom: 34px;
      display: flex;
      justify-content: center;
      margin-left: -20px;
      .user-img {
        display: inline-flex;
        position: relative;
        i {
          padding: 2px 5px;
          background-color: #f7c945;
          color: #fff;
          font-weight: bold;
          border-radius: 4px;
          font-size: 12px;
          left: -12px;
          top: -11px;
          position: absolute;
          z-index: 9;
        }
      }
      span {
        width: 58px;
        height: 58px;
        border-radius: 50%;
        box-sizing: border-box;
        margin-left: 29px;
        border: 1px dashed #bbb;
        &:first-of-type {
          border: 4px solid #f7c945;
          margin-left: 0;
        }
        img {
          border-radius: 50%;
          width: 100%;
          height: 100%;
        }
      }
    }
    .rule {
      border-top: 1px solid #bbb;
      padding: 24px 0;
      p {
        font-size: 14px;
        width: 100%;
        padding-right: 66px;
        margin-bottom: 16px;
        text-align: right;
        color: #101010;
        &:last-child {
          margin-bottom: 0;
        }
        span {
          font-weight: 500 !important;
          margin-left: 66px;
          float: left;
          text-align: justify;
          text-align-last: justify;
          text-justify: inter-word;
          display: block;
          width: 70px;
          height: 20px;
          color: #a7a7a7;
        }
      }
    }
  }
}
</style>
