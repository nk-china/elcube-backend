package cn.nkpro.ts5.test;

import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.CustomES;
import cn.nkpro.ts5.orm.mb.gen.DocH;
import cn.nkpro.ts5.orm.mb.gen.DocHMapper;
import cn.nkpro.ts5.utils.TextUtils;
import com.apifan.common.random.constant.CompetitionType;
import com.apifan.common.random.constant.CreditCardType;
import com.apifan.common.random.source.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class RandomDocCreator {

    @Autowired
    @Qualifier("nkTaskExecutor")
    private TaskExecutor taskExecutor;
    @Autowired
    private SearchEngine searchEngine;
    @Autowired
    private DocHMapper docHMapper;

    public void random(int count){
        List<DocH> hes = docHMapper.selectByExample(null, new RowBounds(0, 10));

        LocalDateTime begin = LocalDateTime.of(1990,1,1,0,0,0);
        LocalDateTime end   = LocalDateTime.of(2021,12,31,23,59,59);
        LocalDate beginDate = LocalDate.of(1990,1,1);
        LocalDate endDate   = LocalDate.of(2021,12,31);

        int thread = 16;
        for(int t=0;t<thread;t++){
            taskExecutor.execute(() -> {
                for (int i=0;i<count/thread;i++){

                    CustomES c = new CustomES();
                    try{
                        DocH doc = hes.get(NumberSource.getInstance().randomInt(0, hes.size()));

                        c.setCustomId(UUID.randomUUID().toString());
                        c.setCustomType("ES:TEST");
                        c.setDocNumber(doc.getDocNumber());
                        c.setDocName(TextUtils.randomText());

                        c.setDocId(doc.getDocId());
                        c.setClassify(doc.getClassify());

                        c.setPartnerName(PersonInfoSource.getInstance().randomChineseName());
                        c.setTags(FinancialSource.getInstance().randomStock());
                        c.setCreatedTime(DateTimeSource.getInstance().randomTimestamp(begin, end)/1000);
                        c.setUpdatedTime(DateTimeSource.getInstance().randomTimestamp(begin, end)/1000);

                        String[] city = AreaSource.getInstance().randomCity(",").split("[,]");
                        boolean bool1 = NumberSource.getInstance().randomInt(0, 2)==0;
                        int index     = NumberSource.getInstance().randomInt(0, 2);

                        List<String> likes = new ArrayList<>();
                        for (int j=0;j<=NumberSource.getInstance().randomInt(0, 4);j++){
                            switch (NumberSource.getInstance().randomInt(0, 16)){
                                case 0:
                                case 1:
                                case 2:likes.add(SportSource.getInstance().randomFootballTeam(CompetitionType.PREMIER_LEAGUE));break;
                                case 3:likes.add(SportSource.getInstance().randomFootballTeam(CompetitionType.LA_LIGA));break;
                                case 4:likes.add(SportSource.getInstance().randomFootballTeam(CompetitionType.BUNDESLIGA));break;
                                case 5:likes.add(SportSource.getInstance().randomFootballTeam(CompetitionType.SERIE_A));break;
                                case 6:likes.add(SportSource.getInstance().randomFootballTeam(CompetitionType.LIGUE_1));break;
                                case 7:likes.add(SportSource.getInstance().randomFootballTeam(CompetitionType.EREDIVISIE));break;
                                case 8:likes.add(SportSource.getInstance().randomBasketballTeam(CompetitionType.CBA));break;
                                case 9:
                                case 10:likes.add(SportSource.getInstance().randomBasketballTeam(CompetitionType.NBA));break;
                                case 11:likes.add(SportSource.getInstance().randomFootballTeam().getName());break;
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                    String[] stock = FinancialSource.getInstance().randomStock();
                                    if(stock!=null){
                                        likes.add(StringUtils.join(stock,","));
                                    }
                                    break;
                            }
                        }

                        c.getDynamics().put("province_keyword", city[0]);
                        c.getDynamics().put("city_keyword",     city[1]);

                        c.getDynamics().put("company_name",     OtherSource.getInstance().randomCompanyName(city[index]));
                        c.getDynamics().put("department_name",  OtherSource.getInstance().randomCompanyDepartment());
                        c.getDynamics().put("plate_serial",     OtherSource.getInstance().randomPlateNumber());
                        c.getDynamics().put("college_name",     EducationSource.getInstance().randomCollege());
                        c.getDynamics().put("sentence_text",    OtherSource.getInstance().randomChineseSentence());
                        c.getDynamics().put("likes_keyword",    likes.toArray(new String[0]));

                        c.getDynamics().put("lat_double",       AreaSource.getInstance().randomLatitude());
                        c.getDynamics().put("lng_double",       AreaSource.getInstance().randomLongitude());

                        c.getDynamics().put("date1_date",       DateTimeSource.getInstance().randomTimestamp(begin, end)/1000);
                        c.getDynamics().put("date2_date",       DateTimeSource.getInstance().randomTimestamp(begin, end)/1000);

                        c.getDynamics().put("num1_int",         NumberSource.getInstance().randomInt(0, 10001));
                        c.getDynamics().put("num2_long",        NumberSource.getInstance().randomLong(10000000000L, 20000000001L));
                        c.getDynamics().put("num3_float",       NumberSource.getInstance().randomDouble(0.0001D, 0.9999D));
                        c.getDynamics().put("num4_float",       NumberSource.getInstance().randomDouble(0.0001D, 0.9999D));
                        c.getDynamics().put("num5_double",      NumberSource.getInstance().randomDouble(1000D, 1000000D));
                        c.getDynamics().put("num6_double",      NumberSource.getInstance().randomDouble(100000D, 100000000D));

                        c.getDynamics().put("english_name",     PersonInfoSource.getInstance().randomEnglishName());
                        c.getDynamics().put("phone_serial",     PersonInfoSource.getInstance().randomChineseMobile());
                        c.getDynamics().put("creditCard_serial",PersonInfoSource.getInstance().randomCreditCardNo(CreditCardType.UnionPay));
                        c.getDynamics().put("idCard_serial",    bool1?
                                PersonInfoSource.getInstance().randomMaleIdCard(AreaSource.getInstance().randomProvince(), beginDate, endDate):
                                PersonInfoSource.getInstance().randomFemaleIdCard(AreaSource.getInstance().randomProvince(), beginDate, endDate));
                        c.getDynamics().put("domain_url",       InternetSource.getInstance().randomDomain(16));
                        c.getDynamics().put("email_url",        InternetSource.getInstance().randomEmail(20));
                        c.getDynamics().put("phone2_serial",    AreaSource.getInstance().randomPhoneNumber(AreaSource.getInstance().randomProvince(), "-"));

                    }catch (Exception ignored){}
                    searchEngine.indexBeforeCommit(c);
                }
            });
        }
    }
}
