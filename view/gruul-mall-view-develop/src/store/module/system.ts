import {Module} from "vuex";
const KEY = import.meta.env.VITE_LOCAL_STORAGE_KEY_PREFIX + '_system';

const state: System = {
    leftAside: {
        show: true,
    },
    rightAside: {
        show: true,
    }
}

const system: Module<System, string> = {
    namespaced: true,
    state,
    mutations: {
        leftAside(state,leftAside:Aside){
            state.leftAside = leftAside;
        },
        rightAside(state,rightAside:Aside){
            state.rightAside = rightAside;
        }
    },
    getters: {},
    actions: {}
}
export default system;