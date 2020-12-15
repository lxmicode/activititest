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
 * 任务
 * 1.组受理任务
 * 2.归还任务
 * 3.交接任务
 */
@RestController
@RequestMapping("/task/")
public class TaskController {

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
     * 启动组任务
     * @param id
     * @return
     */
    @GetMapping("/start")
    Object start(String id) {
        ActivitiControler activitiControler = new ActivitiControler();
        return activitiControler.start(id, null);
    }

    //部署


    /**
     * 受理任务
     * @param taskId
     * @return
     */
    @GetMapping("/claim")
    Object claim(String taskId,String userId) {
        try {

            Task task = taskService
                    .createTaskQuery().taskTenantId(tenantId)
                    .taskId(taskId)
                    .singleResult();
            taskService.claim(task.getId(),userId);
            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.OK.value()
                            , userId+" 受理任务完成："+task.getName()));
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
     * 归还任务
     * @param taskId
     * @return
     */
    @GetMapping("/assigneeToGourp")
    Object assigneeToGourp(String taskId,String userId) {
        try {
            Task task = taskService
                    .createTaskQuery().taskTenantId(tenantId)
                    .taskId(taskId)
                    .singleResult();
            taskService.setAssignee(task.getId(),null);
            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.OK.value()
                            , userId+" 归还任务完成："+task.getName()));
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
     * 修改负责人
     * @param taskId 任务Id
     * @param userId 当前负责人
     * @param newUserId 新的负责人
     * @return
     */
    @GetMapping("/assigneeChange")
    Object assigneeChange(String taskId,String userId,String newUserId) {
        try {
            Task task = taskService
                    .createTaskQuery().taskTenantId(tenantId)
                    .taskId(taskId)
                    .taskAssignee(userId)
                    .singleResult();
            taskService.setAssignee(task.getId(),newUserId);
            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.OK.value()
                            , userId+" 归还任务完成："+task.getName()));
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
