package com.medusa.gruul.overview.api.model;

import lombok.Data;

/**
 * @author jipeng
 * @since 2024/11/15
 */
@Data
public class DownloadFileSseMessage {
    private Long fileId;
    private Boolean success;
    private String content;
}
