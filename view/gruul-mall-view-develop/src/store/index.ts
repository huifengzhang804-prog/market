/**
 * vuex localstorage 状态管理/状态缓存
 * @author 张治保
 */
import {createStore} from 'vuex'
import {FileHelper} from "../utils/FileHelper";

const files: Record<string, { [key: string]: any }> = import.meta.globEager('./module/*');
const modules = {};
for (const key in files) {
    const fullpath = <string>key;
    const fileName = FileHelper.getFileName(fullpath);
    console.log(fileName)
    let module;
    if (!!fileName && !!(module = files[fullpath].default)) {
        // @ts-ignore
        modules[fileName] = module
    }
}
// 调用createStore
const stateStore = createStore(
    {
        modules
    }
);
export default stateStore