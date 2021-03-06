package io.getmedusa.hydra.discovery.model;

import java.util.Objects;

public class MenuItem {
    private String endpoint;
    private String label;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        MenuItem menuItem = (MenuItem) o;
        return getEndpoint().equals(menuItem.getEndpoint()) && Objects.equals(getLabel(), menuItem.getLabel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEndpoint(), getLabel());
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "endpoint='" + endpoint + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
