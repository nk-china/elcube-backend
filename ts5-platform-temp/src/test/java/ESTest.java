
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by bean on 2020/7/22.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes={TfmsPlatformApplication.class})
public class ESTest {



//    @Autowired
//    private ElasticsearchRestTemplate restTemplate;
//
//    @Autowired
//    private SearchEngine searchEngine;

//    public void testDynamicMappings() throws IOException {
//
//
//
//        searchEngine.deleteIndices(IndexDoc.class);
//        searchEngine.createIndices(IndexDoc.class);
//
//        IndexDoc doc = new IndexDoc();
//        doc.setDocId("1");
//        doc.addDynamicField("a_int",1);
//
//        searchEngine.indexAfterCommit(doc);
//
//    }


    @Test
    public void test1() throws IOException {

        System.out.println("123".hashCode());

//
//        Arrays.stream(StringUtils.split("1|2",'|'))
//                .forEach(System.out::println);
//
//        List<TfmsGrantedAuthority> list = new ArrayList<>();
//
//        TfmsGrantedAuthority authority = new TfmsGrantedAuthority();
//        authority.setPermResource("a");
//        authority.setPermOperate("1");
//        authority.setLimitId("1");
//        authority.setRole(String.format("%s:%s",authority.getPermResource(),authority.getPermOperate()));
//        authority.setSortKey(String.format("%s:%s:%s",authority.getPermResource(),authority.getPermOperate(), StringUtils.defaultString(authority.getLimitId())));
//        list.add(authority);
//
//        authority = new TfmsGrantedAuthority();
//        authority.setPermResource("a");
//        authority.setPermOperate("*");
//        authority.setLimitId("1");
//        authority.setRole(String.format("%s:%s",authority.getPermResource(),authority.getPermOperate()));
//        authority.setSortKey(String.format("%s:%s:%s",authority.getPermResource(),authority.getPermOperate(), StringUtils.defaultString(authority.getLimitId())));
//        list.add(authority);
//
//        authority = new TfmsGrantedAuthority();
//        authority.setPermResource("a");
//        authority.setPermOperate("*");
//        authority.setLimitId(null);
//        authority.setRole(String.format("%s:%s",authority.getPermResource(),authority.getPermOperate()));
//        authority.setSortKey(String.format("%s:%s:%s",authority.getPermResource(),authority.getPermOperate(), StringUtils.defaultString(authority.getLimitId())));
//        list.add(authority);
//
//        list.stream()
//                .sorted()
//                .distinct()
//                .forEach(System.out::println);
//



    }

    @Test
    public void test() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .postFilter(QueryBuilders.boolQuery()
                        .should(
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.termsQuery("classify","TRANSACTION"))
                                        .must(QueryBuilders.prefixQuery("docId","1ed9b649"))
                        )
                        .should(
                                QueryBuilders
                                        .wrapperQuery("{\n" +
                                                "                    \"bool\": {\n" +
                                                "                        \"must\": [\n" +
                                                "                            {\n" +
                                                "                                \"term\": {\n" +
                                                "                                    \"updatedTime\": {\n" +
                                                "                                        \"value\": \"1594201083\",\n" +
                                                "                                        \"boost\": 1.0\n" +
                                                "                                    }\n" +
                                                "                                }\n" +
                                                "                            }\n" +
                                                "                        ]\n" +
                                                "                    }\n" +
                                                "                }")
//                                        .boolQuery()
//////                                        .must(QueryBuilders.termQuery("classify","TRANSACTION"))
//                                        .must(QueryBuilders.termQuery("updatedTime","1594201083"))
                        )
                )
                .query(QueryBuilders.boolQuery()
//                        .must(QueryBuilders.termsQuery("docType","HT01"))
                        .must(QueryBuilders.matchAllQuery())

                );

        System.out.println(sourceBuilder);
        SearchRequest searchRequest = new SearchRequest()
                .indices("document")
                .source(sourceBuilder);

//        SearchResponse search = restTemplate.getClient().search(searchRequest, RequestOptions.DEFAULT);
//
//        System.out.println(search);
    }
}
