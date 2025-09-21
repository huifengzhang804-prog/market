let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'OrderConfigController',
    order: '1',
    link: '订单配置控制器',
    desc: '订单配置控制器',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-order/order/config/timeout',
    desc: '订单超时时间配置',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-order/order/config/timeout',
    desc: '查询订单超时时间配置',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-order/order/config/form',
    desc: '商铺交易信息编辑',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-order/order/config/form',
    desc: '店铺交易信息',
});
api[0].list[0].list.push({
    order: '5',
    deprecated: 'false',
    url: '/gruul-mall-order/order/config/form/batch',
    desc: '根据店铺id列表 批量查询下单设置',
});
api[0].list.push({
    alias: 'ReceiverController',
    order: '2',
    link: '收货人控制器',
    desc: '收货人控制器',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-order/order/{orderNo}/receiver/',
    desc: '修改订单收货人信息',
});
api[0].list.push({
    alias: 'OrderPayController',
    order: '3',
    link: '订单支付',
    desc: '订单支付',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-order/order/pay/page',
    desc: '获取渲染支付数据',
});
api[0].list.push({
    alias: 'PrintTaskController',
    order: '4',
    link: '打印任务控制器',
    desc: '打印任务控制器',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-order/order/print/task/',
    desc: '新增或编辑打印任务',
});
api[0].list[3].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-order/order/print/task/page',
    desc: '分页查询打印任务',
});
api[0].list[3].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-order/order/print/task/delete',
    desc: '删除打印任务',
});
api[0].list[3].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-order/order/print/task/print',
    desc: '商家手动打印订单',
});
api[0].list.push({
    alias: 'PrinterOpenApiConfigController',
    order: '5',
    link: '打印机_开放平台_api_配置',
    desc: '打印机 开放平台 api 配置',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-order/order/printer/open/api/feie',
    desc: '配置飞鹅打印机开放平台配置',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-order/order/printer/open/api/feie/get',
    desc: '获取飞鹅打印机开放平台配置',
});
api[0].list.push({
    alias: 'PrintTemplateController',
    order: '6',
    link: '打印模板控制器',
    desc: '打印模板控制器',
    list: []
})
api[0].list[5].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-order/order/print/template/',
    desc: '新增或编辑打印模板',
});
api[0].list[5].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-order/order/print/template/page',
    desc: '分页查询打印模板',
});
api[0].list[5].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-order/order/print/template/detail',
    desc: '根据 模板id查询模板详情',
});
api[0].list[5].list.push({
    order: '4',
    deprecated: 'false',
    url: '/gruul-mall-order/order/print/template/delete',
    desc: '根据模板 id删除打印模板',
});
api[0].list.push({
    alias: 'PrinterController',
    order: '7',
    link: '打印机控制器',
    desc: '打印机控制器',
    list: []
})
api[0].list[6].list.push({
    order: '1',
    deprecated: 'false',
    url: '/gruul-mall-order/order/printer/',
    desc: '添加或编辑打印机',
});
api[0].list[6].list.push({
    order: '2',
    deprecated: 'false',
    url: '/gruul-mall-order/order/printer/page',
    desc: '打印机分页查询',
});
api[0].list[6].list.push({
    order: '3',
    deprecated: 'false',
    url: '/gruul-mall-order/order/printer/delete',
    desc: '删除打印机 已绑定打印任务的打印机不可删除',
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