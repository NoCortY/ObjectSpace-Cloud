package cn.objectspace.common.util;

/**
 * @Description: 时间工具
 * @Author: NoCortY
 * @Date: 2020/3/4
 */
public class TimeUtil {
    /**
     * @Description: 分钟转毫秒
     * @Param: [minute]
     * @return: java.lang.Long
     * @Author: NoCortY
     * @Date: 2020/3/4
     */
    public static Long minuteToMillisecond(Long minute) {
        return minute * 60 * 1000;
    }
}
