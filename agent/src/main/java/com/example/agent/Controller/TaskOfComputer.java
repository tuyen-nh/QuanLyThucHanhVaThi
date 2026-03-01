package com.example.agent.Controller;

import com.example.agent.Service.AgentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Component
public class TaskOfComputer {
    private final AgentService reporter;

    public TaskOfComputer(AgentService reporter) {
        this.reporter = reporter;

    }

    // @PostMapping
    // chạy lại sau mỗi 60s
    @Scheduled(fixedRate = 60000)
    public void reportStatus() {
        System.out.println("Running scheduled task...");
        reporter.sendStatus();
        reporter.getRunningApplications();
    }

}
