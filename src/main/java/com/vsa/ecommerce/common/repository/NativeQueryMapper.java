package com.vsa.ecommerce.common.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to map Native Query results to DTOs.
 * Supports mapping from Map<String, Object> and Jakarta Persistence Tuple.
 * 
 * Features:
 * - Snake_case (DB) -> CamelCase (DTO) automatic mapping
 * - Ignores unknown properties in DTO
 * - Handles Java 8 Dates (via JavaTimeModule)
 */
@Component
public class NativeQueryMapper {

    private final ObjectMapper objectMapper;

    public NativeQueryMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Map a list of Maps to a list of DTOs.
     */
    public <T> List<T> mapList(List<Map<String, Object>> resultList, Class<T> targetClass) {
        List<T> list = new ArrayList<>();
        if (resultList == null)
            return list;

        for (Map<String, Object> row : resultList) {
            list.add(map(row, targetClass));
        }
        return list;
    }

    /**
     * Map a single Map to a DTO.
     */
    public <T> T map(Map<String, Object> row, Class<T> targetClass) {
        // Leveraging Jackson to convert Map -> JSON -> Object
        return objectMapper.convertValue(row, targetClass);
    }

    /**
     * Map a list of Tuples to a list of DTOs.
     */
    public <T> List<T> mapTupleList(List<Tuple> tupleList, Class<T> targetClass) {
        List<T> list = new ArrayList<>();
        if (tupleList == null)
            return list;

        for (Tuple tuple : tupleList) {
            list.add(map(tuple, targetClass));
        }
        return list;
    }

    /**
     * Map a single Tuple to a DTO.
     */
    public <T> T map(Tuple tuple, Class<T> targetClass) {
        Map<String, Object> map = new HashMap<>();
        for (TupleElement<?> element : tuple.getElements()) {
            String alias = element.getAlias();
            if (alias != null) {
                map.put(alias, tuple.get(alias));
            } else {
                // Fallback or warning? For now, we need aliases to map to properties
            }
        }
        return map(map, targetClass);
    }
}
