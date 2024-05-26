package com.AutoValidateFrame.Validator;

public class ValidationException extends RuntimeException{
    //验证不通过的时候，发送错误信息
public ValidationException(String message){
    super(message.toString());
}
}
