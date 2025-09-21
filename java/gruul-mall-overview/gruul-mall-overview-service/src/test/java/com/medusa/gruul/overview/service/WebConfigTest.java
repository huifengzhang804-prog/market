package com.medusa.gruul.overview.service;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.C;

/**
 * @author jipeng
 * @since 2024/6/20
 */
public class WebConfigTest {

  public static void main(String[] args) throws ClassNotFoundException {


    WebConfigTest.Attribute<Object> attribute= new  WebConfigTest.Attribute<>("site_name", "MyWebsite", "string");
    WebConfigTest.Attribute<Object> attribute1= new  WebConfigTest.Attribute<>("double", 3.14, "double");
    WebConfigTest.Attribute<Object> attribute2= new  WebConfigTest.Attribute<>("int", 3, "int");
    WebConfigTest.Attribute<Object> attribute3= new  WebConfigTest.Attribute<>("boolean", true, "boolean");
    List<WebConfigTest.Attribute<Object>> list= Lists.newArrayList();
    list.add(attribute);
    list.add(attribute1);
    list.add(attribute2);
    list.add(attribute3);
    Map<String,Object> map= Maps.newHashMap();
    for (Attribute<Object> objectAttribute : list) {
      map.put(objectAttribute.getKey(),parseValue(objectAttribute.getValue().toString(), objectAttribute.getType()));
    }

    String name = Integer.class.getName();
    System.out.println("name = " + name);
    String jsonString = JSON.toJSONString(map);
    System.out.println("jsonString = " + jsonString);
  }
  private static <T> T parseValue(String value, String type) {
    switch (type.toLowerCase()) {
      case "int":
        return (T) Integer.valueOf(value);
      case "float":
        return (T) Float.valueOf(value);
      case "double":
        return (T) Double.valueOf(value);
      case "boolean":
        return (T) Boolean.valueOf(value);
      case "long":
        return (T) Long.valueOf(value);
      case "string":
      default:
        return (T) value;
    }
  }

   static class Attribute<T> {
    private String key;
    private T value;
    private String type;

    public Attribute() {}

    public Attribute(String key, T value, String type) {
      this.key = key;
      this.value = value;
      this.type = type;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public T getValue() {
      return value;
    }

    public void setValue(T value) {
      this.value = value;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
