package com.medusa.gruul.common.addon.supporter;

import com.medusa.gruul.common.addon.supporter.sacnner.AddonSupporterRegistrar;
import org.springframework.context.annotation.Import;

/**
 * @author 张治保
 * date 2022/2/18
 */

@Import({
        AddonSupporterRegistrar.class
})
public class AddonSupporterAutoconfigure {
}
