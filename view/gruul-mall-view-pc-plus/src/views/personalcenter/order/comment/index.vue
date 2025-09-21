<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { doGetbuyersEvaluation } from '@/apis/assess'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import { ApiEvaluate } from './types'

const keywords = ref('')
const evaluateList = ref<ApiEvaluate[]>([])
const videoType = ['avi', 'mp4', 'wmv', 'mov', 'flv', 'mkv', 'webm', 'rmvb', '3gp', 'MOV']
const pageConfig = shallowReactive({
  size: 10,
  current: 1,
  total: 0,
})
const type = ref('')
const typeList = [
  {
    value: '',
    label: '全部状态',
  },
  {
    value: 'CONTENT',
    label: '有内容',
  },
  {
    value: 'IMAGE',
    label: '有图片',
  },
]
const rate = ref('')
const rateList = [
  {
    value: '',
    label: '全部',
  },
  {
    value: '1',
    label: '1星',
  },
  {
    value: '2',
    label: '2星',
  },
  {
    value: '3',
    label: '3星',
  },
  {
    value: '4',
    label: '4星',
  },
  {
    value: '5',
    label: '5星',
  },
]

initEvaluateList()

async function initEvaluateList() {
  const { size, current } = pageConfig
  const { code, data, msg } = await doGetbuyersEvaluation({ size, current, rate: rate.value, type: type.value, keywords: keywords.value })
  if (code !== 200) return ElMessage.error(msg ? msg : '获取列表失败')
  if (data) {
    pageConfig.total = data.total
    data.records.forEach((item: any) => {
      item.picture = []
      item.video = []
      item.medias?.forEach((ite: any) => {
        if (ite.split('.')[2] === 'mp4') {
          item.video.push(ite)
        } else {
          item.picture.push(ite)
        }
      })
    })
    evaluateList.value = data.records
  }
}
/**
 * @description: 调用initEvaluateList
 */
const callinitEvaluateList = () => {
  pageConfig.current = 1
  initEvaluateList()
}

// 打开评论视频
const videoSrc = ref('')
const commentVideo = ref(null)
const showVideoComment = ref(false)
const showCommentVideo = (videoUrl: string) => {
  showVideoComment.value = true
  videoSrc.value = videoUrl
  playCommentVideo()
}
const playCommentVideo = () => {
  if (commentVideo.value) {
    ;(commentVideo.value as HTMLVideoElement).play()
  }
}
const videoCommentEnd = () => {
  videoSrc.value = ''
  showVideoComment.value = false
}

/**
 * @description: 分页切换
 */
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  initEvaluateList()
}
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <div c-h-66 c-w-1024 b-b c-bc-ccc c-pl-25 e-c-3 fw-700 c-fs-18 c-lh-66 text-left c-mb-16>评价晒单</div>
        <div flex justify-between items-center>
          <div flex items-center c-w-303 bg-white b-1 c-bc-DCDCDC c-fs-12>
            <el-input v-model="keywords" placeholder="请输入商品名称或订单号搜索" />
            <div cursor-pointer c-w-48 c-h-32 c-lh-32 c-bg-eee b-l c-bc-DCDCDC @click="callinitEvaluateList">搜索</div>
          </div>
          <div c-fs-12 e-c-3 flex items-center>
            <div>评价星级:</div>
            <el-select
              v-model="rate"
              placeholder="全部订单"
              size="small"
              style="width: 120px; border: 1px solid rgba(220, 220, 220, 1); border-radius: 3px"
              @change="callinitEvaluateList"
            >
              <el-option v-for="item in rateList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <div c-ml-27>评价内容:</div>
            <el-select
              v-model="type"
              placeholder="全部状态"
              size="small"
              style="width: 120px; border: 1px solid rgba(220, 220, 220, 1); border-radius: 3px"
              @change="callinitEvaluateList"
            >
              <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
        </div>
        <div flex e-c-3 c-fs-14 c-h-39 c-lh-39>
          <div c-ml-45>商品</div>
          <div c-ml-288>评价星级</div>
          <div c-ml-127>评价内容</div>
        </div>
        <template v-for="order in evaluateList" :key="order.id">
          <div b-1 c-bc-ebeae6 c-bg-f7f7f7 c-h-40 c-lh-40 e-c-9 c-fs-12 text-left c-w-1022>
            <span c-ml-34>评价时间：{{ order.createTime }}</span>
          </div>
          <div c-w-1022 c-fs-12 e-c-3 c-mb-10 min-h-22 flex items-center b-1 c-bc-EBEAE6>
            <div c-ml-24>
              <el-image style="width: 60px; height: 60px" :src="order.image" />
            </div>
            <div c-w-164 text-left c-ml-12>
              <div c-ellipsis-2>
                {{ order.name }}
              </div>
              <div e-c-9>
                {{ order.specs?.join(',') }}
              </div>
            </div>
            <el-rate v-model="order.rate" disabled size="small" c-ml-72 />
            <div c-w-454 e-c-3 c-fs-12 text-left c-ml-90 c-mt-15 c-mb-15>
              <div>
                {{ order.comment }}
              </div>
              <div style="display: flex; flex-wrap: wrap" c-mt-14 c-mb-13 c-gap-15>
                <div v-for="(img, index) in order.medias" :key="index">
                  <video v-if="videoType.some((type) => type === img.split('.').pop())" c-w-56 c-h-56 :src="img" @click="showCommentVideo(img)" />
                  <el-image v-else :src="img" :preview-src-list="order.medias" :initial-index="index" style="width: 75px; height: 75px" />
                </div>
              </div>
              <div v-if="order.shopReply" c-bg-eee c-pb-8 c-pt-8 c-pl-12 c-pr-12 c-mt-6>商家回复：{{ order.shopReply }}</div>
            </div>
          </div>
        </template>
        <ToTopGoCar />
        <div c-mt-28 c-mb-43 flex justify-center>
          <el-pagination
            background
            layout="prev, pager, next"
            :total="+pageConfig.total"
            :current-page="pageConfig.current"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
    <div v-show="showVideoComment" class="video-container">
      <div class="video-container__box">
        <div class="video-container__close" @click="videoCommentEnd">
          <QIcon svg name="icon-icon-close1" size="25px" style="width: 25px; height: 25px" />
        </div>
        <video ref="commentVideo" class="video-container__video" :src="videoSrc" controls @canplay="playCommentVideo" />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@include b(video-container) {
  z-index: 99;
  display: flex;
  justify-content: center;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: hsla(0, 0%, 1%, 0.7);
  @include e(box) {
    position: absolute;
    top: 12%;
    max-width: 800px;
  }
  @include e(close) {
    display: flex;
    display: flex;
    justify-content: flex-end;
    color: #fff;
    margin-right: -22px;
  }
  @include e(video) {
    max-width: 800px;
    max-height: 600px;
    opacity: 1;
  }
}
</style>
