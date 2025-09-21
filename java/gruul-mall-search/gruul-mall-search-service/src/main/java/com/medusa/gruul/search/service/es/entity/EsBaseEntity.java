package com.medusa.gruul.search.service.es.entity;

import com.medusa.gruul.common.fastjson2.FastJson2;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;
import org.elasticsearch.common.xcontent.XContentElasticsearchExtension;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/12/15
 * @see XContentElasticsearchExtension
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class EsBaseEntity implements Serializable {

    /**
     * es中的唯一id
     */
    @IndexId(type = IdType.NONE)
    private String id;

    /**
     * 时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = FastJson2.DATETIME_PATTEN)
    private LocalDateTime createTime;
}
