interface Menu {
    id:number;
    name:string,
    icon:string,
    type:number,
    path:string,
    children?:Array<Menu>
}

interface Button {
    name:string
    type:string
    icon:string
}

declare interface User {
    username:string
    name:string
    token: string
    avatar: string|undefined
    menus: Array<Menu>
    buttons: Map<String,Button>
}

declare interface Aside{
    show: boolean,
}
declare interface System {
    leftAside:Aside,
    rightAside:Aside,
}