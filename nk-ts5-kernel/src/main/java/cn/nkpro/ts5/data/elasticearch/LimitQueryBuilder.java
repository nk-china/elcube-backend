package cn.nkpro.ts5.data.elasticearch;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryShardContext;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.Objects;

@AllArgsConstructor
public class LimitQueryBuilder extends AbstractQueryBuilder<LimitQueryBuilder> {

    private String source;

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.map(JSON.parseObject(source));
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