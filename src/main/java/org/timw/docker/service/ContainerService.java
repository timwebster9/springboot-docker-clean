package org.timw.docker.service;

import com.spotify.docker.client.messages.Container;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.timw.docker.DockerJavaClient;

@Component
class ContainerService {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerService.class);
    private DockerJavaClient dockerJavaClient;
    private List<String> exclusions;
    private boolean dryRun;

    @Autowired
    ContainerService(final DockerJavaClient dockerJavaClient,
                     @Value("${exclusions.containers}") List<String> exclusions,
                     @Value("${dryrun}") boolean dryRun) {
        this.dockerJavaClient = dockerJavaClient;
        this.exclusions = exclusions;
        this.dryRun = dryRun;
    }

    List<Container> listNonRunningContainers() {
        return this.dockerJavaClient.listNonRunningContainers();
    }

    List<Container> listRunningContainers() {
        return this.dockerJavaClient.listRunningContainers();
    }

    List<String> listImageIdsFromRunningContainers() {
        final List<Container> runningContainers = this.listRunningContainers();
        return runningContainers.stream()
                .map(Container::imageId)
                .collect(Collectors.toList());
    }

    void deleteNonRunningContainers() {
        final List<Container> nonRunningContainers = this.listNonRunningContainers();
        LOG.info("Container exclusions are: {}", this.exclusions);
        nonRunningContainers.stream()
                .filter(container -> container.names().stream()
                        .map(name -> name.substring(1))  // strip "/" prefix
                        .noneMatch(this.exclusions::contains))
                .forEach(container -> deleteContainer(container.id()));
    }

    private void deleteContainer(final String containerId) {
        LOG.info("Removing container: {}", containerId);
        if (!dryRun) {
            this.dockerJavaClient.deleteContainer(containerId);
        }
    }

}
