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
package cn.nkpro.elcard.data.elasticearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryShardContext;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.Objects;

/**
 * 自定义的QueryBuilder
 *
 * 用于解析前端配置的Limit表达式
 */
public class LimitQueryBuilder extends AbstractQueryBuilder<LimitQueryBuilder> {

    private JSONObject source;

    public LimitQueryBuilder(String str){
        this.source = JSON.parseObject(str);
    }

    public LimitQueryBuilder(JSONObject str){
        this.source = str;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.map(source);
        return builder;
    }

    @Override
    protected void doXContent(XContentBuilder xContentBuilder, Params params) {
        throw new UnsupportedOperationException("不需要实现");
    }

    @Override
    protected void doWriteTo(StreamOutput streamOutput) {
        throw new UnsupportedOperationException("不知道干嘛用的");
    }

    @Override
    protected Query doToQuery(QueryShardContext queryShardContext) {
        throw new UnsupportedOperationException("不知道干嘛用的");
    }

    @Override
    protected boolean doEquals(LimitQueryBuilder myQueryBuilder) {
        return Objects.equals(myQueryBuilder.source,source);
    }

    @Override
    protected int doHashCode() {
        return source.hashCode();
    }

    @Override
    public String getWriteableName() {
        return StringUtils.EMPTY;
    }
}