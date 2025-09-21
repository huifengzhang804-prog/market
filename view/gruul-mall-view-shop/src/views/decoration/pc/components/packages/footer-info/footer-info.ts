export const logo = {
    img: '',
    logoInfo: [
        { title: '', link: '' },
        { title: '', link: '' },
        { title: '', link: '' },
    ],
}

export const moduleTitleDefault = () => ({ title: '', link: '' })

export const moduleDefault = () => ({ moduleName: '', moduleTitle: [moduleTitleDefault()] })

export const module = [moduleDefault()]

export default { logo, module, QRcode: '' }
