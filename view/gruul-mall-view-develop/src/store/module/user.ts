import {Module} from "vuex";
import localStorage from "../LocalStorage";

const KEY = import.meta.env.VITE_LOCAL_STORAGE_KEY_PREFIX + '_user';

/**
 * 从localstorage读取数据
 */


const init = ():User => {
    const storeInfo = localStorage.get(KEY);
    if (!!storeInfo) {
        return JSON.parse(storeInfo)
    }
    return {
        username: '',
        name: '',
        token: '',
        avatar: '/src/assets/image/avatar/standard_'+Math.floor(Math.random()*(3))+'.png',
        menus: [],
        buttons: new Map<String, Button>()
    };
}
const state = init();

const user:Module<User,string> = {
    namespaced:true,
    state,
    mutations: {
        clear: (state: User) => {
            console.log(state);
            state.username = '';
            state.name ='';
            state.token ='';
            state.avatar = '';
            state.menus = [];
            state.buttons = new Map<String, Button>();
            localStorage.remove(KEY);
        },
        username: (state: User, username: string) => {
            state.username = username;
            localStorage.set(KEY, state)
        },
        name: (state: User, name: string) => {
            state.name = name;
            localStorage.set(KEY, state)
        },
        token: (state: User, token: string) => {
            console.log("gettoken")
            state.token = token
            localStorage.set(KEY, state)
        },
        avatar: (state: User, avatar: string) => {
            state.avatar = avatar
            localStorage.set(KEY, state)
        },
        menus: (state: User, menus: Array<Menu>) => {
            state.menus = menus
            localStorage.set(KEY, state)
        },
        buttons: (state: User, buttons: Map<String, Button>) => {
            state.buttons = buttons
            localStorage.set(KEY, state)
        }
    },

    getters: {
        username: (state: User) => state.username,
        name: (state: User) => state.name,
        token: (state: User) => state.token,
        avatar: (state: User) => state.avatar,
        menus: (state: User) => state.menus,
        buttons: (state: User) => state.buttons
    },
    actions:{

    }
};
export default user
