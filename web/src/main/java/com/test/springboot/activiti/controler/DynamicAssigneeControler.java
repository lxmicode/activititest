package com.test.springboot.activiti.controler;

import com.test.springboot.activiti.entity.Result;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 动态负责人(Assignee)
 */
@RestController
@RequestMapping("/dynAssignee/")
public class DynamicAssigneeControler {

    @Autowired
    ProcessEngine engine;
    //资源管理类
    @Autowired
    RepositoryService repositoryService;
    //任务管理类
    @Autowired
    TaskService taskService;
    //流行运行管理
    @Autowired
    RuntimeService runtimeService;
    //历史管理
    @Autowired
    HistoryService historyService;
    //引擎管理
    @Autowired
    ManagementService managementService;


    private String tenantId = "Lxm";


    /**
     * 部署一个流程,uel表达式
     *
     * @return
     */
    @GetMapping("/dep")
    Object dep() {
        DeploymentEntity deploy = (DeploymentEntity) repositoryService.createDeployment()
                .name("测试-uel流程")
//                .key("qingjia")
                .tenantId(tenantId)
                .addClasspathResource("bpnm/leave.bpmn")
                .addClasspathResource("bpnm/leave.png")
                .enableDuplicateFiltering()
                .deploy();

        if (deploy.isNew() == false) {
            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.CREATED.value()
                            , HttpStatus.CREATED.getReasonPhrase()
                            , "已经存在了，请勿重复添加！"));
        }

        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        , "部署成功！"));
    }


    /**
     * 启动流程
     * @param id
     * @return
     */
    @GetMapping("/start")
    Object start(String id,String bizKey) {
        try {

            ProcessInstance processInstance = null;

            //map key必须与BPMN文件的assignee字段uel表达式保持一致
            Map<String, Object> assigneeMap = new HashMap<>();
            assigneeMap.put("user1","岳云鹏");
            assigneeMap.put("user2","郭德纲");
            //根据流程实例编号查询
            if(StringUtils.isEmpty(bizKey))
                processInstance = runtimeService.startProcessInstanceById(id,assigneeMap);
            else
                //启动流程中设置，busessionKey,通过businessKey来关联自己的业务表
                processInstance = runtimeService.startProcessInstanceById(id, bizKey,assigneeMap);


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



}
