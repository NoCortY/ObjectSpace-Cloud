package cn.objectspace.authcenter.util;

import com.google.code.kaptcha.Constants;

import javax.servlet.http.HttpSession;

public class CaptchaUtil {
    public static boolean veriry(HttpSession session,String captcha){
        if(session.getAttribute(Constants.KAPTCHA_SESSION_KEY)==null) return false;
        if(session.getAttribute(Constants.KAPTCHA_SESSION_KEY).equals(captcha)) return true;
        else return false;
    }
}
