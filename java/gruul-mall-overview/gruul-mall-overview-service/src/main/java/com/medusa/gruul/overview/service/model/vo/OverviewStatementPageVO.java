package com.medusa.gruul.overview.service.model.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/11/24
 */

@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
public class OverviewStatementPageVO extends Page<OverviewStatement> {

    /**
     * 数据统计
     */
    private StatementStatisticsVO statistics;


    public static OverviewStatementPageVO toOverviewStatementPage(IPage<OverviewStatement> statementPage) {
        OverviewStatementPageVO page = new OverviewStatementPageVO();
        page.setPages(statementPage.getPages())
                .setCurrent(statementPage.getCurrent())
                .setSize(statementPage.getSize())
                .setTotal(statementPage.getTotal())
                .setRecords(statementPage.getRecords());
        return page;
    }
}
