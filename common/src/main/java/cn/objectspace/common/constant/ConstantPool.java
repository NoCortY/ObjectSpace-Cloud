package cn.objectspace.common.constant;

/**
* @Description: 常量池
* @Author: NoCortY
* @Date: 2019/12/20
*/
public class ConstantPool {
    public static class Common{
        /**
         *  返回状态码的key
         */
        public static final String RES_CODE="code";
        /**
         * 返回状态消息的key
         */
        public static final String RES_MSG = "message";
        /**
         * 返回数据的key
         */
        public static final String RES_DATA = "data";
        /**
         * 通用请求成功状态码
         */
        public static final String REQUEST_SUCCESS_CODE="1001";
        /**
         * 通用请求成功消息
         */
        public static final String REQUEST_SUCCESS_MESSAGE="请求成功";
        /**
         * 通用请求失败状态码
         */
        public static final String REQUEST_FAILURE_CODE="-1001";
        /**
         * 通用请求失败状态消息
         */
        public static final String REQUEST_FAILURE_MESSAGE="请求失败";
        /**
         * 当前接口无需返回实体数据
         */
        public static final String RES_NOT_DATA="Have not data in this function";
    }
    public static class LogCenter{
        /**
         *日志中心应用ID
         */
        public static final int APPLICATION_ID = 2;
        /**
         * 日志中心应用名
         */
        public static final String LC_APPLICATION_NAME = "http://ObjectService-LC";
        /**
         * 日志中心url
         */
        public static final String LC_URL = "http://127.0.0.1:7302";
        /**
         * 记录日志失败错误码
         */
        public static final String RECORD_LOG_FALURE_CODE = "-1001";
        /**
         * 记录日志失败消息
         */
        public static final String RECORD_LOG_FALURE_MESSAGE="日志入库失败";
        /**
         * 记录日志成功状态码
         */
        public static final String RECORD_LOG_SUCCESS_CODE = "1001";
        /**
         * 记录日志成功消息
         */
        public static final String RECORD_LOG_SUCCESS_MESSAGE="日志入库成功";
        /**
         * 日志传输对象名
         */
        public static final String RECORD_LOG_NAME = "log";
    }
    public static class Shiro{

        /**
         * 认证中心应用ID
         */
        public static final int APPLICATION_ID = 1;


        /**
         * 认证中心应用名
         */
        public static final String AC_APPLICATION_NAME = "http://ObjectService-AC";

        /**
         * 认证中心url
         */
        public static final String AC_URL = "http://127.0.0.1:7301";
        /**
         * 验证码存储key
         * key:用户邮箱
         */
        public static final String VERIFY_KEY = "verify:";
        /**
         * 项目使用邮箱
         */
        public static final String FROM_EMAIL = "ObjectSpace@126.com";

        public static final Integer GENERAL_USER_ROLE_ID = 2;
        /**
         * 存用户信息的KEY名 每个登录用户只有可能有一个
         */
        public static final String AC_UUID = "ACId";
        /**
         * 密钥TOKEN COOKIE
         */
        public static final String AC_TOKEN = "ACToken";
        /**
         * 授权失败码
         */
        public static final String AZ_FAILURE_CODE = "-1002";
        /**
         * 授权失败消息
         */
        public static final String AZ_FAILURE_MESSAGE="授权失败";
        /**
         * 认证失败码
         */
        public static final String AC_FAILURE_CODE="-1001";
        /**
         * 认证失败消息
         */
        public static final String AC_FAILURE_MESSAGE="认证失败";
        /**
         * 已封禁用户码
         */
        public static final String AC_BINDED_CODE = "-1003";
        /**
         * 已封禁用户消息
         */
        public static final String AC_BINDED_MESSAGE="该账号已被封禁";
        /**
         * 用户不存在码
         */
        public static final String AC_UKNOWN_ACCOUNT="-1004";
        /**
         * 用户不存在消息
         */
        public static final String AC_UKNOWN_ACCOUNT_MESSAGE="该用户不存在";
        /**
         * 认证成功码
         */
        public static final String AC_SUCCESS_CODE = "1001";
        /**
         * 认证成功消息
         */
        public static final String AC_SUCCESS_MESSAGE="认证成功";
        /**
         * 授权成功码
         */
        public static final String AZ_SUCCESS_CODE="1002";
        /**
         * 授权成功消息
         */
        public static final String AZ_SUCCESS_MESSAGE="授权成功";
        /**
         * 加密算法
         */
        public static final String ALGORITHM_NAME = "md5";
        /**
         * 加密次数
         */
        public static final Integer ITERATIONS = 3;
    }
    public static class ComponentCenter{
        public static final int APPLICATION_ID=3;
        public static final String REGISTER_SERVER_SUCCESS="服务器注册成功";
        public static final String REGISTER_SERVER_FALURE="服务器注册失败";
        public static final String ERROR_SERVER_IP = "错误的服务器IP";

        /**
         * 用户授权信息存入redis key
         */
        public static final String URPDTO_REDIS_KEY_CC = "urpdto_redis_key_cc:";
    }
}
