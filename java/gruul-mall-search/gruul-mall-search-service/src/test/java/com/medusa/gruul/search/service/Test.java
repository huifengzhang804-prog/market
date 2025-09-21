package com.medusa.gruul.search.service;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

/**
 * @author 张治保
 * @since 2024/5/16
 */
public class Test {


    @org.junit.Test
    public void testDefaultSize() {
        TermsAggregationBuilder field = AggregationBuilders.terms("shopIdField").field("shopIdField");
        System.out.println(field.size());
    }
}
