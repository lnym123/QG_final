package com.AutoValidateFrame.Validator;

/*
* 注解校验器泛型接口
* U 验证的数据类型（泛型）
* */

import java.lang.annotation.Annotation;

public interface ConstraintValidator<T extends Annotation,U> {
     void initialize(T constraintAnnotation);
     boolean isValid(U value);
      String getMessage();
}
