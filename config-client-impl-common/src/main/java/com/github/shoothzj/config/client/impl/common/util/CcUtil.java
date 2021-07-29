package com.github.shoothzj.config.client.impl.common.util;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.annotation.Anonymous;
import com.github.shoothzj.config.client.api.annotation.ConfClass;
import com.github.shoothzj.config.client.api.annotation.ConfField;
import com.github.shoothzj.config.client.api.annotation.Required;
import com.github.shoothzj.config.client.api.annotation.Secret;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.module.FieldType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shoothzj
 */
public class CcUtil {

    /**
     * 根据配置类名，转换为配置名称
     * @param configClass 配置Class
     * @param <T> 泛型对象
     * @return 配置名称
     */
    public static <T> String getConfigName(Class<T> configClass) {
        return configClass.getSimpleName();
    }

    public static <T extends BaseConfig> List<FieldDescribe> getConfigFieldDescribe(Class<T> clazz) {
        ConfClass classAnnotation = clazz.getAnnotation(ConfClass.class);
        if (classAnnotation == null) {
            throw new IllegalArgumentException("this class is not config class");
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        List<FieldDescribe> fieldDescribeList = new ArrayList<>();
        for (Field field : declaredFields) {
            if (!field.isAnnotationPresent(ConfField.class)) {
                continue;
            }
            final ConfField confField = field.getAnnotation(ConfField.class);
            FieldDescribe aux;
            if (confField == null) {
                aux = new FieldDescribe(field.getName());
            } else {
                aux = new FieldDescribe(field.getName(), confField.name());
            }
            if (field.isAnnotationPresent(Anonymous.class)) {
                aux.setAnonymous(true);
            }
            if (field.isAnnotationPresent(Required.class)) {
                aux.setRequired(true);
            }
            if (field.isAnnotationPresent(Secret.class)) {
                aux.setSecret(true);
            }
            if ("int".equals(field.getType().getSimpleName())) {
                aux.setFieldType(FieldType.INT);
            } else if ("String".equals(field.getType().getSimpleName())) {
                aux.setFieldType(FieldType.STRING);
            } else {
                throw new IllegalArgumentException("not supported field type " + field.getType().getSimpleName());
            }
            fieldDescribeList.add(aux);
        }
        return fieldDescribeList;
    }

}
