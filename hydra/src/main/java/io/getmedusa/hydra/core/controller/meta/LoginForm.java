package io.getmedusa.hydra.core.controller.meta;

public record LoginForm(String username, String password, String csrf) {
}
