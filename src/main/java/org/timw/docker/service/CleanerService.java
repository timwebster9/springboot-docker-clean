package org.timw.docker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CleanerService {

    private ContainerService containerService;

    @Autowired
    public CleanerService(final ContainerService containerService) {
        this.containerService = containerService;
    }

    public void clean() {
        this.cleanContainers();
    }

    private void cleanContainers() {
        this.containerService.deleteNonRunningContainers();
    }
}
