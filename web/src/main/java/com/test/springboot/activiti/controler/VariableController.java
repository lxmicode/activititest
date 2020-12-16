package com.test.springboot.activiti.controler;

import com.test.springboot.activiti.entity.Result;
import com.test.springboot.activiti.entity.VariableEntity;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程变量
 */
@RestController
@RequestMapping("/var/")
public class VariableController {


    //资源管理类
    @Autowired
    RepositoryService repositoryService;
    //任务管理类
    @Autowired
    TaskService taskService;
    //流行运行管理
    @Autowired
    RuntimeService runtimeService;


    private String tenantId = "LXM";


    /**
     * 启动流程
     * 1.在开始的流程时候，设置一个流程的全局变量
     *
     * 效果：设置请假3天，根据请假天数，去到不同处理人
     * 注意：变量可以在流程结束前，任意阶段设置
     * @param id
     * @return
     */
    @GetMapping("/start")
    Object start(String id) {
        try {

            ProcessInstance processInstance = null;

            //map key必须与BPMN文件的assignee字段uel表达式保持一致
            Map<String, Object> assigneeMap = new HashMap<>();
            assigneeMap.put("days","3");
            //修改重新部署，可以测试
//            assigneeMap.put("days","4");
            //根据流程实例编号查询
            processInstance = runtimeService.startProcessInstanceById(id,assigneeMap);

            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.OK.value()
                            , HttpStatus.OK.getReasonPhrase()
                            , processInstance.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                            , HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                            , e.getMessage()));
        }

    }

    /**
     * 给流程设置变量
     * 1.根据任务编号设置变量
     *
     * 注意：
     *  1.设置流程变量使用 runtimeService.setVariables(exceId,maps);
     *  2.给任务变量使用 taskService.setVariables(taskId,maps);
     *
     * uel表达式
     * ${key} 需要map中存入对应的key/value
     * ${key.属性} 需要map中存入对应的key/object
     * @param proInsId 流程实例编号
     * @return
     */
    @GetMapping("/setVar")
    Object setVar(String proInsId) {
        try {
            Task task = taskService.createTaskQuery()
                    .processInstanceId(proInsId).taskTenantId(tenantId).singleResult();

            ProcessInstance instance = runtimeService
                    .createProcessInstanceQuery().processInstanceId(proInsId).singleResult();

            //存入对象，画图中表达式可以使用${entity.属性}
            VariableEntity entity = new VariableEntity("001","请假申请",3d);



            //map key必须与BPMN文件的assignee字段uel表达式保持一致
            Map<String, Object> assigneeMap = new HashMap<>();
            assigneeMap.put("user1","jacK");
            assigneeMap.put("user2","mario");
            assigneeMap.put("user3","mike");
            assigneeMap.put("days","3");
            assigneeMap.put("entity",entity);

            //设置单个
//            runtimeService.setVariable(task.getExecutionId(),"entity",entity);
            //设置多个
            runtimeService.setVariables(task.getExecutionId(),assigneeMap);


            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.OK.value()
                            , HttpStatus.OK.getReasonPhrase()
                            , instance.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                            , HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                            , e.getMessage()));
        }

    }
}
