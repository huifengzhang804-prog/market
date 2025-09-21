import { Howl } from 'howler'
/**
 * 播放声音
 * @param url 可以使用相对路径或全路径
 * 相对路径 相对于当前目录 ./uils目录
 */
export const playSound = (url: string) => {
    return new Promise<void>((resolve, reject) => {
        const howler = new Howl({
            src: [url],
        })
        howler.on('play', () => {
            setTimeout(resolve, 2000)
        })
        howler.on('playerror', reject)
        howler.on('loaderror', reject)
        howler.play()
    })
}
