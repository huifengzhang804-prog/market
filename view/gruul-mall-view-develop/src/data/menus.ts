
const menus:Array<Menu> = [
    {
        id: 1,
        name: '用户管理',
        icon: 'el-icon-platform-eleme',
        type: 0,
        path: "",
        children: [
            {
                id: 11,
                name: '用户查询',
                icon: 'el-icon-eleme',
                type: 1,
                path: "/home1",
            },
            {
                id: 12,
                name: '用户编辑',
                icon: 'el-icon-delete-solid',
                type: 0,
                path: "",
                children:[
                    {
                        id: 121,
                        name: '新增用户',
                        icon: 'el-icon-delete',
                        type: 0,
                        path: "/query/1",
                    }
                ]
            }
        ]
    },
    {
        id: 2,
        name: '部门管理',
        icon: 'el-icon-s-tools',
        type: 0,
        path: "",
        children:[
            {
                id: 21,
                name: '部门查询',
                icon: 'el-icon-user',
                type: 2,
                path: "/home2",
            }
        ]
    }
]

export default menus;