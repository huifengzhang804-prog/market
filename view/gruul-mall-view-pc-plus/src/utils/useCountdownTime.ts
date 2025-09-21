export function useCountdownTime(startTime: string, timeOut: number) {
  const createDate = new Date(startTime).getTime()
  const timeDifference = createDate + timeOut * 1000 - new Date().getTime()
  const time = timeDifference > 0 ? timeDifference : 0
  return time
}
