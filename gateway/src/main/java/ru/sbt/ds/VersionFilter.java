package ru.sbt.ds;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.apache.RibbonApacheHttpResponse;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VersionFilter extends ZuulFilter {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances((String)ctx.get("serviceId"));
        String maxVersion = serviceInstances.stream()
                .map(serviceInstance -> serviceInstance.getMetadata().get("version"))
                .filter(str -> str.matches("\\d+"))
                .map(Integer::parseInt)
                .reduce(Integer::max)
                .map(i -> Integer.toString(i))
                .orElse("-1");

        Set<String> maxVersionInstances = serviceInstances.stream()
                .filter(serviceInstance -> maxVersion.equals(serviceInstance.getMetadata().get("version")))
                .map(ServiceInstance::getUri)
                .map(this::getHostPort)
                .collect(Collectors.toSet());

        if(ctx.containsKey("ribbonResponse")) {
            RibbonApacheHttpResponse response = (RibbonApacheHttpResponse) ctx.get("ribbonResponse");
            return !maxVersionInstances.contains(getHostPort(response.getRequestedURI()));
        }
        return false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances((String)ctx.get("serviceId"));
        String maxVersion = serviceInstances.stream()
                .map(serviceInstance -> serviceInstance.getMetadata().get("version"))
                .filter(str -> str.matches("\\d+"))
                .map(Integer::parseInt)
                .reduce(Integer::max)
                .map(i -> Integer.toString(i))
                .orElse("-1");

        Set<String> maxVersionInstances = serviceInstances.stream()
                .filter(serviceInstance -> maxVersion.equals(serviceInstance.getMetadata().get("version")))
                .map(ServiceInstance::getUri)
                .map(this::getHostPort)
                .collect(Collectors.toSet());

        if(ctx.containsKey("ribbonResponse")) {
            RibbonApacheHttpResponse response = (RibbonApacheHttpResponse) ctx.get("ribbonResponse");
            if(!maxVersionInstances.contains(getHostPort(response.getRequestedURI()))) {
                throw new RuntimeException();
            }
        }
        return null;
    }

    private String getHostPort(URI uri) {
        return getHostPort(uri.getHost(), uri.getPort());
    }

    private String getHostPort(String host, int port) {
        return host + (StringUtils.isEmpty(port) ? "" : (":" + port));
    }
}
