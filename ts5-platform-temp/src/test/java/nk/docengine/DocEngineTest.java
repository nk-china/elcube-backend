package nk.docengine;

import cn.nkpro.TfmsPlatformApplication;
import cn.nkpro.ts5.engine.doc.impl.NkDocEngineImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by bean on 2020/7/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TfmsPlatformApplication.class})
public class DocEngineTest {
    @Autowired
    private NkDocEngineImpl docEngine;

    @Test
    public void test1(){
        docEngine.getDoc("1");
    }
}
