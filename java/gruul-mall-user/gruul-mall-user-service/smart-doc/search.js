let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'MemberPurchaseController',
    order: '1',
    link: '',
    desc: '',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/member/purchase/list',
    desc: '会员流水列表',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/member/purchase/export',
    desc: '会员流水导出',
});
api[0].list.push({
    alias: 'MemberRightsController',
    order: '2',
    link: '用户会员权益控制层',
    desc: '用户会员权益控制层',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/member/rights/',
    desc: '会员权益获取',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/member/rights/update',
    desc: '会员权益修改',
});
api[0].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-user/user/member/rights/save',
    desc: '会员权益新增',
});
api[0].list[1].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-user/user/member/rights/del/{ids}',
    desc: '会员权益删除',
});
api[0].list[1].list.push({
    order: '5',
    deprecated: 'false',
    url: '/gruul-mall-user/user/member/rights/{rightsSwitch}',
    desc: '会员权益开启或者关闭',
});
api[0].list[1].list.push({
    order: '6',
    deprecated: 'false',
    url: '/gruul-mall-user/user/member/rights/usable',
    desc: '可用会员权益获取',
});
api[0].list[1].list.push({
    order: '7',
    deprecated: 'false',
    url: '/gruul-mall-user/user/member/rights/default',
    desc: '会员权益恢复默认',
});
api[0].list.push({
    alias: 'ShopUserAccountController',
    order: '3',
    link: '店铺客户列表前端控制器',
    desc: '店铺客户列表前端控制器',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/shopUserAccount/',
    desc: '分页查询店铺客户列表',
});
api[0].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/shopUserAccount/detail',
    desc: '查询店铺客户详情',
});
api[0].list.push({
    alias: 'UserAddressController',
    order: '4',
    link: '用户收货地址_前端控制器',
    desc: '用户收货地址 前端控制器',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/address/',
    desc: '分页查询用户收货地址',
});
api[0].list[3].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/address/{addressId}',
    desc: '根据收货地址ID获取地址详情',
});
api[0].list[3].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-user/user/address/default',
    desc: '获取默认收货地址',
});
api[0].list[3].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-user/user/address/',
    desc: '新增用户收货地址',
});
api[0].list[3].list.push({
    order: '5',
    deprecated: 'false',
    url: '/gruul-mall-user/user/address/{addressId}',
    desc: '根据id编辑收货地址',
});
api[0].list[3].list.push({
    order: '6',
    deprecated: 'false',
    url: '/gruul-mall-user/user/address/{addressId}',
    desc: '根据id编辑收货地址',
});
api[0].list[3].list.push({
    order: '7',
    deprecated: 'false',
    url: '/gruul-mall-user/user/address/{addressId}/{isDefault}',
    desc: '设置/取消默认地址',
});
api[0].list.push({
    alias: 'UserBalanceHistoryController',
    order: '5',
    link: '用户余额历史_控制层',
    desc: '用户余额历史 控制层',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/balance/list',
    desc: '用户储值流水列表查询',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/balance/remark',
    desc: '储值流水备注',
});
api[0].list[4].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-user/user/balance/export',
    desc: '储值流水导出',
});
api[0].list.push({
    alias: 'UserCollectController',
    order: '6',
    link: '用户收藏_前端控制器',
    desc: '用户收藏 前端控制器',
    list: []
})
api[0].list[5].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/collect/getUserCollectInfo',
    desc: '获取用户收藏',
});
api[0].list[5].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/collect/',
    desc: '用户收藏、取消收藏',
});
api[0].list[5].list.push({
    order: '3',
    deprecated: 'true',
    url: '/gruul-mall-user/user/collect/cancel',
    desc: '取消用户收藏   //TODO 2023 3 月底 该接口废弃  2.0.9版本将删除该接口',
});
api[0].list[5].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-user/user/collect/findUserIsCollect',
    desc: '查询用户是否对该商品进行收藏',
});
api[0].list[5].list.push({
    order: '5',
    deprecated: 'false',
    url: '/gruul-mall-user/user/collect/getUserCollectInfo/count',
    desc: '',
});
api[0].list.push({
    alias: 'UserDealDetailController',
    order: '7',
    link: '用户交易明细控制层',
    desc: '用户交易明细控制层',
    list: []
})
api[0].list[6].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/deal/detail/list/{userId}',
    desc: '用户交易明细列表',
});
api[0].list.push({
    alias: 'UserFootMarkController',
    order: '8',
    link: '用户足迹_前端控制器',
    desc: '用户足迹 前端控制器',
    list: []
})
api[0].list[7].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/FootMark/list',
    desc: '获取用户足迹',
});
api[0].list[7].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/FootMark/getFootMarkBrowsDates',
    desc: '根据月份获取有足迹的日期',
});
api[0].list[7].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-user/user/FootMark/',
    desc: '添加用户足迹',
});
api[0].list[7].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-user/user/FootMark/',
    desc: '足迹批量删除',
});
api[0].list.push({
    alias: 'UserGrowthValueSettingsController',
    order: '9',
    link: '会员成长值设置_前端控制器',
    desc: '会员成长值设置 前端控制器',
    list: []
})
api[0].list[8].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/growthValue/settings/',
    desc: '获取会员成长值设置',
});
api[0].list[8].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/growthValue/settings/',
    desc: '编辑会员成长值设置',
});
api[0].list.push({
    alias: 'UserGuessYouLikeController',
    order: '10',
    link: '猜你喜欢_前端控制器',
    desc: '猜你喜欢 前端控制器',
    list: []
})
api[0].list[9].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/GuessYouLike/list',
    desc: '猜你喜欢',
});
api[0].list.push({
    alias: 'UserMemberCardController',
    order: '11',
    link: '用户会员卡控制层',
    desc: '用户会员卡控制层',
    list: []
})
api[0].list[10].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/member/card/info',
    desc: '获取会员中心信息',
});
api[0].list.push({
    alias: 'UserSavingController',
    order: '12',
    link: '用户储蓄控制层',
    desc: '用户储蓄控制层',
    list: []
})
api[0].list[11].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/saving/pay',
    desc: '用户储值充值',
});
api[0].list.push({
    alias: 'UserSavingManageController',
    order: '13',
    link: '用户储蓄管理控制层',
    desc: '用户储蓄管理控制层',
    list: []
})
api[0].list[12].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/saving/manage/get',
    desc: '获取储值管理信息',
});
api[0].list[12].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/saving/manage/edit',
    desc: '编辑储值管理信息',
});
api[0].list[12].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-user/user/saving/manage/update/{status}',
    desc: '修改储值管理信息开关',
});
api[0].list.push({
    alias: 'UserTagController',
    order: '14',
    link: '会员标签_前端控制器',
    desc: '会员标签 前端控制器',
    list: []
})
api[0].list[13].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-user/user/userTag/',
    desc: '查询会员所有标签',
});
api[0].list[13].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-user/user/userTag/',
    desc: '设置会员标签',
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