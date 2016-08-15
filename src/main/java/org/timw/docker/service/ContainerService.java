package org.timw.docker.service;

import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.Image;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.timw.docker.DockerJavaClient;

@Component
public class ContainerService {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerService.class);
    private DockerJavaClient dockerJavaClient;
    private boolean dryRun;

    @Autowired
    public ContainerService(final DockerJavaClient dockerJavaClient, @Value("${dryrun}") boolean dryRun) {
        this.dockerJavaClient = dockerJavaClient;
        this.dryRun = dryRun;
    }

    public List<Container> listNonRunningContainers() {
        return this.dockerJavaClient.listNonRunningContainers();
    }

    public List<Container> listRunningContainers() {
        return this.dockerJavaClient.listRunningContainers();
    }

    public List<String> listImageIdsFromRunningContainers() {
        final List<Container> runningContainers = this.listRunningContainers();
        return runningContainers.stream().map(Container::imageId).collect(Collectors.toList());
    }

    public void deleteNonRunningContainers() {
        final List<Container> nonRunningContainers = this.listNonRunningContainers();
        nonRunningContainers.forEach(container -> deleteContainer(container.id()));
    }

    private void deleteContainer(final String containerId) {
        LOG.info("Removing container: {}", containerId);
        if (!dryRun) {
            this.dockerJavaClient.deleteContainer(containerId);
        }
    }

}
