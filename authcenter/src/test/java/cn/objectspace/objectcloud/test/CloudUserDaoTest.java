package cn.objectspace.objectcloud.test;

import cn.objectspace.authcenter.AuthCenterStarter;
import cn.objectspace.authcenter.dao.CloudUserDao;
import cn.objectspace.authcenter.pojo.entity.CloudUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AuthCenterStarter.class})// 指定启动类
public class CloudUserDaoTest {
    @Autowired
    private CloudUserDao cloudUserDao;
    @Test
    public void queryUserTest(){
        CloudUser cloudUser = new CloudUser();
        cloudUser.setUserId(1);
        cloudUser = cloudUserDao.queryCloudUser(cloudUser);
        System.out.println(cloudUser);
    }
}
