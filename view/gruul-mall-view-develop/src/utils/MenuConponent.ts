/*
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-15 09:24:01
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-03-16 10:38:12
 */
/**
 * 菜单插件渲染函数
 * @author 张治保
 */

import {defineComponent} from 'vue'


const getType = name => {
    const lowerCase = name.toLowerCase;
    return lowerCase === 'string' ? String :
        lowerCase === 'number' ? Number :
            lowerCase === 'array' ? Array :
                lowerCase === 'boolean' ? Boolean : Object;


}
const getStyle = style => {
    return !!style ? '<div v-is="style" v-html="' + style + '"></div>' : ''
}

const getProps = compProps => {
    if (!compProps) {
        return {};
    }
    const props = {};
    for (const key in compProps) {
        props[key] = getType(compProps[key])
    }
    return props;
}

const getData = data => {
    return !data ? () => {
        return {}
    } : () => {
        return data
    };
}

const getComponents = comps => {
    if (!comps) {
        return {}
    }
    const components = {};
    comps.forEach(
        comp => {
            components[comp.name] = define(comp);
        }
    )
    return components;
}

const getMethods = compMethods => {
    if (!compMethods) {
        return {}
    }
    return {};
}


const define = (comp) => {
    return defineComponent({
        name: comp.name,
        template: getStyle(comp.style) + comp.template,
        data: getData(comp.data),
        props: getProps(comp.props),
        components: getComponents(comp.components),
        methods: getMethods(comp.methods),
    })
}

export {
    getStyle,
    getProps,
    getData,
    getComponents,
    define
};