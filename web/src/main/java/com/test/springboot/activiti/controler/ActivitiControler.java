package com.test.springboot.activiti.controler;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ActivitiControler {


    @Autowired
    RepositoryService repositoryService;
    @Autowired
    TaskService taskService;

    @GetMapping("/")
    @ResponseBody
    Object index() {

        repositoryService.createDeployment().deploy();

        List<Deployment> list = repositoryService.createDeploymentQuery().list();

        System.out.println(list.size());
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @GetMapping("/list")
    @ResponseBody
    Object list() {
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("")
                .list();
        for (Task task : list) {
            System.out.printf(task.getName());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
