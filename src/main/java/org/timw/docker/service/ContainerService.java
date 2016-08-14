package org.timw.docker.service;

import com.spotify.docker.client.messages.Container;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.timw.docker.DockerJavaClient;

@Component
public class ContainerService {

    private DockerJavaClient dockerJavaClient;

    @Autowired
    public ContainerService(final DockerJavaClient dockerJavaClient) {
        this.dockerJavaClient = dockerJavaClient;
    }

    public List<Container> listNonRunningContainers() {
        return this.dockerJavaClient.listNonRunningContainers();
    }

    public void deleteNonRunningContainers() {
        final List<Container> nonRunningContainers = this.listNonRunningContainers();
        nonRunningContainers.forEach(container -> deleteContainer(container.id()));
    }

    private void deleteContainer(final String containerId) {
        this.dockerJavaClient.deleteContainer(containerId);
    }

}
