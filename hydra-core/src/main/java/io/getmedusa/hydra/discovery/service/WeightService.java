package io.getmedusa.hydra.discovery.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeightService {

    private Map<String, Integer> weights = new HashMap<>();

    public void addWeight(String uri, int weight) {
        weights.put(uri, weight);
    }

    public int getWeight(String uri) {
        final Integer weight = weights.get(uri);
        if(weight == null) return 10;
        return weight;
    }
}
