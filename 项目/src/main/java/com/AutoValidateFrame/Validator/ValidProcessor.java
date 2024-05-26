package com.AutoValidateFrame.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/*
* 注解解析器：
* 1.用HashMap储存Class和ConstrainValidator对象
* 2.根据注解类型动态创建相应校验器
* 3.解析相应注解校验并返回校验错误信息:不指定字段校验和指定字段校验
*
* */

public class    ValidProcessor {
    private final Map<Class<?>, ConstraintValidator<?, ?>> validators = new HashMap<>();   //储存注解类型以及相应的验证器

    //添加注解类对象以及及其验证器
    public void addValidator(Class<?> annotationType, ConstraintValidator<?, ?> validator){
        validators.put(annotationType,validator);
    }
    public void process(Object object)  {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

        //Key:注解 Value：相应注解校验器
        for(Map.Entry<Class<?>, ConstraintValidator<?, ?>> entry:validators.entrySet()){
            Class<Annotation> annotationclass=(Class<Annotation>)entry.getKey();

            //判断field上的注解（一个字段上不止一种注解）是否包含相应注解key的注解
            if(field.isAnnotationPresent(annotationclass)){
                Annotation annotation=field.getAnnotation(annotationclass);
                //创建具体的ConstraintValidator对象：第二个校验参数为Object类型
                ConstraintValidator<Annotation,Object> validator = (ConstraintValidator<Annotation, Object>) entry.getValue();

                try {
                    Object value=field.get(object);
                    validator.initialize(annotation);//初始化自定义的Validator
                    if(!validator.isValid(value)) {
                        throw new ValidationException(validator.getMessage());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        }
    }



    }




