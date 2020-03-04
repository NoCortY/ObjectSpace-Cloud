package cn.objectspace.componentcenter.daotest;

import cn.objectspace.componentcenter.ComponentCenterStarter;
import cn.objectspace.componentcenter.dao.ComponentDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ComponentCenterStarter.class})// 指定启动类
public class DaoTest {
    @Autowired
    private ComponentDao componentDao;

    @Test
    public void queryUserTest() {
        Long start = System.currentTimeMillis() - 1000000000000L;
        Long end = System.currentTimeMillis();
        System.out.println(componentDao.queryCpuRuntimeRecord(1, "1", new Date(start), new Date(end)));
    }
}
