package com.medusa.gruul.goods.service.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jipeng
 * @since 2024/7/23
 */
public class CopyApiTest {

  public static void main(String[] args) {
    String html= """
             <p><img style="width:auto;height:auto;max-width:100%;" data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/206020/28/14155/142805/629aeeadEc537fed9/43c68fe1011f30d3.jpg"><img style="width:auto;height:auto;max-width:100%;" data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/62188/5/18512/140077/629aeeadEf51f0d63/b617eac0b3c264b8.jpg"><img style="width:auto;height:auto;max-width:100%;" data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/62990/22/18200/157101/629aeeadE6d8c4e2a/038d6ced43a46ee2.jpg"><img style="width:auto;height:auto;max-width:100%;" data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/59569/9/18091/148169/629aeeaeE9bb0e842/71d4e5a77171f056.jpg"><img style="width:auto;height:auto;max-width:100%;" data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/43794/6/17672/121371/629aeeaeE28c5f671/3c8debb1bb710f7c.jpg"><img data-lazyload="//img10.360buyimg.com/imgzone/jfs/t1/206951/5/32982/134444/64e220c9F792f7d0c/896151ca6df98acd.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/65404/38/18631/140855/629aeeaeE1a712d6c/0f307b9023192d55.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/46127/19/18553/122819/629aeeaeE4d31a5fe/74751259eb08f8d8.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/154468/16/23675/117954/629aeeaeEce2c838c/ea847fa0849a9ba6.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/197545/28/34639/73742/64f7166cF20139bdc/4d73525b2770a22b.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/56728/10/18877/106713/629aeeafE38cbda80/7429de7466196536.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/181016/23/24551/88266/629aeeafE42bd38d5/0e5e8c4d5491ff4a.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/89442/38/27105/94814/629aeeafE071c833c/5bed7ea62f39fe84.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/197185/16/24662/88318/629aeeafE33fea670/b9e13f1e6d8b6c17.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/81255/15/19569/91683/629aeeafEda99c40d/c4db25acf86cdf4a.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/118641/37/26050/119606/629aeeb0Ec8ccd819/d72a0f52f9cd836d.jpg" style="width: auto; height: auto; max-width: 100%;"><img data-lazyload="http://img30.360buyimg.com/popWareDetail/jfs/t1/150685/2/23821/104446/629aeeb0Ee87fac94/5b77837e4345b788.jpg" style="width: auto; height: auto; max-width: 100%;"></p><p><br></p><br/>
             """;
    // 正则表达式
    String imgRegex = "data-lazyload=\"(https?://[^\"]+)\"";
    Pattern pattern = Pattern.compile(imgRegex);
    Matcher matcher = pattern.matcher(html);

    // 查找并打印所有匹配的URL
    while (matcher.find()) {
      System.out.println(matcher.group(1));
    }
  }

