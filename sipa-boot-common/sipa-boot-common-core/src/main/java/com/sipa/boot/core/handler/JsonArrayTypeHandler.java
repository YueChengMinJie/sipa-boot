package com.sipa.boot.core.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sipa.boot.core.util.SipaJsonUtil;

/**
 * @author caszhou
 * @date 2024/6/24
 */
public class JsonArrayTypeHandler<T> implements TypeHandler<List<T>> {
    @Override
    public void setParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
        } else {
            ps.setString(i, SipaJsonUtil.writeValueAsString(parameter));
        }
    }

    @Override
    public List<T> getResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = rs.getString(columnName);
        if (jsonString == null) {
            return null;
        }
        return SipaJsonUtil.convertValue(jsonString, new TypeReference<>() {});
    }

    @Override
    public List<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getResult(rs, rs.getString(columnIndex));
    }

    @Override
    public List<T> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        if (jsonString == null) {
            return null;
        }
        return SipaJsonUtil.convertValue(jsonString, new TypeReference<>() {});
    }
}
