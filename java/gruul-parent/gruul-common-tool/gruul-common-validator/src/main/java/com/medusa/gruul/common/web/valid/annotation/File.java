package com.medusa.gruul.common.web.valid.annotation;

import com.medusa.gruul.common.web.valid.enums.FileFormat;
import com.medusa.gruul.common.web.valid.enums.FileSizeUnit;
import com.medusa.gruul.common.web.valid.validator.FileValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 默认:<br/>
 * 上传文件不能为空<br/>
 * 允许上传所有后缀格式的文件<br/>
 * 文件后缀名不区分大小写<br/>
 * 文件最大不超过springmvc配置的文件大小<br/>
 * 文件最小不小于0 kb <br/>
 * Created by bruce on 2018/8/30 16:34
 *
 * @author sombody
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class)
public @interface File {


	/**
	 * 文件最大size 单位默认MB
	 *
	 * @return 最大size
	 */
	long maxSize() default Long.MAX_VALUE;

	/**
	 * 文件size单位
	 */
	FileSizeUnit unit() default FileSizeUnit.MB;

	/**
	 * 文件格式限制
	 *
	 * @return 允许上传的文件格式
	 */
	FileFormat[] formats() default {};

	/**
	 * 是否允许空文件
	 *
	 * @return true 允许 false 不允许
	 */
	boolean allowEmpty() default false;

	/**
	 * @return
	 */
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String message() default "The uploaded file is not verified.";


	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@Retention(RUNTIME)
	@Documented
	@interface Files {
		File[] value();
	}
}