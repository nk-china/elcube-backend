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
package cn.nkpro.elcube.docengine.gen;

import java.io.Serializable;

public class DocAsyncQueueWithBLOBs extends DocAsyncQueue implements Serializable {
    private String asyncJson;

    private String asyncCauseStackTrace;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getAsyncJson() {
        return asyncJson;
    }

    public void setAsyncJson(String asyncJson) {
        this.asyncJson = asyncJson;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getAsyncCauseStackTrace() {
        return asyncCauseStackTrace;
    }

    public void setAsyncCauseStackTrace(String asyncCauseStackTrace) {
        this.asyncCauseStackTrace = asyncCauseStackTrace;
    }
}