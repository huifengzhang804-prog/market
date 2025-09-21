import { defineStore } from 'pinia'
import { decorationState } from './state'
import storage from '@/utils/Storage'
import type { SubmitForm, PageType, DecorationType, EndpointType } from '@/views/decoration/on-line-set/types'
const $storage = new storage()
export const useDecorationStore = defineStore('settingStore', {
    state: () => decorationState,
    actions: {
        SET_ISUSERCENTER(data: number) {
            this.isUsercenterCompontents = false
            this.userCenterType = ''
            this.activeTab = data
        },
        SET_LINK_FORM(data: string) {
            this.getFrom = data
        },
        SET_ACTIVE_COMINDEX(data: number) {
            this.activeComIndex = data
        },
        SET_ACTIVE_PAGE_TYPE(type: PageType) {
            this.activePageType = type
        },
        SET_ACTIVE_PAGE(page: SubmitForm) {
            this.activePage = page
        },
        SET_DEC_TYPE(type: DecorationType) {
            this.decorationType = type
            $storage.setItem('decorationType', type, 60 * 60 * 24)
        },
        SET_ENDPOINT_TYPE(type: EndpointType) {
            this.endpointType = type
            $storage.setItem('endpointType', type, 60 * 60 * 24)
        },
    },
    getters: {
        getterDecType(): DecorationType {
            if (this.decorationType === '') {
                if (!$storage.getItem('decorationType')) {
                    return ''
                } else {
                    return $storage.getItem('decorationType')
                }
            } else {
                return this.decorationType
            }
        },
        getEndpointType(): EndpointType {
            if (this.endpointType === '') {
                if (!$storage.getItem('endpointType')) {
                    return ''
                } else {
                    return $storage.getItem('endpointType')
                }
            } else {
                return this.endpointType
            }
        },
    },
})
