package com.vsa.ecommerce.common.repository;

import com.vsa.ecommerce.common.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility to map Native Query results (which return Map or Object[]) to DTOs.
 * This is useful when you need high-performance queries that bypass Hibernate Entity managment.
 */
@Component
public class NativeQueryMapper {

    public <T> List<T> mapList(List<Map<String, Object>> resultList, Class<T> targetClass) {
        List<T> list = new ArrayList<>();
        for (Map<String, Object> row : resultList) {
            list.add(map(row, targetClass));
        }
        return list;
    }

    public <T> T map(Map<String, Object> row, Class<T> targetClass) {
        // Leveraging Jackson to convert Map -> JSON -> Object
        // This is not the fastest way (Reflection is faster) but it is the most robust 
        // as it handles Type conversion (snake_case to camelCase if configured) automatically.
        return JsonUtil.getMapper().convertValue(row, targetClass);
    }
}
