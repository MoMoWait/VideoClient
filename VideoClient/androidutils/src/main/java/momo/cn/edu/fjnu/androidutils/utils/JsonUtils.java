package momo.cn.edu.fjnu.androidutils.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Json工具
 * Created by GaoFei on 2016/3/17.
 */
public class JsonUtils {
    public static JSONArray listToJsonArray(List<?> objects){
        JSONArray arrays = new JSONArray();
        for(Object object : objects){
            JSONObject jsonObject = new JSONObject();
            Class<?> objectClass = object.getClass();
            Field[] fields = objectClass.getDeclaredFields();
            for(Field field:fields){
                try{
                    field.setAccessible(true);
                    jsonObject.put(field.getName(), field.get(object));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            arrays.put(jsonObject);
        }
        return arrays;
    }
}
