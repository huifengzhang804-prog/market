import storage from '@/utils/storage'

enum STORAGE_KEY {
  liveWords = 'liveWords',
  historyWords = 'historyWords',
}
type JointStorageKey = keyof typeof STORAGE_KEY
/**
 * 历史记录搜索放入storage
 */
function addStorage(value: string, key: JointStorageKey) {
  if (!value || !value.trim()) return
  const historyWordStorage = storage.get(key) || []
  const sameWordIndex = historyWordStorage.findIndex((word: string) => word === value)
  if (sameWordIndex !== -1) historyWordStorage.splice(sameWordIndex, 1)
  historyWordStorage.unshift(value)
  if (historyWordStorage.length > 6) historyWordStorage.pop()
  storage.set(key, historyWordStorage)
  return historyWordStorage
}
export { STORAGE_KEY, type JointStorageKey, addStorage }
