package com.goal.payment.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    /**
     * 字符串Json格式转换为对象Map
     *
     * @param strJson {"username":"lp"}
     * @return 根据json转换为Map对象
     */
    public static Map<String, Object> jsonToMap(String strJson) {
        Map<String, Object> jsoMap = new HashMap<String, Object>();
        try {
            jsoMap = JSONObject.parseObject(strJson, Map.class);
        } catch (JSONException e) {
            System.out.println("json转换Map出错：" + e.getMessage());
        }
        return jsoMap;
    }

    /**
     * 字符串Json格式转换为对象Map
     *
     * @param strJson {"username":"lp"}
     * @return 根据json转换为Map对象
     */
    public static Map<String, String> jsonToMapString(String strJson) {
        Map<String, String> jsoMap = new HashMap<String, String>();
        try {
            jsoMap = JSONObject.parseObject(strJson, Map.class);
        } catch (JSONException e) {
            System.out.println("json转换Map出错：" + e.getMessage());
        }
        return jsoMap;
    }


    /**
     * 字符串Json 转换为对象List
     *
     * @param strJson [{"username":"lp"}]
     * @return 根据json转换List
     */
    public static List<Map<String, Object>> jsonToList(String strJson) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = JSONObject.parseObject(strJson, List.class);
        } catch (JSONException e) {
            System.out.println("json转换List出错：" + e.getMessage());
        }
        return list;
    }
}