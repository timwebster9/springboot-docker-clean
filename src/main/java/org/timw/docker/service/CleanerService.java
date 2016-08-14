package org.timw.docker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CleanerService {

    private ContainerService containerService;
    private ImageService imageService;

    @Autowired
    public CleanerService(final ContainerService containerService, final ImageService imageService) {
        this.containerService = containerService;
        this.imageService = imageService;
    }

    public void clean() {
        this.containerService.deleteNonRunningContainers();
        this.imageService.deleteAllImages();
    }
}
