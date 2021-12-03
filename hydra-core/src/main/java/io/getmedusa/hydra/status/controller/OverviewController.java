package io.getmedusa.hydra.status.controller;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

@UIEventPage(path = "/hydra/overview", file = "pages/overview.html")
public class OverviewController {

    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        return new PageAttributes().with("name", "Maria");
    }

}
