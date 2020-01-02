package cn.objectspace.objectcloud.test;

import cn.objectspace.authcenter.AuthCenterStarter;
import cn.objectspace.authcenter.dao.ShiroDao;
import cn.objectspace.authcenter.pojo.entity.CloudUser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AuthCenterStarter.class})// 指定启动类
public class ShiroDaoTest {
    @Autowired
    private ShiroDao shiroDao;
    @Test
    public void insertUserTest(){
        CloudUser cloudUser = new CloudUser();
        cloudUser.setUserEmail("nocorty@163.com");
        cloudUser.setUserName("NoCortY");
        cloudUser.setUserPassword("123");
        cloudUser.setUserLastLoginDate(new Date());
        cloudUser.setUserProfile("/123/123");
        cloudUser.setUserStatus(true);
        cloudUser.setUserSalt("213jhiuds");
        cloudUser.setUserRegisterDate(new Date());
        int effectNum = shiroDao.insertCloudUser(cloudUser);
        Assert.assertEquals(effectNum,1L);
    }
}