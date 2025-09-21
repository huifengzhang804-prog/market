# com.medusa.gruul.common.web.valid.ValidatorConfig

**文件路径**: `web\valid\ValidatorConfig.java`

## 代码文档
///
Description: 关闭快速返回

@author alan
Date: 2019/9/4 21:01
 
///

///
JSR和Hibernate validator的校验只能对Object的属性进行校验
不能对单个的参数进行校验
spring 在此基础上进行了扩展
添加了MethodValidationPostProcessor拦截器
可以实现对方法参数的校验
     
///


## 文件结构
```
项目根目录
└── web\valid
    └── ValidatorConfig.java
```

## 完整代码
```java
package com.medusa.gruul.common.web.valid;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Description: 关闭快速返回
 *
 * @author alan
 * Date: 2019/9/4 21:01
 */
@Configuration
public class ValidatorConfig {

    @Bean
    public Validator validator() {
        try (
            ValidatorFactory validatorFactory = Validation
                    .byProvider(HibernateValidator.class)
                    .configure()
                    //快速返回模式，有一个验证失败立即返回错误信息
                    .failFast(false)
                    .buildValidatorFactory()
        ){
            return validatorFactory.getValidator();
        }
    }

    /**
     * JSR和Hibernate validator的校验只能对Object的属性进行校验
     * 不能对单个的参数进行校验
     * spring 在此基础上进行了扩展
     * 添加了MethodValidationPostProcessor拦截器
     * 可以实现对方法参数的校验
     */
    @Bean
    public MethodValidationPostProcessor getMethodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }
}

```

# com.medusa.gruul.common.web.valid.annotation.File

**文件路径**: `valid\annotation\File.java`

## 代码文档
///
默认:<br/>
上传文件不能为空<br/>
允许上传所有后缀格式的文件<br/>
文件后缀名不区分大小写<br/>
文件最大不超过springmvc配置的文件大小<br/>
文件最小不小于0 kb <br/>
Created by bruce on 2018/8/30 16:34

@author sombody
 
///

///
文件最大size 单位默认MB

@return 最大size
	 
///

///
文件size单位
	 
///

///
文件格式限制

@return 允许上传的文件格式
	 
///

///
是否允许空文件

@return true 允许 false 不允许
	 
///

///
@return
	 
///


## 文件结构
```
项目根目录
└── valid\annotation
    └── File.java
```

## 完整代码
```java
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
```

# com.medusa.gruul.common.web.valid.annotation.Limit

**文件路径**: `valid\annotation\Limit.java`

## 代码文档
///
valid 支持枚举/范围类型的注解



@author 张治保
date 2021/3/23
 
///


## 文件结构
```
项目根目录
└── valid\annotation
    └── Limit.java
```

## 完整代码
```java
package com.medusa.gruul.common.web.valid.annotation;

import com.medusa.gruul.common.web.valid.validator.LimitValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * valid 支持枚举/范围类型的注解
 *
 *
 *
 * @author 张治保
 * date 2021/3/23
 */

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RUNTIME)
@Documented
@Repeatable(Limit.Limits.class)
@Constraint(validatedBy = {LimitValidator.class})
@NotNull
public @interface Limit {

    String message() default "com.medusa.gruul.common.web.valid.Limit.message";


    String[] value() default {};


    String min() default "0";

    String max() default "10";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default { };
    
    @Target({ElementType.FIELD,ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface Limits{
        Limit[] value();
    }
}

```

# com.medusa.gruul.common.web.valid.enums.FileFormat

**文件路径**: `valid\enums\FileFormat.java`

## 代码文档
///
文件格式类型枚举

@author 张治保
@since 2023/06/05
 
///

///
未知类型
	 
///

///
webp
	 
///

///
jpeg
	 
///

///
jpg
	 
///

///
PNG
	 
///

///
GIF
	 
///

///
tif
	 
///

///
Windows bitmap (bmp)
	 
///

///
16色位图(bmp)
	 
///

///
24位位图(bmp)
	 
///

///
256色位图(bmp)
	 
///

///
XML
	 
///

///
HTML
	 
///

///
XLS
	 
///

///
DOC
	 
///

///
DOCX
	 
///

///
XLSX
	 
///

///
PDF
	 
///

///
文件格式
	 
///

///
文件格式代码
	 
///


## 文件结构
```
项目根目录
└── valid\enums
    └── FileFormat.java
```

