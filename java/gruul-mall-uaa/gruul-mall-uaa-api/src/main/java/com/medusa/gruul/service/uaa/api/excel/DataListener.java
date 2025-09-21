package com.medusa.gruul.service.uaa.api.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;



import com.medusa.gruul.common.model.resp.SystemCode;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataListener<T> extends AnalysisEventListener<T> {
    private final List<T> dataList = new ArrayList<>();

    public DataListener() {
    }

    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        if (!dataList.contains(data)) {
            dataList.add(data);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        for (T data : dataList) {
            Set<ConstraintViolation<T>> violations = validator.validate(data);
            if (!violations.isEmpty()) {
                throw SystemCode.PARAM_VALID_ERROR.msgEx(violations.iterator().next().getMessage());

            }
        }
    }

    public List<T> getDataList() {
        return dataList;
    }
}
