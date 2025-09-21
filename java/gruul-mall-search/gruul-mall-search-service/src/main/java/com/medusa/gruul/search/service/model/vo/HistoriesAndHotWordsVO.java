package com.medusa.gruul.search.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2022/12/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class HistoriesAndHotWordsVO implements Serializable {

    /**
     * 搜索历史
     */
    private List<String> histories;

    /**
     * 搜索热词
     */
    private List<String> hotWords;

}
