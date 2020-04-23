package cn.objectspace.common.util;

/**
* @Description: 分页工具
* @Author: NoCortY
* @Date: 2020/2/14
*/
public class PageUtil {
    /**
     * @Description: 计算行号
     * @Param: [pageNum, pageSize]
     * @return: java.lang.Integer
     * @Author: NoCortY
     * @Date: 2020/2/14
     */
    public static Integer getRowIndex(Integer pageNum, Integer pageSize) {

        if (pageNum == null || pageSize == null) return null;

        return (pageNum> 0) ? (pageNum- 1) * pageSize : 0;
    }
}
