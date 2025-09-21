package com.medusa.gruul.common.spring.listener.functions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;

/**
 * @author 张治保
 * date 2022/2/15
 */
@Getter
@Setter
public class GruulSpringListener implements SpringApplicationRunListener {
    private SpringApplication application;
    private String[] args;
}
