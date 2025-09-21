let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'UuptController',
    order: '1',
    link: '',
    desc: '',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-intra-city/ic/uupt/open/config',
    desc: '设置 uupt 开放平台配置',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: '/addon-intra-city/ic/uupt/open/config/get',
    desc: '获取 uupt 开放平台配置',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: '/addon-intra-city/ic/uupt/shop/status',
    desc: '查询店铺 uupt 账号激活状态',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: '/addon-intra-city/ic/uupt/shop/sms',
    desc: '商家发送短信验证码进行授权',
});
api[0].list[0].list.push({
    order: '5',
    deprecated: 'false',
    url: '/addon-intra-city/ic/uupt/shop/auth',
    desc: '商家用户授权',
});
api[0].list.push({
    alias: 'ICShopConfigController',
    order: '2',
    link: '',
    desc: '',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/config/',
    desc: '保存店铺同城配置',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/config/get',
    desc: '查询店铺同城配置信息',
});
api[0].list.push({
    alias: 'ICShopOrderController',
    order: '3',
    link: '',
    desc: '',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/uupt/callback',
    desc: 'UU跑腿回调接口',
});
api[0].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/page',
    desc: '分页查询商家配送单',
});
api[0].list[2].list.push({
    order: '3',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/take',
    desc: '店员接单',
});
api[0].list[2].list.push({
    order: '4',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/offer',
    desc: '店员取消接单',
});
api[0].list[2].list.push({
    order: '5',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/status/next',
    desc: '订单更新为下个状态',
});
api[0].list[2].list.push({
    order: '6',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/deliver/type',
    desc: '获取店铺可选择的同城配送方式',
});
api[0].list[2].list.push({
    order: '7',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/deliver/price',
    desc: '批量获取配送单运费价格 当选择UU 跑腿作为配送方时可用',
});
api[0].list[2].list.push({
    order: '8',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/deliver/info',
    desc: '获取指定订单的配送详情',
});
api[0].list[2].list.push({
    order: '9',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/courier/uupt',
    desc: '获取UU跑腿配送员最新信息和定位',
});
api[0].list[2].list.push({
    order: '10',
    deprecated: 'false',
    url: '/addon-intra-city/ic/shop/order/',
    desc: '同城单异常处理',
});
api[0].list.push({
    alias: 'NotifyCallbackController',
    order: '4',
    link: '',
    desc: '',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-intra-city/',
    desc: '',
});
api[0].list.push({
    alias: 'CommonConfigController',
    order: '5',
    link: '',
    desc: '',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: '/addon-intra-city/common/config/{type}/',
    desc: '',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: '/addon-intra-city/common/config/{type}/',
    desc: '',
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