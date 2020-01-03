package cn.objectspace.common.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* @Description: HTTP请求获取工具类
* @Author: NoCortY
* @Date: 2019年11月26日
*/
public class HttpRequestUtil {
	/**
	 * @Description: 获取当前上下文的HTTP请求，可用于获取session等
	 * @Param: args
	 * @return: HttpServletRequest
	 * @Author: NoCortY
	 * @Date: 2019年12月6日
	 */
	public static HttpServletRequest getRequest() {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes)ra;
        HttpServletRequest request = sra.getRequest();
        return request;
	}
	public static String getCookieValue(HttpServletRequest request,String key){
		if(key==null||"".equals(key)) return null;
		Cookie[] cookies = request.getCookies();
		if(cookies!=null&&cookies.length>0){
			for(Cookie cookie : cookies){
				if(key.equals(cookie.getName())){
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	/**
	 * @Description: 获取String类型的参数
	 * @Param: args
	 * @return: String
	 * @Author: NoCortY
	 * @Date: 2019年11月26日
	 */
	public static String getStringParameter(HttpServletRequest request,String parameterName) {
		if(request.getParameter(parameterName)!=null) return request.getParameter(parameterName);
		else return null;
	}
	/**
	 * @Description: 获取Integer类型的参数
	 * @Param: args
	 * @return: Integer
	 * @Author: NoCortY
	 * @Date: 2019年11月26日
	 */
	public static Integer getIntegerParameter(HttpServletRequest request,String parameterName) {
		if(request.getParameter(parameterName)!=null&&request.getParameter(parameterName)!="") return Integer.valueOf(request.getParameter(parameterName));
		else return null;
	}
	/**
	 * @Description: 获取Date类型的参数
	 * @Param: args
	 * @return: Date
	 * @Author: NoCortY
	 * @Date: 2019年11月26日
	 */
	public static Date getDateParameter(HttpServletRequest request,String parameterName) throws ParseException {
		if(request.getParameter(parameterName)!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(request.getParameter(parameterName));
		}else {
			return null;
		}
	}
}
