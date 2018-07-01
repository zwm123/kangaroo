package io.github.pactstart.biz.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.pactstart.biz.common.errorcode.ResponseCode;
import io.github.pactstart.biz.common.exception.ApplicationException;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BeanMapUtils {

    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将对象装换为map,并制定忽略字段名
     *
     * @param bean
     * @param ignoreFields
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> beanToMapIgnore(T bean, List<String> ignoreFields) {
        if (CollectionUtils.isEmpty(ignoreFields)) {
            beanToMap(bean);
        }
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                String keyStr = key + "";
                boolean ignore = false;
                for (String ignoreField : ignoreFields) {
                    if (ignoreField.equals(keyStr)) {
                        ignore = true;
                        break;
                    }
                }
                if (!ignore) {
                    map.put(keyStr, beanMap.get(key));
                }
            }
        }
        return map;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 将List<T>转换为List<Map<String, Object>>
     *
     * @param objList
     * @return
     */
    public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (objList != null && objList.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0, size = objList.size(); i < size; i++) {
                bean = objList.get(i);
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 将List<Map<String,Object>>转换为List<T>
     *
     * @param maps
     * @param clazz
     * @return
     */
    public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps, Class<T> clazz) {
        List<T> list = Lists.newArrayList();
        try {
            if (maps != null && maps.size() > 0) {
                Map<String, Object> map = null;
                T bean = null;
                for (int i = 0, size = maps.size(); i < size; i++) {
                    map = maps.get(i);
                    bean = clazz.newInstance();
                    mapToBean(map, bean);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            throw new ApplicationException(ResponseCode.SYSTEM_ERROR);
        }
        return list;
    }
}
