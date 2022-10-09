package io.getmedusa.hydra.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/status")
    public Map<String, String> showStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "123");
        return status;
    }

}

