package io.github.pactstart.biz.common.utils;

import org.dozer.Mapper;
import org.dozer.MappingException;

import java.util.ArrayList;
import java.util.List;

public class MapperUtils {

    /**
     * 单个 bean 转换
     *
     * @param source
     * @param destinationClass
     * @return
     * @throws MappingException
     */
    static public <T> T map(Object source, Class<T> destinationClass) throws MappingException {
        if (source == null) {
            return null;
        }
        return SpringContextHolder.getBean(Mapper.class).map(source, destinationClass);
    }

    /**
     * 转换 List
     *
     * @param source
     * @param destinationClass
     * @return
     * @throws MappingException
     */
    static public <T> List<T> mapList(List<?> source, Class<T> destinationClass) throws MappingException {
        if (source == null) {
            return null;
        }
        List<T> result = new ArrayList<T>();
        for (Object object : source) {
            result.add(map(object, destinationClass));
        }
        return result;
    }
}