## 完整代码
```java
package com.medusa.gruul.common.web.valid.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 文件格式类型枚举
 *
 * @author 张治保
 * @since 2023/06/05
 */
@Getter
@RequiredArgsConstructor
public enum FileFormat {

	/**
	 * 未知类型
	 */
	ANY("ANY", ""),

	/**
	 * webp
	 */
	WEBP("WEBP", "52494646"),

	/**
	 * jpeg
	 */
	JPEG("JPEG", "FFD8FF"),

	/**
	 * jpg
	 */
	JPG("JPG", "FFD8FF"),

	/**
	 * PNG
	 */
	PNG("PNG", "89504E47"),

	/**
	 * GIF
	 */
	GIF("GIF", "47494638"),

	/**
	 * tif
	 */
	TIFF("TIF", "49492A00"),

	/**
	 * Windows bitmap (bmp)
	 */
	BMP("BMP", "424D"),

	/**
	 * 16色位图(bmp)
	 */
	BMP_16("BMP", "424D228C010000000000"),

	/**
	 * 24位位图(bmp)
	 */
	BMP_24("BMP", "424D8240090000000000"),

	/**
	 * 256色位图(bmp)
	 */
	BMP_256("BMP", "424D8E1B030000000000"),

	/**
	 * XML
	 */
	XML("XML", "3C3F786D6C"),

	/**
	 * HTML
	 */
	HTML("HTML", "68746D6C3E"),

	/**
	 * XLS
	 */
	XLS("XLS", "D0CF11E0"),

	/**
	 * DOC
	 */
	DOC("DOC", "D0CF11E0"),

	/**
	 * DOCX
	 */
	DOCX("DOCX", "504B0304"),

	/**
	 * XLSX
	 */
	XLSX("XLSX", "504B0304"),

	/**
	 * PDF
	 */
	PDF("PDF", "25504446");

	/**
	 * 文件格式
	 */
	private final String format;

	/**
	 * 文件格式代码
	 */
	private final String code;


}

```

# com.medusa.gruul.common.web.valid.enums.FileSizeUnit

**文件路径**: `valid\enums\FileSizeUnit.java`

## 代码文档
///
文件size单位

@author 张治保
date 2023/6/5
 
///

///
B
	 
///

///
KB
	 
///

///
MB
	 
///

///
GB
	 
///


## 文件结构
```
项目根目录
└── valid\enums
    └── FileSizeUnit.java
```

## 完整代码
```java
package com.medusa.gruul.common.web.valid.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 文件size单位
 *
 * @author 张治保
 * date 2023/6/5
 */
@Getter
@RequiredArgsConstructor

public enum FileSizeUnit {
	/**
	 * B
	 */
	B(1),

	/**
	 * KB
	 */
	KB(1024),

	/**
	 * MB
	 */
	MB(1024 * 1024),

	/**
	 * GB
	 */
	GB(1024 * 1024 * 1024);

	private final long size;
}

```

# com.medusa.gruul.common.web.valid.group.Create

**文件路径**: `valid\group\Create.java`

## 文件结构
```
项目根目录
└── valid\group
    └── Create.java
```

## 完整代码
```java
package com.medusa.gruul.common.web.valid.group;

import jakarta.validation.groups.Default;

public interface Create extends Default {
}

```

# com.medusa.gruul.common.web.valid.group.Update

**文件路径**: `valid\group\Update.java`

## 文件结构
```
项目根目录
└── valid\group
    └── Update.java
```

## 完整代码
```java
package com.medusa.gruul.common.web.valid.group;


import jakarta.validation.groups.Default;

public interface Update extends Default {
}

```

# com.medusa.gruul.common.web.valid.validator.FileValidator

**文件路径**: `valid\validator\FileValidator.java`

## 代码文档
///
校验器只有在真正使用的时候才会被实例化,不需要自己加实例化注解
Created by bruce on 2018/8/31 0:23

@author sombody
 
///

///
可迭代的文件 校验

@param objectFile 文件object
@param cvc        校验上下文
@return 是否校验通过
	 
///

///
单个文件校验

@param unknown 文件
@param cvc     校验上下文
@return 是否校验通过
	 
///

///
文件名
		 
///

///
文件头
		 
///

///
文件大小 单位字节
		 
///


## 文件结构
```
项目根目录
└── valid\validator
    └── FileValidator.java
```

