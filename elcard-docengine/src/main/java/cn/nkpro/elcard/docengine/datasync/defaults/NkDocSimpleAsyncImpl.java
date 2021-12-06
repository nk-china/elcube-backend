/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.datasync.defaults;

import cn.nkpro.elcard.docengine.datasync.NkAbstractDocDataAsyncAdapter;
import cn.nkpro.elcard.docengine.gen.DocAsyncQueue;
import cn.nkpro.elcard.exception.NkSystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("NkDocSimpleAsync")
public class NkDocSimpleAsyncImpl extends NkAbstractDocDataAsyncAdapter {

    @Override
    protected void schedule(DocAsyncQueue asyncQueue) {

        log.info(asyncQueue.toString());
        if(Math.random()>0)
            throw new NkSystemException("自定义错误");
    }
}
