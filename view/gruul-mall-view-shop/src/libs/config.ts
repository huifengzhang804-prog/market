export const getConfig = () => {
    return {
        TooltipEffect: 'light',
        Title: import.meta.env.VITE_PLATFORM_NAME || '启山智软',
        SidebarStatus: true,
        Layout: 'vertical',
        MenuArrowIconNoTransition: false,
    }
}
