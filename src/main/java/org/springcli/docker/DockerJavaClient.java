package org.springcli.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Image;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DockerJavaClient {

    private DockerClient dockerClient;

    public DockerJavaClient() {
        try {
            this.dockerClient = DefaultDockerClient.fromEnv().build();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Image> listImages() {
        try {
            return this.dockerClient.listImages();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
