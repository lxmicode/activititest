package com.test.springboot.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 任务监听器
 * 可以不主要执行处理人或者其他人物相关操作，在监听器中处理
 *
 * 提前：画图时候设置任务的监听器
 */
public class MyTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        //根据任务名和事件名判断
        if ("提交申请".equals(delegateTask.getName())
                && "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("玛丽");
        }else if ("经理审核".equals(delegateTask.getName())
                && "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("沈腾");
        }

    }
}
