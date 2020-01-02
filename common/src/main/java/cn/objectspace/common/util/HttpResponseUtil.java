package cn.objectspace.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpResponseUtil {
    public static Boolean addCookie(HttpServletResponse response, String cookieName, String cookieValue){
        try{
            Cookie cookie = new Cookie(cookieName,cookieValue);
            cookie.setPath("/");
            cookie.setMaxAge(-1);
            response.addCookie(cookie);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public static Boolean deleteCookie(HttpServletResponse response,String cookieName){
        try{
            Cookie cookie = new Cookie(cookieName,null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
