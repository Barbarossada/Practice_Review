package com.exam.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义 TypeHandler，用于处理 List<Long> 与 JSON 的转换
 * 解决 Jackson 默认将数字反序列化为 Integer 的问题
 * 
 * @author Exam System
 * @since 2026-01-03
 */
@MappedTypes(List.class)
public class LongListTypeHandler extends BaseTypeHandler<List<Long>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, OBJECT_MAPPER.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting List<Long> to JSON", e);
        }
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    /**
     * 解析 JSON 字符串为 List<Long>
     * 如果 Jackson 解析出 Integer，则自动转换为 Long
     */
    private List<Long> parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            // 先解析为 Number 列表（可能是 Integer 或 Long）
            List<Number> numbers = OBJECT_MAPPER.readValue(json, new TypeReference<List<Number>>() {});
            // 统一转换为 Long
            return numbers.stream()
                    .map(Number::longValue)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON to List<Long>: " + json, e);
        }
    }
}
