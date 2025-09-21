let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'LiveRoomController',
    order: '1',
    link: '主播直播间控制器',
    desc: '主播直播间控制器',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-live/live/room/begin/{id}',
    desc: '查询是否有已经开播的直播间',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: '/addon-live/live/room/create',
    desc: '创建直播间',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: '/addon-live/live/room/anchor/list',
    desc: '查询主播对应的直播间列表',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: '/addon-live/live/room/lower/broadcast/{id}',
    desc: '直播间下播',
});
api[0].list[0].list.push({
    order: '5',
    deprecated: 'false',
    url: '/addon-live/live/room/deleted/{id}',
    desc: '删除直播间',
});
api[0].list[0].list.push({
    order: '6',
    deprecated: 'false',
    url: '/addon-live/live/room/detail/{id}',
    desc: '直播间详情',
});
api[0].list[0].list.push({
    order: '7',
    deprecated: 'false',
    url: '/addon-live/live/room/userSig/{userId}',
    desc: '直播间聊天室userSig',
});
api[0].list.push({
    alias: 'LiveUserController',
    order: '2',
    link: '用户控制器',
    desc: '用户控制器',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-live/user/add/reservation',
    desc: '预约直播间',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: '/addon-live/user/add/follow',
    desc: '用户关注主播',
});
api[0].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: '/addon-live/user/follow/live/list',
    desc: '关注直播间列表',
});
api[0].list[1].list.push({
    order: '4',
    deprecated: 'false',
    url: '/addon-live/user/discover/live/list',
    desc: '用户发现直播间',
});
api[0].list[1].list.push({
    order: '5',
    deprecated: 'false',
    url: '/addon-live/user/message',
    desc: 'C端获取主播信息',
});
api[0].list[1].list.push({
    order: '6',
    deprecated: 'false',
    url: '/addon-live/user/random/view/{id}',
    desc: '用户随机获取一条直播间信息',
});
api[0].list[1].list.push({
    order: '7',
    deprecated: 'false',
    url: '/addon-live/user/characters',
    desc: '直播分享图文字转图片',
});
api[0].list[1].list.push({
    order: '8',
    deprecated: 'false',
    url: '/addon-live/user/viewership/{liveId}',
    desc: '用户进入直播间时，添加直播间观看人数',
});
api[0].list[1].list.push({
    order: '9',
    deprecated: 'false',
    url: '/addon-live/user/viewership/status/{anchorId}',
    desc: '用户是否关注 前端需要过滤一遍用户登陆状态',
});
api[0].list.push({
    alias: 'ManagerController',
    order: '3',
    link: '管理端控制器',
    desc: '管理端控制器',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-live/manager/live/list',
    desc: '管理端直播间列表',
});
api[0].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: '/addon-live/manager/anchor/list',
    desc: '管理端主播列表',
});
api[0].list[2].list.push({
    order: '3',
    deprecated: 'false',
    url: '/addon-live/manager/update/anchor/{id}/{isEnable}',
    desc: '启用/禁用主播',
});
api[0].list[2].list.push({
    order: '4',
    deprecated: 'false',
    url: '/addon-live/manager/platform/update/anchor',
    desc: '平台 恢复/违规禁播主播',
});
api[0].list[2].list.push({
    order: '5',
    deprecated: 'false',
    url: '/addon-live/manager/ban/reason/{id}/{type}',
    desc: '查看违禁原因',
});
api[0].list[2].list.push({
    order: '6',
    deprecated: 'false',
    url: '/addon-live/manager/add/anchor',
    desc: '添加主播',
});
api[0].list.push({
    alias: 'NotifyController',
    order: '4',
    link: '直播回调',
    desc: '直播回调',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-live/api/notify',
    desc: '直播回调',
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