  public static void main1(String[] args) {
//    String content= """
//        <img class="shop-editor-floor id-W8jSiHin7QEX7cGw2pmxJ" floor-id="id-W8jSiHin7QEX7cGw2pmxJ" data-lazyload="https://img10.360buyimg.com/img/jfs/t1/193706/4/46224/176454/666baafdF578d0e93/d49022d51e4a41da.jpg" style="max-width:100%;display:;margin:"/><img class="shop-editor-floor id-avaUl0TsH1LaYKogWamwB" floor-id="id-avaUl0TsH1LaYKogWamwB" data-lazyload="https://img10.360buyimg.com/img/jfs/t1/173706/1/46689/202614/666baaa9F1a2c179c/7d2358deb7f89715.jpg" style="max-width:100%;display:;margin:"/><img class="shop-editor-floor id-S5zlueR4RovHBmgonMvsP" floor-id="id-S5zlueR4RovHBmgonMvsP" data-lazyload="https://img10.360buyimg.com/img/jfs/t1/247246/3/11494/222839/666baaa9F35d87b10/ca730b93accee7b9.jpg" style="max-width:100%;display:;margin:"/><br/>
//        """;
//    String a=content.replaceAll("data-lazyload=", "src=");
//    System.out.println("a = " + a);
    String text ="""
        "<style>.ssd-module-wrap{position:relative;margin:0 auto;width:750px;text-align:left;background-color:#fff}.ssd-module-wrap .ssd-module,.ssd-module-wrap .ssd-module-heading{width:750px;position:relative;overflow:hidden}.ssd-module-wrap .ssd-floor-activity{max-height:380px;overflow:hidden}.ssd-module-wrap .ssd-floor-shopPrior{max-height:900px;overflow:hidden}.ssd-module-wrap .ssd-floor-authorize{max-height:300px;overflow:hidden}.ssd-module-wrap .ssd-floor-reminder{max-height:4000px;overflow:hidden}.ssd-module-wrap .ssd-module{background-repeat:no-repeat;background-position:left top;background-size:100% 100%}.ssd-module-wrap .ssd-module-heading{background-repeat:no-repeat;background-position:left center;background-size:100% 100%}.ssd-module-wrap .ssd-module-heading .ssd-module-heading-layout{display:inline-block}.ssd-module-wrap .ssd-module-heading .ssd-widget-heading-ch{float:left;display:inline-block;margin:0 6px 0 15px;height:100%}.ssd-module-wrap .ssd-module-heading .ssd-widget-heading-en{float:left;display:inline-block;margin:0 15px 0 6px;height:100%}.ssd-module-wrap .ssd-widget-circle,.ssd-module-wrap .ssd-widget-line,.ssd-module-wrap .ssd-widget-pic,.ssd-module-wrap .ssd-widget-rectangle,.ssd-module-wrap .ssd-widget-table,.ssd-module-wrap .ssd-widget-text,.ssd-module-wrap .ssd-widget-triangle{position:absolute;overflow:hidden}.ssd-module-wrap .ssd-widget-rectangle{box-sizing:border-box;-moz-box-sizing:border-box;-webkit-box-sizing:border-box}.ssd-module-wrap .ssd-widget-triangle svg{width:100%;height:100%}.ssd-module-wrap .ssd-widget-table table{width:100%;height:100%}.ssd-module-wrap .ssd-widget-table td{position:relative;white-space:pre-line;word-break:break-all}.ssd-module-wrap .ssd-widget-pic img{display:block;width:100%;height:100%}.ssd-module-wrap .ssd-widget-text{line-height:1.5;word-break:break-all}.ssd-module-wrap .ssd-widget-text span{display:block;overflow:hidden;width:100%;height:100%;padding:0;margin:0;word-break:break-all;word-wrap:break-word;white-space:normal}.ssd-module-wrap .ssd-widget-link{position:absolute;left:0;top:0;width:100%;height:100%;background:0 0;z-index:100}.ssd-module-wrap .ssd-cell-text{position:absolute;top:0;left:0;right:0;width:100%;height:100%;overflow:auto}.ssd-module-wrap .ssd-show{display:block}.ssd-module-wrap .ssd-hide{display:none}.ssd-module-wrap .M17114418615161{width:750px; background-color:#e9e9e9; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/160919/5/41993/155556/65fa9e05Fe2cbbe6d/1eb8ac2775ed9f4f.jpg); height:1324px}
        .ssd-module-wrap .M17114418615332{width:750px; background-color:#cbcbcb; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/172857/18/39378/220792/65fa9e05F45cbc88e/762415857d69996e.jpg); height:1324px}
        .ssd-module-wrap .M17114418615473{width:750px; background-color:#d7d7d7; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/16523/26/20175/340949/65fa9e05F93e45cbb/94bd22eb1dee0dfb.jpg); height:1323px}
        .ssd-module-wrap .M17114418615614{width:750px; background-color:#b3b3b3; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/88350/26/45910/292278/65fa9e05Fa004780b/651f1ee979f5fbc6.jpg); height:1324px}
        .ssd-module-wrap .M17114418615775{width:750px; background-color:#cbcbcb; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/196762/19/44169/128127/65fa9e05Fe69a8a7a/364eed37fc8b3e88.jpg); height:1324px}
        .ssd-module-wrap .M17114418615936{width:750px; background-color:#d7d7d7; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/247502/5/6165/174960/65fa9e05F1d9763a8/90f14bf9c043df1b.jpg); height:1324px}
        .ssd-module-wrap .M17114418616277{width:750px; background-color:#e9e9e9; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/245338/19/5968/165776/65fa9e05F73980cb6/c405491904cfe728.jpg); height:1324px}
        .ssd-module-wrap .M17114418616548{width:750px; background-color:#d7d7d7; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/101484/27/48708/152520/65fa9e05Fef1645ae/2ccc308b75c90fe5.jpg); height:1323px}
        .ssd-module-wrap .M17114418616819{width:750px; background-color:#b3b3b3; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/184944/25/43262/139530/65fa9e05F4cb4466c/250a586a38ba832c.jpg); height:1324px}
        .ssd-module-wrap .M171144186170910{width:750px; background-color:#b3b3b3; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/236221/18/14169/157980/65fa9e05F189e9880/05ab71e3772c6a42.jpg); height:1324px}
        .ssd-module-wrap .M171144186174112{width:750px; background-color:#cbcbcb; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/159221/24/23486/192269/65fa9e05F2d537d68/b54a5b0abd5ddd83.jpg); height:1474px}
        .ssd-module-wrap .M171144186176013{width:750px; background-color:#cbcbcb; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/218334/32/36971/150873/65fa9e05F67a3a75d/f5ebf555d8d05ae4.jpg); height:1473px}
        .ssd-module-wrap .M171144186178115{width:750px; background-color:#cbcbcb; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/134380/19/36120/134127/65fa9e05F4ef68b76/14d773d8ec3ac802.jpg); height:1474px}
        .ssd-module-wrap .M171144186182317{width:750px; background-color:#d7d7d7; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/66994/8/24747/181673/65fa9e05F8cc8f405/93a03a84f34233a9.jpg); height:1473px}
        .ssd-module-wrap .M171144186184518{width:750px; background-color:#f2f2f2; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/99135/25/48981/136751/65fa9e05F7cd88dd2/9d660f5342d88a02.jpg); height:1474px}
        .ssd-module-wrap .M171144186186819{width:750px; background-color:#b3b3b3; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/179386/26/43120/131880/65fa9e05F7679d342/44c1521daa984fdd.jpg); height:1474px}
        .ssd-module-wrap .M171144186188920{width:750px; background-color:#f2f2f2; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/216957/22/38671/315084/65fa9e05Fc5461d5b/f6431982d7a60a20.jpg); height:1473px}
        .ssd-module-wrap .M171144186191321{width:750px; background-color:#f2f2f2; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/107797/33/48709/403696/65fa9e05Fb6bcc798/d5667b6c979dace8.jpg); height:1474px}
        .ssd-module-wrap .M171144186193523{width:750px; background-color:#e9e9e9; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/161083/14/42887/209812/65fa9e05Fb39cf52b/f983e3da191e8cf4.jpg); height:1473px}
        .ssd-module-wrap .M171144186195824{width:750px; background-color:#f2f2f2; background-size:100% 100%; background-image:url(//img30.360buyimg.com/sku/jfs/t1/107927/38/41964/166556/66037599F122e1e25/223eb7caf04e14b8.png); height:1474px}
        </style><br/><div cssurl='//sku-market-gw.jd.com/css/pc/100093385191.css?t=1718714333257'></div><div id='zbViewModulesH'  value='27974'></div><input id='zbViewModulesHeight' type='hidden' value='27974'/><div skudesign="100010"></div><div class="ssd-module-wrap" >
                    <div class="ssd-module M17114418615161 animate-M17114418615161" data-id="M17114418615161">
               \s
        </div>
        <div class="ssd-module M17114418615332 animate-M17114418615332" data-id="M17114418615332">
               \s
        </div>
        <div class="ssd-module M17114418615473 animate-M17114418615473" data-id="M17114418615473">
               \s
        </div>
        <div class="ssd-module M17114418615614 animate-M17114418615614" data-id="M17114418615614">
               \s
        </div>
        <div class="ssd-module M17114418615775 animate-M17114418615775" data-id="M17114418615775">
               \s
        </div>
        <div class="ssd-module M17114418615936 animate-M17114418615936" data-id="M17114418615936">
               \s
        </div>
        <div class="ssd-module M17114418616277 animate-M17114418616277" data-id="M17114418616277">
               \s
        </div>
        <div class="ssd-module M17114418616548 animate-M17114418616548" data-id="M17114418616548">
               \s
        </div>
        <div class="ssd-module M17114418616819 animate-M17114418616819" data-id="M17114418616819">
               \s
        </div>
        <div class="ssd-module M171144186170910 animate-M171144186170910" data-id="M171144186170910">
               \s
        </div>
        <div class="ssd-module M171144186174112 animate-M171144186174112" data-id="M171144186174112">
               \s
        </div>
        <div class="ssd-module M171144186176013 animate-M171144186176013" data-id="M171144186176013">
               \s
        </div>
        <div class="ssd-module M171144186178115 animate-M171144186178115" data-id="M171144186178115">
               \s
        </div>
        <div class="ssd-module M171144186182317 animate-M171144186182317" data-id="M171144186182317">
               \s
        </div>
        <div class="ssd-module M171144186184518 animate-M171144186184518" data-id="M171144186184518">
               \s
        </div>
        <div class="ssd-module M171144186186819 animate-M171144186186819" data-id="M171144186186819">
               \s
        </div>
        <div class="ssd-module M171144186188920 animate-M171144186188920" data-id="M171144186188920">
               \s
        </div>
        <div class="ssd-module M171144186191321 animate-M171144186191321" data-id="M171144186191321">
               \s
        </div>
        <div class="ssd-module M171144186193523 animate-M171144186193523" data-id="M171144186193523">
               \s
        </div>
        <div class="ssd-module M171144186195824 animate-M171144186195824" data-id="M171144186195824">
               \s
        </div>
                
            </div>
        <!-- 2024-03-27 09:26:10 --> <br/><script></script><br/>"
        """;
    // 匹配 background-image:url(...) 的正则表达式
    String regex = "background-image\\s*:\\s*url\\(([^)]+)\\)";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);

    // 用于存储提取的URL
    List<String> urls = new ArrayList<>();

    // 提取匹配的URL
    while (matcher.find()) {
      String url = matcher.group(1).replaceAll("^['\"]|['\"]$", ""); // 去掉引号
      urls.add(url);
    }

    // 输出提取的URL
    for (String url : urls) {
      System.out.println(url);
    }
  }

}
