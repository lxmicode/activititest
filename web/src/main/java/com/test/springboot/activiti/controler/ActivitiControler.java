package com.test.springboot.activiti.controler;

import com.test.springboot.activiti.entity.Result;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;

/**
 * 第一个简单例子
 */
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
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private String tenantId = "Lxm";


    /**
     * 流程定义
     * 1对多个部署
     * @return
     */
    @GetMapping("/proDefList")
    Object proDefList() {

        List<ProcessDefinition> list = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionTenantId(tenantId)
                .list();

        List<Map> data = new ArrayList<>();
        for (ProcessDefinition dep : list) {
            Map d = new HashMap();
            d.put("name", dep.getName());
            d.put("id", dep.getId());
            d.put("deploymentId", dep.getDeploymentId());
            d.put("version", dep.getVersion());
            d.put("key", dep.getKey());
            d.put("tenantId", dep.getTenantId());
            data.add(d);
        }

        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        , data));
    }

    /**
     * 部署一个流程
     *
     * @return
     */
    @GetMapping("/dep")
    Object dep() {
        DeploymentEntity deploy = (DeploymentEntity) repositoryService.createDeployment()
                .name("第"+(new Random().nextInt(1000))+"个测试")
//                .key("qingjia")
                .tenantId(tenantId)
                .addClasspathResource("bpnm/test2.bpmn20.xml")
                .addClasspathResource("bpnm/test2.png")
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
     * 流程部署列表
     *
     * @return
     */
    @GetMapping("/depList")
    Object depList() {
        List<Deployment> list = repositoryService
                .createDeploymentQuery().deploymentTenantId(tenantId).list();
//        return new ResponseEntity<>(list, HttpStatus.OK);


        List<Map> data = new ArrayList<>();
        Map d;
        for (Deployment dep : list) {
            d = new HashMap();
            d.put("name", dep.getName());
            d.put("id", dep.getId());
            d.put("version", dep.getVersion());
            d.put("key", dep.getKey());
            d.put("tenantId", dep.getTenantId());
            data.add(d);
        }

        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        , data));
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
            //根据业务编号确定流程
            if(StringUtils.isEmpty(bizKey))
                processInstance = runtimeService.startProcessInstanceById(id);
            else
                //启动流程中设置，busessionKey,通过businessKey来关联自己的业务表
                processInstance = runtimeService.startProcessInstanceById(id, bizKey);


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
     * 任务
     * @return
     */
    @GetMapping("/task")
    Object task() {
        List<Task> list = taskService.createTaskQuery().taskTenantId(tenantId).list();

        List<Map> data = new ArrayList<>();
        Map d;
        for (Task dep : list) {
            d = new HashMap();
            d.put("id", dep.getId());
            d.put("proInsId", dep.getProcessInstanceId());
            d.put("proDefId", dep.getProcessDefinitionId());
            d.put("name", dep.getName());
            d.put("assignee", dep.getAssignee());
            d.put("bizKey", dep.getBusinessKey());
            d.put("tenantId", dep.getTenantId());
            data.add(d);
        }

        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        , data));
    }

    /**
     * 完成任务
     */
    @GetMapping("/complete")
    Object complete(String taskId,String proInsId) {

        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceTenantId(tenantId)
                .processInstanceId(proInsId)
                .singleResult();

        //是否挂起
        if (processInstance.isSuspended()) {
            return ResponseEntity.ok(
                    new Result(
                            HttpStatus.NOT_MODIFIED.value()
                            , "任务已经挂起，暂时无法操作"));
        }

        taskService.complete(taskId);
        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        , ""));
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delDepl")
    Object delDepl(String id) {
        //true 表示级联删除
        repositoryService.deleteDeployment(id, true);
        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()));
    }

    /**
     * 暂停和挂起
     *
     * 原理：
     * 定义和实例一对多
     * 处理多个通过流程定义来，需要使用 repositoryService对象
     * 处理具体实例，需要使用 runtimeService 对象
     */
    @GetMapping("/stopOrStart")
    public Object stopOrStart(String proInsId){
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceTenantId(tenantId)
                .processInstanceId(proInsId)
                .singleResult();


        boolean suspended = processInstance.isSuspended();


        if (suspended) {
            //根据流程实例。如果已经暂时就激活(单个)
            runtimeService.activateProcessInstanceById(processInstance.getId());
            //根据流程定义ID暂时(多个)
//            repositoryService.activateProcessDefinitionById("");
        }else{
            //根据流程实例。如果已经激活就暂时(单个)
            runtimeService.suspendProcessInstanceById(processInstance.getId());
            //根据流程定义ID激活(多个)
//            repositoryService.suspendProcessDefinitionById("");
        }
        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()));
    }

    /**
     * 查看-历史-流程明细
     * @return
     */
    @GetMapping("/his")
    Object his() {
        List<HistoricActivityInstance> list = historyService
                .createHistoricActivityInstanceQuery()
                .activityTenantId(tenantId)
                .orderByProcessInstanceId()
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        List<Map> data = new ArrayList<>();
        Map d;
        for (HistoricActivityInstance his : list) {
            d = new HashMap();
            d.put("proDefId", his.getProcessDefinitionId());
            d.put("proInsId", his.getProcessInstanceId());
            d.put("actId", his.getActivityId());
            d.put("tenantId", his.getTenantId());
            d.put("name", his.getActivityName());
            d.put("assignee", his.getAssignee());
            d.put("startTime", his.getStartTime());
            d.put("endTime", his.getEndTime());
            data.add(d);
        }

        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        , data));
    }

    /**
     * 查看-历史-流程
     * @return
     */
    @GetMapping("/hisPro")
    Object hisPro() {
        List<HistoricProcessInstance> list = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId)
                .orderByProcessInstanceStartTime()
                .asc()
                .list();

        List<Map> data = new ArrayList<>();
        Map d;
        for (HistoricProcessInstance his : list) {
            d = new HashMap();
            d.put("proDefId", his.getProcessDefinitionId());
            d.put("proInsId", his.getId());
            d.put("depId", his.getDeploymentId());
            d.put("bizKey", his.getBusinessKey());
            d.put("name", his.getName());
            d.put("tenantId", his.getTenantId());
            d.put("startTime", his.getStartTime());
            d.put("endTime", his.getEndTime());
            data.add(d);
        }

        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        , data));
    }

    /**
     * 查看-历史-流程
     * @return
     */
    @GetMapping("/delHisPro")
    Object delHisPro(String proInsId) {
        historyService.deleteHistoricTaskInstance(proInsId);
        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()));
    }

    /**
     * 下载资源文件
     * @param deploymentId 部署ID
     */
    @GetMapping("/downloadRes")
    public Object downloadRes(String deploymentId) throws IOException {
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionTenantId(tenantId)
                .deploymentId(deploymentId)
                .singleResult();

        String pngName = processDefinition.getDiagramResourceName();
        String pbmnName = processDefinition.getResourceName();

        //输入流
        InputStream pngInputStream = repositoryService.getResourceAsStream(deploymentId, pbmnName);
        InputStream pbmnInputStream = repositoryService.getResourceAsStream(deploymentId, pbmnName);

        System.out.println("保存路径: "+"d:/Downloads/"+pngName);
        File pngFile = new File("d:/Downloads/"+pngName);
        File pbmnFile = new File("d:/Downloads/"+pbmnName);

        //输出流
        FileOutputStream pngOutputStream = new FileOutputStream(pngFile);
        FileOutputStream pbmnOutputStream = new FileOutputStream(pbmnFile);

        IOUtils.copy(pngInputStream,pngOutputStream);
        IOUtils.copy(pbmnInputStream,pbmnOutputStream);

        pngInputStream.close();
        pbmnInputStream.close();

        pngOutputStream.close();
        pbmnOutputStream.close();

        return ResponseEntity.ok(
                new Result(
                        HttpStatus.OK.value()
                        , HttpStatus.OK.getReasonPhrase()
                        , "操作成功"));
    }

}
