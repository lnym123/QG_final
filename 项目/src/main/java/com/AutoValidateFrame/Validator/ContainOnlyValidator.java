package com.AutoValidateFrame.Validator;


import com.AutoValidateFrame.Inno.ContainOnly;
//通过 传入正则表达式给allowedCharacters，进行验证。
public class ContainOnlyValidator implements ConstraintValidator<ContainOnly,String>{
    private String allowedCharacters;       //正则表达式字符集
    private String message; //验证失败后返回的消息


    @Override
    public void initialize(ContainOnly constraintAnnotation) {
        this.allowedCharacters=constraintAnnotation.allowed();
        this.message=constraintAnnotation.message();
        //从注解中读取正则表达式，以及失败消息
    }

    @Override
    public boolean isValid(String value) {
        if(value==null)
        {return true;}

        return value.matches(allowedCharacters);

    }

    @Override
    public String getMessage() {
        return message;
    }
}
