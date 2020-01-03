package cn.objectspace.authcenter.util;

import java.util.UUID;

/**
 * @Description: UUID工具
 * @Author: NoCortY
 * @Date: 2019/10/4
 */
public class UUIDUtil {

    /**
     * @Description: 获取UUID
     * @Param: []
     * @return: java.lang.String
     * @Author: NoCortY
     * @Date: 2019/10/4
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        String result = uuid.replace("-", "");
        return result;
    }
}
