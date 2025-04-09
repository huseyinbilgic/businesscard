package com.algofusion.businesscard.helper;

import org.springframework.stereotype.Component;

import com.algofusion.businesscard.errors.CustomException;

@Component
public class PermissionHelper {
    public void checkUsername(String us1, String us2){
        if (!us1.equals(us2)) {
            throw new CustomException("You do not have authorization.");
        }
    }
}
