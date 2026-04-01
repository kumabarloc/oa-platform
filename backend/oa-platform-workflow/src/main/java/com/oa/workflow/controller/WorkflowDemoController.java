package com.oa.workflow.controller;

import com.oa.common.core.result.R;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flowable 引擎最小可用演示接口：
 * - 部署 classpath:processes 下的示例流程
 * - 启动流程实例
 * - 查询待办任务
 */
@RestController
@RequestMapping("/api/workflow/demo")
public class WorkflowDemoController {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public WorkflowDemoController(RepositoryService repositoryService, RuntimeService runtimeService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @PostMapping("/deploy")
    public R<?> deploy() {
        var deployment = repositoryService.createDeployment()
                .name("oa-demo-deployment")
                .addClasspathResource("processes/leave-request.bpmn20.xml")
                .deploy();
        return R.ok(Map.of("deploymentId", deployment.getId(), "name", deployment.getName()));
    }

    @PostMapping("/start")
    public R<?> start(@RequestParam(defaultValue = "admin") String applicant) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("applicant", applicant);
        var instance = runtimeService.startProcessInstanceByKey("leaveRequest", vars);
        return R.ok(Map.of(
                "processInstanceId", instance.getId(),
                "processDefinitionId", instance.getProcessDefinitionId()
        ));
    }

    @GetMapping("/tasks")
    public R<?> tasks(@RequestParam(required = false) String assignee,
                      @RequestParam(required = false) String candidateGroup) {
        var query = taskService.createTaskQuery().active().orderByTaskCreateTime().desc();
        if (assignee != null && !assignee.isBlank()) {
            query = query.taskAssignee(assignee);
        }
        if (candidateGroup != null && !candidateGroup.isBlank()) {
            query = query.taskCandidateGroup(candidateGroup);
        }

        List<Task> tasks = query.listPage(0, 50);
        List<Map<String, Object>> data = tasks.stream().map(t -> Map.<String, Object>of(
                "id", t.getId(),
                "name", t.getName(),
                "assignee", t.getAssignee(),
                "processInstanceId", t.getProcessInstanceId(),
                "processDefinitionId", t.getProcessDefinitionId(),
                "createTime", t.getCreateTime()
        )).toList();

        return R.ok(data);
    }
}

