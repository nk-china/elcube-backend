//import cn.nkpro.tfms.platform.custom.determine.algorithm.*;
//import cn.nkpro.tfms.platform.custom.determine.algorithm.context.AlgorithmContext;
//import cn.nkpro.tfms.platform.custom.determine.algorithm.context.Fixed;
//import cn.nkpro.tfms.platform.custom.determine.algorithm.context.Rate;
//import cn.nkpro.tfms.platform.custom.determine.algorithm.context.Subsidy;
//import cn.nkpro.tfms.platform.custom.determine.algorithm.flow.*;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//public class PaymentAlgorithm {
//
//    private Algorithm algorithm = new Algorithm();
//
//
//    @Test
//    public void test3(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2020,0,1,0,0,0);
//        calendar.set(Calendar.MILLISECOND,0);
//
//        List<FlowType> flows = new ArrayList<>();
//
//        FlowType_LOAN flowLa = new FlowType_LOAN();
//        flowLa.setAmount(100000d);
//        flowLa.setExpireDate(calendar.getTimeInMillis());
//        flows.add(flowLa);
//
//        FlowType_Loop_P flowP = new FlowType_Loop_P();
//        flowP.setStartDate(flowLa.getExpireDate());
//        calendar.set(2020,3,1);
//        flowP.setExpireDate(calendar.getTimeInMillis());
//        calendar.set(2021,0,1);
//        flowP.setEndDate(calendar.getTimeInMillis());
//        flowP.setFrequency("ENDSQ");
//        flows.add(flowP);
//
//        List<Cash> cashs = algorithm.cale(flows,new Algorithm());
//        cashs.forEach(System.out::println);
//        System.out.println("=============================");
//        algorithm.toView(cashs).forEach(System.out::println);
//    }
//
//    @Test
//    public void test2(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2020,0,1,0,0,0);
//        calendar.set(Calendar.MILLISECOND,0);
//
//        List<FlowType> flows = new ArrayList<>();
//
//        FlowType_LOAN flowLa = new FlowType_LOAN();
//        flowLa.setAmount(1000000d);
//        flowLa.setExpireDate(calendar.getTimeInMillis());
//        flows.add(flowLa);
//
//        FlowType_Loop_P flowP = new FlowType_Loop_P();
//        flowP.setStartDate(flowLa.getExpireDate());
//        calendar.set(2020,3,1);
//        flowP.setExpireDate(calendar.getTimeInMillis());
//        calendar.set(2021,0,1);
//        flowP.setEndDate(calendar.getTimeInMillis());
//        flowP.setFrequency("ENDSQ");
//        flows.add(flowP);
//
//        FlowType_Loop_L flowI = new FlowType_Loop_L();
//        flowI.setStartDate(flowLa.getExpireDate());
//        calendar.set(2020,1,1);
//        flowI.setExpireDate(calendar.getTimeInMillis());
//        calendar.set(2021,0,1);
//        flowI.setEndDate(calendar.getTimeInMillis());
//        flowI.setRate(5.88*0.01);
//        flowI.setFrequency("ENDSM");
//        flows.add(flowI);
//
//
//
//        List<Fixed> fixeds = new ArrayList<>();
//        Fixed fixed = new Fixed();
//        fixed.setAmount(10000d);
//        fixed.setPeriodStart(1);
//        fixed.setPeriodEnd(2);
//        fixeds.add(fixed);
//
//        List<Subsidy> subsidys = new ArrayList<>();
//        Subsidy subsidy = new Subsidy();
//        subsidy.setPercent(1d);
//        subsidy.setPeriodStart(1);
//        subsidy.setPeriodEnd(5);
//        subsidys.add(subsidy);
//
//        List<Rate> rates = new ArrayList<>();
//        Rate rate = new Rate();
//        rate.setDateStart(0l);
//        calendar.set(2020,10,20);
//        rate.setDateEnd(calendar.getTimeInMillis());
//        rate.setValue(5.88*0.01);
//        rates.add(rate);
//        rate = new Rate();
//        rate.setDateStart(calendar.getTimeInMillis());
//        rate.setDateEnd(Long.MAX_VALUE);
//        rate.setValue(1*0.01);
//        rates.add(rate);
//
//        Algorithm context = new Algorithm();
//        context.setFixeds(fixeds);
//        //context.setSubsidys(subsidys);
//        context.setRates(rates);
//
//        List<Cash> cashs = algorithm.cale(flows,context);
//        cashs.forEach(System.out::println);
//        System.out.println("=============================");
//        algorithm.toView(cashs).forEach(System.out::println);
//    }
//
//
//    @Test
//    public void test(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2020,0,1,0,0,0);
//        calendar.set(Calendar.MILLISECOND,0);
//
//        List<FlowType> flows = new ArrayList<>();
//
//        FlowType_LOAN flowLa = new FlowType_LOAN();
//        flowLa.setAmount(10000000d);
//        flowLa.setExpireDate(calendar.getTimeInMillis());
//        flows.add(flowLa);
//
////        calendar.set(2020,8,1,0,0,0);
////        flowLa = new FlowType_LA();
////        flowLa.setAmount(5000000d);
////        flowLa.setExpireDate(calendar.getTimeInMillis());
////        flows.add(flowLa);
//
//        FlowType_Loop_PI flowPi = new FlowType_Loop_PI();
//        flowPi.setStartDate(flowLa.getExpireDate());
//        calendar.set(2020,1,1);
//        flowPi.setExpireDate(calendar.getTimeInMillis());
//        calendar.set(2022,0,1);
//        flowPi.setEndDate(calendar.getTimeInMillis());
//        flowPi.setRate(5.88*0.01);
//        flowPi.setFrequency("ENDSM");
//
//        flows.add(flowPi);
//
//        FlowType_P flowP = new FlowType_P();
//        flowP.setAmount(1000000d);
//        calendar.set(2020,11,1);
//        flowP.setExpireDate(calendar.getTimeInMillis());
//        flows.add(flowP);
//
//        FlowType_BALA flowLb = new FlowType_BALA();
//        flowLb.setAmount(1000000d);
//        calendar.set(2022,0,1);
//        flowLb.setExpireDate(calendar.getTimeInMillis());
//        flows.add(flowLb);
//
//        List<Fixed> fixeds = new ArrayList<>();
//        Fixed fixed = new Fixed();
//        fixed.setAmount(0d);
//        fixed.setPeriodStart(1);
//        fixed.setPeriodEnd(3);
//        fixeds.add(fixed);
//        fixed = new Fixed();
//        fixed.setAmount(10000d);
//        fixed.setPeriodStart(1);
//        fixed.setPeriodEnd(4);
//        fixeds.add(fixed);
//
//        List<Subsidy> subsidys = new ArrayList<>();
//        Subsidy subsidy = new Subsidy();
//        subsidy.setPercent(1d);
//        subsidy.setPeriodStart(5);
//        subsidy.setPeriodEnd(5);
//        subsidys.add(subsidy);
//
//        List<Rate> rates = new ArrayList<>();
//        Rate rate = new Rate();
//        rate.setDateStart(0l);
//        calendar.set(2020,10,20);
//        rate.setDateEnd(calendar.getTimeInMillis());
//        rate.setValue(5.88*0.01);
//        rates.add(rate);
//        rate = new Rate();
//        rate.setDateStart(calendar.getTimeInMillis());
//        rate.setDateEnd(Long.MAX_VALUE);
//        rate.setValue(5.44*0.01);
//        rates.add(rate);
//
//        Algorithm context = new Algorithm();
//        context.setFixeds(fixeds);
//        context.setSubsidys(subsidys);
//        context.setRates(rates);
//
//        List<Cash> cashs = algorithm.cale(flows, context);
//        cashs.forEach(System.out::println);
//        System.out.println("=============================");
//        algorithm.toView(cashs).forEach(System.out::println);
//    }
//
//
//
//}
