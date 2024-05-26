package com.AutoValidateFrame;

import com.AutoValidateFrame.Inno.ContainOnly;
import com.AutoValidateFrame.Validator.*;

/*功能：
 *1.自动检查类对象中所有字段上的含有校验注解类型
 * */
public class ValidatorFactory {
    public static void Validator(Object object) {
        ValidProcessor validProcessor = new ValidProcessor();
        validProcessor.addValidator(ContainOnly.class, new ContainOnlyValidator());
        validProcessor.process(object);
    }

}
