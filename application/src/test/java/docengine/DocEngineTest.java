/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package docengine;

import cn.nkpro.elcube.ELCubeApplication;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.gen.DocH;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by bean on 2020/7/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={ELCubeApplication.class})
public class DocEngineTest {
    @Autowired
    private NkDocEngine docEngine;

    @Test
    public void test1() throws Exception {

        DocH docH = docEngine.find("BR03","BR04")
                .dynamicBetween("payment_keyword", 1, 3)
                .dynamicEquals("createdTime_keyword", 1639207678)
                .dynamicIn("payment_keyword", 1,2,3,4,5)
                .dynamicExists("payment_keyword")
                .dynamicNotExists("payment_keyword1")
                .orderByDesc("DOC_ID")
                .singleResult();

        System.out.println(JSON.toJSONString(docH));
    }
}
