package ru.sbt.ds;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


public class VersionRule implements IRule {
    private final static Random RANDOM = new Random();
    private ILoadBalancer iLoadBalancer;
    private IPing ping;

    @Override
    public Server choose(Object o) {
        List<Server> reachable = new ArrayList<>(iLoadBalancer.getReachableServers());
        List<Server> filtered = getFilteredListOfServers(reachable);
        return pickAndPing(filtered)
                .orElseGet(() -> {
                    reachable.removeAll(filtered);
                    return pickAndPing(reachable).orElse(null);
                });
    }

    @Autowired
    public void setPing(IPing ping) {
        this.ping = ping;
    }

    @Override
    public void setLoadBalancer(ILoadBalancer iLoadBalancer) {
        this.iLoadBalancer = iLoadBalancer;
    }

    @Override
    public ILoadBalancer getLoadBalancer() {
        return iLoadBalancer;
    }

    private Optional<Server> pickAndPing(List<Server> servers) {
        List<Server> copy = new ArrayList<>(servers);
        try {
            while (!copy.isEmpty()) {
                Server chosen = copy.get(RANDOM.nextInt(copy.size()));
                if (ping.isAlive(chosen)) {
                    return Optional.of(chosen);
                } else {
                    copy.remove(chosen);
                }
            }
        } catch(Exception e) {}
        return Optional.empty();
    }

    private List<Server> getFilteredListOfServers(List<Server> servers) {
        if(!servers.isEmpty() && servers.iterator().next() instanceof DiscoveryEnabledServer) {
            String maxVersion = servers.stream()
                    .map(server -> (DiscoveryEnabledServer) server)
                    .map(server -> server.getInstanceInfo().getMetadata().get("version"))
                    .filter(str -> str.matches("\\d+"))
                    .map(Integer::parseInt)
                    .reduce(Integer::max)
                    .map(i -> Integer.toString(i))
                    .orElse("-1");

            return servers.stream()
                    .map(server -> (DiscoveryEnabledServer) server)
                    .filter(server -> maxVersion.equals(server.getInstanceInfo().getMetadata().get("version")))
                    .collect(Collectors.toList());
        } else {
            return servers;
        }
    }

}
