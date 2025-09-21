package com.medusa.gruul.goods.service.controller.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.goods.service.model.copy.AlProduct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * @author Administrator
 * @since 2024/3/7
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@AutoConfigureMockMvc
public class ApiProductControllerTest {



    /**
     * 商品详情，sku切换测试，需要启动相关RPC，验证相关
     */
    @Test
    void getProductChangeSkuInfoTest(@Autowired MockMvc mvc) throws Exception {
        //创建虚拟请求
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(
                        "/api/product/change/sku/1765293654351069184/1765294804168503296/1765294805801168896/150000");
        //执行对应的请求
        ResultActions resultActions = mvc.perform(requestBuilder);
        log.debug("商品详情，sku切换返回结果：" + resultActions.andReturn().getResponse().getContentAsString());
    }

    /**
     * C端商品详情 聚合接口，需要启动相关RPC，验证相关数据
     */
    @Test
    void getProductDetailsTest(@Autowired MockMvc mvc) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Shop-Id", "1765293654351069184");

        //创建虚拟请求
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/api/product/details/1765294804168503296").headers(headers);
        //执行对应的请求
        ResultActions resultActions = mvc.perform(requestBuilder);
        log.debug("商品详情返回结果：" + resultActions.andReturn().getResponse().getContentAsString());
    }
}
