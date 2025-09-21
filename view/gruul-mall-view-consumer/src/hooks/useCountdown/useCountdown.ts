import { isRef, reactive, ref, type Ref, toRefs, unref, watch } from 'vue'
import DateUtil from '@/utils/date'
import type { Fn, MaybeRef } from '@/utils/types'

interface IntervalFnOptions {
  immediate?: boolean
  immediateCallback?: boolean
}

interface UseIntervalFn {
  pause: Fn
  resume: Fn
  clean: Fn
  isActive: Ref<boolean>
}

function useIntervalFn(cb: Fn, interval = 1000, options: IntervalFnOptions = {}): UseIntervalFn {
  const { immediate = true, immediateCallback = false } = options
  let timer: NodeJS.Timeout | null = null
  const isActive = ref(false)

  function clean() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  function pause() {
    isActive.value = false
    clean()
  }

  function resume() {
    if (interval <= 0) return
    isActive.value = true
    if (immediateCallback) cb()
    clean()
    timer = setInterval(cb, interval)
  }

  if (immediate) {
    resume()
  }
  return {
    isActive,
    pause,
    resume,
    clean,
  }
}

interface CountOptions {
  immediate?: boolean
  immediateCallback?: boolean
}

const dateUtil = new DateUtil()

export function useCountdown(endDate: MaybeRef<string | number>, options: CountOptions = {}, cb?: Fn) {
  const { immediate = false, immediateCallback = false } = options
  const timeSet = reactive({
    days: '00',
    hours: '00',
    minutes: '00',
    seconds: '00',
  })
  const isEnd = ref(false)
  const isStart = ref(false)
  const callbackFn = ref({
    resume: () => {},
    pause: () => {},
    clean: () => {},
  })
  if (isRef(endDate)) {
    watch(
      endDate,
      () => {
        update()
      },
      {
        immediate: true,
      },
    )
  } else {
    update()
  }

  function update() {
    const { resume, pause, clean, isActive } = useIntervalFn(doCount, 1000, { immediate, immediateCallback })
    callbackFn.value.pause = pause
    callbackFn.value.resume = resume
    callbackFn.value.clean = clean
    isEnd.value = isActive.value
  }

  function doCount() {
    isStart.value = true
    countdown(unref(endDate))
  }

  /**
   * 倒计时方法
   * @param {string} endDate 2020-02-02 15:12:13
   */
  function countdown(endDate: string | number) {
    const { days, hours, minutes, seconds } = toRefs(timeSet)
    const now = new Date().getTime()
    endDate = dateUtil.getTime(endDate).toString()
    const distance = Number(endDate) - now
    if (distance > 0) {
      days.value = String(Math.floor(distance / (1000 * 60 * 60 * 24))).padStart(2, '0')
      hours.value = String(Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))).padStart(2, '0')
      minutes.value = String(Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60))).padStart(2, '0')
      seconds.value = String(Math.floor((distance % (1000 * 60)) / 1000)).padStart(2, '0')
    } else {
      callbackFn.value.clean()
      days.value = '00'
      hours.value = '00'
      minutes.value = '00'
      seconds.value = '00'
      if (isStart.value) {
        isStart.value = false
        // isStart 记录开始过
        cb?.()
      }
    }

    return {
      days,
      hours,
      minutes,
      seconds,
    }
  }

  return {
    ...unref(callbackFn),
    timeSet,
  }
}
