package com.medusa.gruul.addon.team.model.count;

import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2023/3/29
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = "userNum")
public class TeamProductUserCount extends ActivityShopProductKey {

    /**
     * 参团人数
     */
    private Integer userNum;


}
