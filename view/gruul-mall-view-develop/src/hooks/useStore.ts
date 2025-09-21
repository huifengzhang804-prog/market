/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-03-22 17:05:03
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-03-22 17:15:25
 */

import { computed } from "vue";
import { mapGetters, mapState, useStore, createNamespacedHelpers } from "vuex";
import type {MapperForState,Mapper,Computed} from "vuex"
const useMapper = (mapper:string[]|string, mapFn:Mapper<Computed> & MapperForState) => {
  const store = useStore();

  const storeStateFns = mapFn(mapper as string[]);
  const storeState = {} as any;
  Object.keys(storeStateFns).forEach((keyFn) => {
    const fn = storeStateFns[keyFn].bind({ $store: store });
    storeState[keyFn] = computed(fn);
  });

  return storeState;
};

export const useState = (moduleName:string[] | string, mapper:string[]|string) => {
  let mapperFn = mapState;
  if (typeof moduleName === "string" && moduleName.length > 0) {
    mapperFn = createNamespacedHelpers(moduleName).mapState;
  } else {
    mapper = moduleName;
  }
  return useMapper(mapper, mapperFn);
};

export const useGetters = (moduleName:string, mapper:string[]|string) => {
  let mapperFn = mapGetters;
  if (typeof moduleName === "string" && moduleName.length > 0) {
    mapperFn = createNamespacedHelpers(moduleName).mapGetters;
  } else {
    mapper = moduleName;
  }
  return useMapper(mapper, mapperFn);
};
