import { defineStore } from 'pinia'
import { decorationState } from './state'
import storage from '@/utils/Storage'
import type { SubmitForm, PageType, DecorationType, EndpointType } from '@/views/decoration/platformDecoration/mobile/types'
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
        SET_ACTIVE_PAGE_ID(id: string) {
            this.activePage.id = id
        },
        SET_ENDPOINT_TYPE(type: EndpointType) {
            this.endpointType = type
            $storage.setItem('endpointType', type, 60 * 60 * 24)
        },
        SET_TEMPLATE_TYPE(type: 'PLATFORM' | 'SHOP' | string) {
            this.templateType = type
            $storage.setItem('templateType', type, 60 * 60 * 24)
        },
    },
    getters: {
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
        getTemplateType(): 'PLATFORM' | 'SHOP' | string {
            if (this.templateType === '') {
                if (!$storage.getItem('templateType')) {
                    return ''
                } else {
                    return $storage.getItem('templateType')
                }
            } else {
                return this.templateType
            }
        },
    },
})
