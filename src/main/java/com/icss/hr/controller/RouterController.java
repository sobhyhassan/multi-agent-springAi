package com.icss.hr.controller;

import com.icss.hr.model.IntentResponse;
import com.icss.hr.service.RouterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RouterController {

    private final RouterService routerService;

    public RouterController(RouterService routerService) {
        this.routerService = routerService;
    }

    @GetMapping("/route")
    public IntentResponse route(@RequestParam String message) {
        return routerService.routeMessage(message);
    }
}
