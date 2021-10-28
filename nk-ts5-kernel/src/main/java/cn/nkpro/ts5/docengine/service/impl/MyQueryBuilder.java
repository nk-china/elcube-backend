package cn.nkpro.ts5.docengine.service.impl;


import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryShardContext;

import java.io.IOException;
import java.util.Objects;


