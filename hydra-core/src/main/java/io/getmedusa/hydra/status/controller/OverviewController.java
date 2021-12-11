package io.getmedusa.hydra.status.controller;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;

@UIEventPage(path = "/_hydra/overview", file = "pages/overview.html")
public class OverviewController {

    public PageAttributes setupAttributes() {
        return new PageAttributes();
    }

}