## 完整代码
```java
package com.medusa.gruul.common.web.valid.validator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import com.medusa.gruul.common.web.valid.annotation.File;
import com.medusa.gruul.common.web.valid.enums.FileFormat;
import com.medusa.gruul.global.i18n.I18N;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 校验器只有在真正使用的时候才会被实例化,不需要自己加实例化注解
 * Created by bruce on 2018/8/31 0:23
 *
 * @author sombody
 */
public class FileValidator implements ConstraintValidator<File, Object> {

	private File valid;


	@Override
	public void initialize(File constraintAnnotation) {
		this.valid = constraintAnnotation;
	}

	private void validMessage(String message, ConstraintValidatorContext cvc) {
		cvc.disableDefaultConstraintViolation();
		cvc.buildConstraintViolationWithTemplate(message)
				.addConstraintViolation();
	}

	@Override
	public boolean isValid(Object objectFile, ConstraintValidatorContext cvc) {
		if (objectFile == null) {
			return true;
		}
		//可迭代的文件
		if (iterableFileValid(objectFile, cvc)) {
			return true;
		}
		//单个文件
		return validFile(objectFile, cvc);
	}

	/**
	 * 可迭代的文件 校验
	 *
	 * @param objectFile 文件object
	 * @param cvc        校验上下文
	 * @return 是否校验通过
	 */
	private boolean iterableFileValid(Object objectFile, ConstraintValidatorContext cvc) {
		Iterable<?> iterable = null;
		if (objectFile.getClass().isArray()) {
			iterable = Arrays.asList((Object[]) objectFile);
		}
		if (objectFile instanceof Iterable<?>) {
			iterable = (Iterable<?>) objectFile;
		}
		if (iterable != null) {
			for (Object o : iterable) {
				if (!validFile(o, cvc)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 单个文件校验
	 *
	 * @param unknown 文件
	 * @param cvc     校验上下文
	 * @return 是否校验通过
	 */
	private boolean validFile(Object unknown, ConstraintValidatorContext cvc) {
		validParam validParam = fileParam(unknown);
		if (validParam == null) {
			validMessage(I18N.msg("web.file.data.error"), cvc);
			return false;
		}
		//校验文件大小
		if (!valid.allowEmpty() && validParam.getFileSize() == 0) {
			validMessage(I18N.msg("web.file.empty"), cvc);
			return false;
		}
		if (validParam.getFileSize() > valid.maxSize() * valid.unit().getSize()) {
			validMessage(I18N.msg("web.file.too.large"), cvc);
			return false;
		}
		//校验文件格式
		FileFormat[] formats = valid.formats();
		if (ArrayUtil.isEmpty(formats)) {
			return true;
		}
		for (FileFormat format : formats) {
			if (
					!validParam.getFormatCode().startsWith(format.getCode()) ||
							!validParam.getFormat().equalsIgnoreCase(format.getFormat())
			) {
				validMessage(I18N.msg("web.file.format.error"), cvc);
				return false;
			}
		}
		return true;
	}


	@SneakyThrows
	private validParam fileParam(Object unknown) {
		if (unknown instanceof java.io.File file) {
			try (InputStream inputStream = new FileInputStream(file)) {
				validParam validParam = new validParam();
				validParam.setFormat(FileUtil.extName(file.getName()));
				validParam.setFileSize(file.length());
				validParam.setFormatCode(IoUtil.readHex64Upper(inputStream));
				return validParam;
			}
		}

		if (unknown instanceof MultipartFile file) {
			validParam validParam = new validParam();
			validParam.setFormat(FileUtil.extName(file.getOriginalFilename()));
			validParam.setFileSize(file.getSize());
			validParam.setFormatCode(IoUtil.readHex64Upper(file.getInputStream()));
			return validParam;
		}
		return null;
	}


	@Getter
	@Setter
	@Accessors(chain = true)
	public static class validParam {
		/**
		 * 文件名
		 */
		private String format;

		/**
		 * 文件头
		 */
		private String formatCode;

		/**
		 * 文件大小 单位字节
		 */
		private Long fileSize;


	}
}
```

# com.medusa.gruul.common.web.valid.validator.LimitValidator

**文件路径**: `valid\validator\LimitValidator.java`

## 代码文档
///
@author 张治保
date 2021/3/23
 
///


## 文件结构
```
项目根目录
└── valid\validator
    └── LimitValidator.java
```

## 完整代码
```java
package com.medusa.gruul.common.web.valid.validator;


import cn.hutool.core.convert.Convert;
import com.medusa.gruul.common.web.valid.annotation.Limit;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * @author 张治保
 * date 2021/3/23
 */
@Slf4j
public class LimitValidator implements ConstraintValidator<Limit, Object> {


	private Limit limit;

	@Override
	public void initialize(Limit limit) {
		this.limit = limit;
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		String[] vals = limit.value();
		/*
		 * 枚举的处理
		 */
		if (vals.length > 0) {
			return enumValid(value, vals);
		}
		/*
		 * 数字范围处理
		 */
		return inRange(value, limit.min(), limit.max());
	}

	public boolean inRange(Object value, String min, String max) {
		BigDecimal val = toBigDecimal(value);
		BigDecimal minv = toBigDecimal(min);
		BigDecimal maxv = toBigDecimal(max);
		int compare = minv.compareTo(maxv);
		if (compare > 0) {
			if (log.isErrorEnabled()) {
				log.error("@Limit min max set error");
			}
			return false;
		} else if (compare == 0 && val.equals(minv)) {
			return true;
		}

		return val.compareTo(minv) >= 0 && val.compareTo(maxv) <= 0;
	}

	public boolean enumValid(Object value, String[] vals) {
		Class<?> valueClazz = value.getClass();
		for (String val : vals) {
			Object convert = convert(valueClazz, val);
			if (convert == null) {
				continue;
			}
			if (convert == value || convert.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public BigDecimal toBigDecimal(Object value) {
		BigDecimal result;
		try {
			result = Convert.toBigDecimal(value);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}


	public Object convert(Class<?> valueClazz, Object val) {
		Object result;
		try {
			result = Convert.convert(valueClazz, val);
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("@Limit Usage error", ex);
			}
			throw new RuntimeException(ex);
		}
		return result;
	}
}

```

