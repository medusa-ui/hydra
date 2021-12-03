package io.getmedusa.hydra.status.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OverviewController {

    @GetMapping("/hydra/overview")
    public String showStatus() {
        return "overview";
    }

}
