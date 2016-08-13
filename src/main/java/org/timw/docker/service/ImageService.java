package org.timw.docker.service;

import com.spotify.docker.client.messages.Image;
import org.timw.docker.DockerJavaClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageService {

    private DockerJavaClient dockerJavaClient;

    @Autowired
    public ImageService(final DockerJavaClient dockerJavaClient) {
        this.dockerJavaClient = dockerJavaClient;
    }

    public List<Image> listImages() {
        return this.dockerJavaClient.listImages();
    }

}
