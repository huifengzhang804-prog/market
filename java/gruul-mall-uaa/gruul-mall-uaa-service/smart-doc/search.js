let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'AuthenticationController',
    order: '1',
    link: '认证控制器',
    desc: '认证控制器',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/switch',
    desc: '获取可切换登录的店铺列表',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/shop/{username}',
    desc: '获取有登录权限的店铺列表',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/captcha/sms',
    desc: '发送短信验证码',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/reset/my/password/sms',
    desc: '发送当前用户的 重置密码短信验证码',
});
api[0].list[0].list.push({
    order: '5',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/reset/my/password',
    desc: '重置当前用户密码',
});
api[0].list[0].list.push({
    order: '6',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/reset/{mobile}/password/sms',
    desc: '发送 重置密码 短信验证码',
});
api[0].list[0].list.push({
    order: '7',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/reset/password',
    desc: '重置密码',
});
api[0].list[0].list.push({
    order: '8',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/captcha/slider/new',
    desc: '生成滑块验证码或者直接发短信',
});
api[0].list[0].list.push({
    order: '9',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/captcha/slider',
    desc: '生成滑块验证码',
});
api[0].list[0].list.push({
    order: '10',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/auth/captcha/sms/{smsType}',
    desc: '根据类型发送短信验证码',
});
api[0].list.push({
    alias: 'MenuController',
    order: '2',
    link: '插件控制器',
    desc: '插件控制器',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/menu/navigate',
    desc: '查询导航列表 树结构',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/menu/dev',
    desc: '开发者 查询导航列表 树结构',
});
api[0].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/menu/',
    desc: '创建新菜单',
});
api[0].list[1].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/menu/{menuId}',
    desc: '编辑菜单',
});
api[0].list[1].list.push({
    order: '5',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/menu/{menuId}',
    desc: '删除菜单',
});
api[0].list.push({
    alias: 'RoleMenuController',
    order: '3',
    link: '角色菜单控制器',
    desc: '角色菜单控制器',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/role/menu/menus',
    desc: '查询可分配自定义管理员的菜单列表',
});
api[0].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/role/menu/',
    desc: '分页查询店铺橘色列表',
});
api[0].list[2].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/role/menu/',
    desc: '新增角色与权限关联关系',
});
api[0].list[2].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/role/menu/{roleId}',
    desc: '根据角色id获取角色对应的菜单id集合',
});
api[0].list[2].list.push({
    order: '5',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/role/menu/{roleId}',
    desc: '编辑角色菜单关联关系',
});
api[0].list[2].list.push({
    order: '6',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/role/menu/{roleId}',
    desc: '删除角色菜单对应关系',
});
api[0].list[2].list.push({
    order: '7',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/role/menu/{roleId}/{status}',
    desc: '',
});
api[0].list[2].list.push({
    order: '8',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/role/menu/role',
    desc: '获取用户子角色集合',
});
api[0].list.push({
    alias: 'ScanCodeController',
    order: '4',
    link: '扫描码生成策略',
    desc: '扫描码生成策略',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/scan/qrcode/redirect',
    desc: '生成普通二维码 小程序扫码可以跳小程序 h5扫码跳h5',
});
api[0].list.push({
    alias: 'UserDataController',
    order: '5',
    link: '个人资料控制器',
    desc: '个人资料控制器',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/user/data/account',
    desc: '查询我的账号资料',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/user/data/',
    desc: '查询个人资料',
});
api[0].list[4].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/user/data/{userId}',
    desc: '根据用户id查询用户资料',
});
api[0].list[4].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/user/data/',
    desc: '修改个人资料',
});
api[0].list[4].list.push({
    order: '5',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/user/data/update/authority',
    desc: '修改用户权限',
});
api[0].list[4].list.push({
    order: '6',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/user/data/import',
    desc: '批量导入用户',
});
api[0].list.push({
    alias: 'WechatController',
    order: '6',
    link: '',
    desc: '',
    list: []
})
api[0].list[5].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/wx/chat',
    desc: '获取小程序码',
});
api[0].list[5].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/wx/schema',
    desc: '获取小程序 schema url',
});
api[0].list[5].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-uaa/uaa/wx/jsapi/signature/{type}',
    desc: '创建调用jsapi时所需要的签名',
});
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code === 13) {
        const search = document.getElementById('search');
        const searchValue = search.value.toLocaleLowerCase();

        let searchGroup = [];
        for (let i = 0; i < api.length; i++) {

            let apiGroup = api[i];

            let searchArr = [];
            for (let i = 0; i < apiGroup.list.length; i++) {
                let apiData = apiGroup.list[i];
                const desc = apiData.desc;
                if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                    searchArr.push({
                        order: apiData.order,
                        desc: apiData.desc,
                        link: apiData.link,
                        list: apiData.list
                    });
                } else {
                    let methodList = apiData.list || [];
                    let methodListTemp = [];
                    for (let j = 0; j < methodList.length; j++) {
                        const methodData = methodList[j];
                        const methodDesc = methodData.desc;
                        if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                            methodListTemp.push(methodData);
                            break;
                        }
                    }
                    if (methodListTemp.length > 0) {
                        const data = {
                            order: apiData.order,
                            desc: apiData.desc,
                            link: apiData.link,
                            list: methodListTemp
                        };
                        searchArr.push(data);
                    }
                }
            }
            if (apiGroup.name.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchGroup.push({
                    name: apiGroup.name,
                    order: apiGroup.order,
                    list: searchArr
                });
                continue;
            }
            if (searchArr.length === 0) {
                continue;
            }
            searchGroup.push({
                name: apiGroup.name,
                order: apiGroup.order,
                list: searchArr
            });
        }
        let html;
        if (searchValue === '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchGroup,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            let $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiGroups, liClass, display) {
    let html = "";
    if (apiGroups.length > 0) {
        if (apiDocListSize === 1) {
            let apiData = apiGroups[0].list;
            let order = apiGroups[0].order;
            for (let j = 0; j < apiData.length; j++) {
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+order+'_'+apiData[j].order+'_' + apiData[j].link + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                html += '<ul class="sectlevel2" style="'+display+'">';
                let doc = apiData[j].list;
                for (let m = 0; m < doc.length; m++) {
                    let spanString;
                    if (doc[m].deprecated === 'true') {
                        spanString='<span class="line-through">';
                    } else {
                        spanString='<span>';
                    }
                    html += '<li><a href="#_'+order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                }
                html += '</ul>';
                html += '</li>';
            }
        } else {
            for (let i = 0; i < apiGroups.length; i++) {
                let apiGroup = apiGroups[i];
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+apiGroup.order+'_' + apiGroup.name + '">' + apiGroup.order + '.&nbsp;' + apiGroup.name + '</a>';
                html += '<ul class="sectlevel1">';

                let apiData = apiGroup.list;
                for (let j = 0; j < apiData.length; j++) {
                    html += '<li class="'+liClass+'">';
                    html += '<a class="dd" href="#_'+apiGroup.order+'_'+ apiData[j].order + '_'+ apiData[j].link + '">' +apiGroup.order+'.'+ apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                    html += '<ul class="sectlevel2" style="'+display+'">';
                    let doc = apiData[j].list;
                    for (let m = 0; m < doc.length; m++) {
                       let spanString;
                       if (doc[m].deprecated === 'true') {
                           spanString='<span class="line-through">';
                       } else {
                           spanString='<span>';
                       }
                       html += '<li><a href="#_'+apiGroup.order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">'+apiGroup.order+'.' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                   }
                    html += '</ul>';
                    html += '</li>';
                }

                html += '</ul>';
                html += '</li>';
            }
        }
    }
    return html;
}