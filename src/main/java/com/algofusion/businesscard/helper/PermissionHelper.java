package com.algofusion.businesscard.helper;

import com.algofusion.businesscard.errors.CustomException;

public class PermissionHelper {
    public static void checkUsername(String us1, String us2) {
        if (!us1.equals(us2)) {
            throw new CustomException("You do not have authorization.");
        }
    }
}
