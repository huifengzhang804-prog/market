package com.medusa.gruul.search.service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * @since 2023/9/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MaterialDTO implements Serializable {

    /**
     * 素材分类id
     */
    private String categoryId;

    /**
     * 素材 url
     */
    @NotNull
    @Size(min = 1, max = 10)
    private List<MultipartFile> files;

}
