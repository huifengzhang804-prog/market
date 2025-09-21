package com.medusa.gruul.order.service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mysql.cj.jdbc.Driver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 张治保
 * @since 2024/11/25
 */
@Slf4j
public class AreaCodeToAreaTest {

    private static final Map<String, String> provinceMap = new HashMap<>();
    private static final Map<String, String> cityMap = new HashMap<>();
    private static final Map<String, String> areaMap = new HashMap<>();

    static {
        // 加载驱动程序
        log.info("驱动程序加载成功，{}", Driver.class.getName());
        //读取json 文件 渲染为 省市区map
        JSONArray array = JSONUtil.parseArray(ResourceUtil.readStr("./json/provinces.json", StandardCharsets.UTF_8));
        for (Object o : array) {
            JSONObject object = (JSONObject) o;
            provinceMap.put(object.getStr("code"), object.getStr("name"));
        }

        JSONArray cities = JSONUtil.parseArray(ResourceUtil.readStr("./json/citys.json", StandardCharsets.UTF_8));
        //市
        for (Object o : cities) {
            JSONArray arr = (JSONArray) o;
            for (Object object : arr) {
                JSONObject city = (JSONObject) object;
                cityMap.put(city.getStr("code"), city.getStr("name"));
                JSONArray areas = city.getJSONArray("children");
                //区
                for (Object area : areas) {
                    JSONObject areaObject = (JSONObject) area;
                    areaMap.put(areaObject.getStr("code"), areaObject.getStr("name"));
                }
            }
        }
    }

    static Connection connection(String url, String username, String password) {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            log.error("获取连接时发生异常", e);
            return null;
        }
    }

    private static void update(String tableName, String jdbcUrl, String username, String password) throws SQLException {
        //建立mhsql数据库连接
        try (Connection connection = connection(jdbcUrl, username, password)) {
            assert connection != null;
            try (
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT id,area_code FROM " + tableName);
                    PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + tableName + " SET area_code =? WHERE id =?")
            ) {
                boolean existsData;
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    existsData = resultSet.next();
                    while (resultSet.next()) {
                        String string = resultSet.getString("area_code");
                        if (string == null || string.isEmpty()) {
                            continue;
                        }
                        JSONArray areaCodes = JSONUtil.parseArray(string);
                        JSONArray area = toArea(areaCodes);
                        updateStatement.setString(1, area.toString());
                        updateStatement.setLong(2, resultSet.getLong("id"));

                        updateStatement.addBatch();
                    }
                }
                // 执行批量更新
                if (existsData) {
                    updateStatement.executeBatch();
                }
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE " + tableName + " CHANGE COLUMN area_code area JSON COMMENT '省市区' NOT NULL");
            }
        }
    }

    private static JSONArray toArea(JSONArray areaCode) {
        JSONArray result = new JSONArray();
        if (areaCode.isEmpty()) {
            return result;
        }
        String province = provinceMap.get(areaCode.get(0, String.class));
        result.add(province);
        String city = cityMap.get(areaCode.get(1, String.class));
        if (!city.equals("市辖区")) {
            result.add(city);
        }
        String area = areaMap.get(areaCode.get(2, String.class));
        result.add(area);
        return result;
    }

    /**
     * url:
     * username: root
     * password: public2022
     */
    @Test
    public void test() throws SQLException {
        update(
                "t_order_receiver",
                "jdbc:mysql://192.168.1.129:3306/test?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true",
                "root",
                "public2022"
        );
    }
}
