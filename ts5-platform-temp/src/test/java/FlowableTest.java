import cn.nkpro.TfmsPlatformApplication;
import org.flowable.engine.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by bean on 2019/12/19.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TfmsPlatformApplication.class})
public class FlowableTest {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessEngine processEngine;

    public void test1(){
        repositoryService.createDeployment()
                .addClasspathResource("processes/qingjia.bpmn")
                .deploy();
    }

    @Test
    public void test3(){


//        // 查询流程实例
//        runtimeService.createProcessInstanceQuery()
//                .list()
//                .forEach(instance -> {
//                    System.out.println("存在的实例");
//                    System.out.println(instance);
//                    System.out.println(instance.getId());
//                    System.out.println(instance.getBusinessKey());
//                    runtimeService.deleteProcessInstance(instance.getId(),"Bye bye");
//                });
//        System.out.println("==================================================================");
//
//        Deployment deploy = processEngine.getRepositoryService()
//                .createDeployment()
//                .addClasspathResource("processes/qingjia.bpmn")
//                .deploy();
//        System.out.println(deploy);


//        // 查询部署记录
//        repositoryService.createDeploymentQuery()
//                .list()
//                .forEach(deployment -> {
//                    System.out.println("部署历史");
//                    System.out.println(deployment);
//                });



        repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .list()
                .forEach(processDefinition -> {
                    System.out.println(processDefinition.getName());
                });


//        // 查询流程定义
//        {
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
//                .latestVersion()
//                .list()
//                .stream()
//                .findAny()
//                .orElse(null);
//
//
//        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
//
//        InputStreamReader isr = new InputStreamReader(inputStream);
//        BufferedReader br = new BufferedReader(isr);
//
//        try{
//
//            String line = null;
//            while((line=br.readLine())!=null) {
//                System.out.println(line);
//            }
//        }catch (Exception e){
//
//        }




        Map<String,Object> variables = new HashMap<>();
            variables.put("实例参数",3);

//            ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(),variables);
//            System.out.println("创建新实例");
//            System.out.println(processInstance);
//        }
//
//
//        taskService.createTaskQuery().list().forEach(task -> {
//            System.out.println("经理");
//            System.out.println(task);
//            System.out.println(task.getExecutionId());
//            System.out.println(task.getProcessVariables());
//
//            System.out.println(runtimeService.getVariables(task.getProcessInstanceId()));
//            //System.out.println(runtimeService.getVariables(task.getId()));
//            System.out.println(runtimeService.getVariables(task.getExecutionId()));
//
//            Map<String,Object> variables = new HashMap<>();
//            variables.put("day",3);
//
//            taskService.addComment(task.getId(),task.getProcessInstanceId(),"我是经理，我同意");
//            taskService.complete(task.getId(),variables);
//        });


//        taskService.createTaskQuery().list().forEach((Task task) -> {
//            System.out.println("人力资源");
//            System.out.println(task);
//            System.out.println(task.getExecutionId());
//            System.out.println(task.getProcessVariables());
//
//            System.out.println(runtimeService.getVariables(task.getProcessInstanceId()));
//            //System.out.println(runtimeService.getVariables(task.getId()));
//            System.out.println(runtimeService.getVariables(task.getExecutionId()));
//
//
//            System.out.println(taskService.getProcessInstanceComments(task.getProcessInstanceId()));
//            System.out.println(taskService.getProcessInstanceComments(task.getId()));
//            System.out.println(taskService.getProcessInstanceComments(task.getExecutionId()));
//
//            taskService.getProcessInstanceComments(task.getProcessInstanceId())
//                    .forEach(comment -> {
//                        System.out.println(comment.getProcessInstanceId());
//                        System.out.println(comment.getTaskId());
//                        System.out.println(comment.getFullMessage());
//                    });
//            System.out.println();
//            System.out.println(taskService.getTaskComments(task.getId()));
//            System.out.println(taskService.getTaskComments(task.getExecutionId()));
//
//            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//                    .processInstanceId(task.getProcessInstanceId())
//                    .singleResult();
//
//            // 一会再测试
//            System.out.println(processInstance.getProcessVariables());
//
//
//            taskService.getTaskComments(task.getId())
//                    .forEach(System.out::println);
//            taskService.getProcessInstanceComments(task.getProcessInstanceId())
//                    .forEach(System.out::println);
//
//            FlowElement flowElement = repositoryService.getBpmnModel(task.getProcessDefinitionId())
//                    .getFlowElement(task.getTaskDefinitionKey());
//
//            ((FlowNode) flowElement).getOutgoingFlows()
//                .forEach(sequenceFlow -> {
//                    System.out.println(sequenceFlow.getName());
//                    System.out.println(sequenceFlow.getDocumentation());
//                });
//
//            taskService.addComment(task.getId(),null,"我是人力资源，我拒绝");
//            Map<String,Object> variables = new HashMap<>();
//            variables.put("btn","拒绝");
//            taskService.complete(task.getId(), variables);
//        });
//
//
//        taskService.createTaskQuery().list().forEach((Task task) -> {
//            System.out.println("谁的任务？");
//            System.out.println(task);
//            System.out.println(task.getExecutionId());
//            System.out.println(task.getProcessVariables());
//
//            taskService.complete(task.getId());
//        });
//
//
//        taskService.createTaskQuery().list().forEach((Task task) -> {
//            System.out.println("没有任务了");
//            System.out.println(task);
//            System.out.println(task.getExecutionId());
//            System.out.println(task.getProcessVariables());
//
//            taskService.complete(task.getId());
//        });

    }
}
