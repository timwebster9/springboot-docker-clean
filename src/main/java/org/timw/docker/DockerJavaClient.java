package org.timw.docker;

import com.google.common.annotations.VisibleForTesting;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.Image;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.spotify.docker.client.DockerClient.ListContainersParam.allContainers;
import static com.spotify.docker.client.DockerClient.ListContainersParam.withStatusCreated;
import static com.spotify.docker.client.DockerClient.ListContainersParam.withStatusExited;
import static com.spotify.docker.client.DockerClient.ListContainersParam.withStatusRunning;

@Component
public class DockerJavaClient {

    private static final Logger LOG = LoggerFactory.getLogger(DockerJavaClient.class);
    private DockerClient dockerClient;

    public List<Image> listImages() {
        try {
            return this.getClient().listImages();
        }
        catch (final Exception e) {
            throw new CleanerException(e);
        }
    }

    public List<Container> listAllContainers() {
        try {
            return this.getClient().listContainers(allContainers());
        }
        catch (final Exception e) {
            throw new CleanerException(e);
        }
    }

    public List<Container> listNonRunningContainers() {
        try {
            return this.getClient().listContainers(withStatusExited(), withStatusCreated());
        }
        catch (final Exception e) {
            throw new CleanerException(e);
        }
    }

    public List<Container> listRunningContainers() {
        try {
            return this.getClient().listContainers(withStatusRunning());
        }
        catch (final Exception e) {
            throw new CleanerException(e);
        }
    }

    public void deleteContainer(final String containerId) {
        try {
            this.getClient().removeContainer(containerId);
        } catch (final Exception e) {
            throw new CleanerException(e);
        }
    }

    public void deleteImage(final String imageId) {
        try {
            this.getClient().removeImage(imageId, true, true);
        } catch (final Exception e) {
            throw new CleanerException(e);
        }
    }

    public void close() {
        if (this.dockerClient != null) {
            LOG.info("Shutting down Docker client.");
            this.dockerClient.close();
        }
    }

    // annoying static...
    @VisibleForTesting
    DockerClient getClient() {
        if (this.dockerClient == null) {
            try {
                this.dockerClient = DefaultDockerClient.fromEnv().build();
            } catch (final Exception e) {
                throw new CleanerException(e);
            }
        }
        return this.dockerClient;
    }

}
