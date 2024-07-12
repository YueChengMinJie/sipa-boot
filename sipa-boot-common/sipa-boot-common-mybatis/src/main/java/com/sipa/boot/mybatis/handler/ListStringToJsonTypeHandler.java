package com.sipa.boot.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.alibaba.fastjson2.JSONArray;

/**
 * @author zhipeng.guo
 */
public class ListStringToJsonTypeHandler implements TypeHandler<List<String>> {
    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
        throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
        } else {
            ps.setString(i, new JSONArray(parameter).toString());
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = rs.getString(columnName);
        if (jsonString == null) {
            return null;
        }
        return JSONArray.parseArray(jsonString).toList(String.class);
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getResult(rs, rs.getString(columnIndex));
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        if (jsonString == null) {
            return null;
        }
        return JSONArray.parseArray(jsonString).toList(String.class);
    }
}
