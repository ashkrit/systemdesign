package org.planetscale.app.archi.level3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LoadBalancer {

    private final List<String> serverUrls;

    public LoadBalancer(List<String> serverUrls) {
        this.serverUrls = serverUrls;
    }

    public String url() {
        int index = ThreadLocalRandom.current().nextInt(serverUrls.size());
        return serverUrls.get(index);
    }
}
