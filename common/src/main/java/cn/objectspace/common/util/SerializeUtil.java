package cn.objectspace.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
* @Description: 序列化工具类,必须实现了Serializable接口的对象才可以进行序列化
* @Author: NoCortY
* @Date: 2019/10/4
*/
public class SerializeUtil {
    private static Logger logger = LoggerFactory.getLogger(SerializeUtil.class);
    /**
     * @Description:  序列化对象
     * @Param: [obj]
     * @return: byte[]
     * @Author: NoCortY
     * @Date: 2019/10/4
     */
    public static byte[] serialize(Object obj){
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try{
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            oos.writeObject(obj);
            byte[] byteArray = baos.toByteArray();
            return byteArray;

        }catch(IOException e){
            logger.error("序列化异常");
            logger.error("异常信息:{}",e.getMessage());
        }
        return null;
    }


    /**
     * @Description:  反序列化对象
     * @Param: [byteArray]
     * @return: java.lang.Object
     * @Author: NoCortY
     * @Date: 2019/10/4
     */
    public static Object unSerialize(byte[] byteArray){
        ByteArrayInputStream bais = null;
        try {
            //反序列化为对象
            bais = new ByteArrayInputStream(byteArray);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();

        } catch (Exception e) {
            logger.error("反序列化异常");
            logger.error("异常信息:{}",e.getMessage());
        }
        return null;
    }

}