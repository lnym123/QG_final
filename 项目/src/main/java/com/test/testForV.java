package com.test;

import com.AutoValidateFrame.Validator.ValidationException;
import com.AutoValidateFrame.ValidatorFactory;
import com.pojo.Group;
import com.pojo.User;
import org.junit.Test;

public class testForV {

    @Test
    public void test6()  {
        User user = new User("123me","家","13");
        Group group = new Group("123","你好","你好");
        System.out.println(user);
        try {

            ValidatorFactory.Validator(group);

        } catch (ValidationException e) {
            System.out.println(e.getMessage());

        }



    }
}
