//import cn.nkpro.ts5.TS5Application;
//import cn.nkpro.ts5.basic.PageList;
//import cn.nkpro.ts5.dataengine.service.impl.ClickHouseService;
//import com.alibaba.fastjson.JSON;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * Created by bean on 2020/7/22.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes={TS5Application.class})
//public class ClickHouseTest {
//
//    @Autowired
//    ClickHouseService service;
//
//    @Test
//    public void test1() {
//
//        PageList page = service.queryPage(
//                "select toStartOfInterval(EventTime, interval 1 day ) as date,count(1)" +
//                        "  from hits_v1" +
//                        " group by date",
//                0, 10
//        );
//
//        System.out.println(JSON.toJSONString(page));
//    }
//}
