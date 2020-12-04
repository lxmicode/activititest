package com.test.springboot.activiti.controler;

import com.test.springboot.activiti.entity.Result;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/dep")
    Object dep() {

        repositoryService.createDeployment()
                .name("第一个测试")
                .tenantId("test0001")
                .addClasspathResource("bpnm/test.bpmn")
                .addClasspathResource("bpnm/test.svg")
                .deploy();

        return new ResponseEntity<>(HttpStatus.OK);
    }



    @GetMapping("/depList")
    Object depList() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
//        return new ResponseEntity<>(list, HttpStatus.OK);

        for (Deployment dep : list) {
            System.out.println("=====================");
            System.out.println(dep.getName());
            System.out.println(dep.getKey());
            System.out.println(dep.getTenantId());
        }

        return ResponseEntity.ok(new Result(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),"222"));
    }
}
