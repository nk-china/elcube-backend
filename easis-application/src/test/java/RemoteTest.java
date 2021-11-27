import cn.nkpro.easis.TS5Application;
import cn.nkpro.easis.co.NkCustomObjectManager;
import cn.nkpro.easis.co.remote.NkRemoteAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by bean on 2020/7/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TS5Application.class})
public class RemoteTest {

    @Autowired
    NkCustomObjectManager customObjectManager;

    @Data
    @AllArgsConstructor
    public static class A{
        private String a;
    }
    @Test
    public void test1() throws Exception {

        NkRemoteAdapter nkDefault1RemoteAdapter = customObjectManager.getCustomObject("NkDefault1RemoteAdapter", NkRemoteAdapter.class);

        System.out.println(nkDefault1RemoteAdapter.apply("1",String.class));
        System.out.println(customObjectManager.getCustomObject("NkDefaultRemoteAdapter", NkRemoteAdapter.class).apply(new A("asf"),String.class));

    }
}
