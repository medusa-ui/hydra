package io.getmedusa.hydra.security.controller.meta;

public record LoginForm(String username, String password, String csrf) {
}
