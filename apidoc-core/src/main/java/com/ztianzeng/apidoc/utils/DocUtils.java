package com.ztianzeng.apidoc.utils;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaField;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 基本工具类
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-05 15:13
 */
public final class DocUtils {
    private DocUtils() {
    }

    /**
     * 是否为私有属性
     *
     * @param type 类型
     * @return true false
     */
    public static boolean isPrimitive(String type) {
        type = type.contains("java.lang") ? type.substring(type.lastIndexOf(".") + 1) : type;
        type = type.toLowerCase();
        switch (type) {
            case "integer":
            case "int":
            case "long":
            case "double":
            case "float":
            case "short":
            case "bigdecimal":
            case "char":
            case "string":
            case "boolean":
            case "byte":
            case "java.sql.timestamp":
            case "java.util.date":
            case "java.time.localdatetime":
            case "localdatetime":
            case "localdate":
            case "java.time.localdate":
            case "java.math.bigdecimal":
            case "java.math.biginteger":
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为Spring mvc的内置对象
     *
     * @param paramType 类型
     * @return true false
     */
    public static boolean isMvcParams(String paramType) {
        switch (paramType) {
            case "org.springframework.ui.Model":
            case "org.springframework.ui.ModelMap":
            case "org.springframework.web.servlet.ModelAndView":
            case "org.springframework.validation.BindingResult":
            case "javax.servlet.http.HttpServletRequest":
            case "javax.servlet.http.HttpServletResponse":
                return true;
            default:
                return false;
        }
    }

    /**
     * 判断属性是否是必须
     * <p>
     * 是否有javax.validation中的注解
     *
     * @param field 属性
     */
    public static boolean isRequired(JavaField field) {
        boolean isRequired = false;
        List<JavaAnnotation> annotations = field.getAnnotations();
        for (JavaAnnotation annotation : annotations) {
            String fullyQualifiedName = annotation.getType().getFullyQualifiedName();
            if (fullyQualifiedName.startsWith("javax.validation")) {
                isRequired = true;
            }
        }
        return isRequired;
    }


    /**
     * 用过名字获取所在的类型
     *
     * @param typeName 类型名称
     * @return javaType
     */
    public static Type getTypeForName(String typeName) {
        try {
            return TypeFactory.defaultInstance().findClass(typeName);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(typeName);
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }


    }


}