package com.test.springboot.activiti.controler;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网关例子，控制层
 *
 * 排它网关特点，结合连线上的条件，必须有一个为true，如果都不成立抛出异常。
 *
 * 与连线條件区别：
 *      连线如果2个都为false直接结算流程
 *
 *  与连线條件共同点:
 *      当多个为true，执行ID值小的
 */
@RestController
@RequestMapping("/dynAssignee/")
public class GateWayController {

    //资源管理类
    @Autowired
    RepositoryService repositoryService;
    //任务管理类
    @Autowired
    TaskService taskService;


}
