package io.getmedusa.hydra.controller;

import io.getmedusa.hydra.model.ActiveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceController {

    @GetMapping("/services")
    public List<ActiveService> getServices() {
        return new ArrayList<>();
    }

    @PostMapping("/services/register")
    public List<ActiveService> registerService(@RequestBody ActiveService activeService) {
        System.out.println("services/register - " + activeService.getServiceName());
        return getServices();
    }

}
