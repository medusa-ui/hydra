package com.example.sampleb.eventhandler;

import io.getmedusa.medusa.core.annotation.HydraMenu;
import io.getmedusa.medusa.core.annotation.UIEventPage;

@UIEventPage(path = "/sample-b", file = "pages/hello-world.html")
@HydraMenu(value = "top-menu", label = "Sample-B")
public class HelloWorldEventHandler {

}