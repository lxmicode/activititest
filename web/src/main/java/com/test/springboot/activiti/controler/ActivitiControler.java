package com.test.springboot.activiti.controler;

import com.test.springboot.activiti.entity.Result;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ActivitiControler {

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

    @GetMapping("/")
    Object index() {
        repositoryService.createDeployment().deploy();

        List<Deployment> list = repositoryService.createDeploymentQuery().list();

        System.out.println(list.size());
        return new ResponseEntity<>( HttpStatus.OK);
    }



    private String tenantId = "test0001";

    /**
     * 部署一个流程
     * @return
     */
    @GetMapping("/dep")
    Object dep() {
        repositoryService.createDeployment()
                .name("第一个测试")
                .key("qingjia")
                .tenantId(tenantId)
                .addClasspathResource("bpnm/test.bpmn")
                .addClasspathResource("bpnm/test.svg")
                .deploy();

        return new ResponseEntity<>(HttpStatus.OK);
    }



    @GetMapping("/depList")
    Object depList() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
//        return new ResponseEntity<>(list, HttpStatus.OK);

        List<Map> data = new ArrayList<>();
        for (Deployment dep : list) {
            Map d = new HashMap();
            d.put("name",dep.getName());
            d.put("id",dep.getId());
            d.put("version",dep.getVersion());
            d.put("key",dep.getKey());
            d.put("tenantId",dep.getTenantId());
            data.add(d);
        }

        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        ,data));
    }

    @GetMapping("/delDepl")
    Object delDepl(String key) {
        repositoryService.deleteDeployment(key,true);
        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()));
    }

    @GetMapping("/start")
    Object start(String key) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        ,processInstance.getName()));
    }

    @GetMapping("/task")
    Object task(String tenantId) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(tenantId);
        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        ,processInstance.getName()));
    }


}
