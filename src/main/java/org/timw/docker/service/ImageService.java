package org.timw.docker.service;

import com.spotify.docker.client.messages.Image;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.timw.docker.DockerJavaClient;

@Component
public class ImageService {

    private static Logger LOG = LoggerFactory.getLogger(ImageService.class);
    private DockerJavaClient dockerJavaClient;
    private boolean dryRun;

    @Autowired
    public ImageService(final DockerJavaClient dockerJavaClient,
                        @Value("${dryrun}") boolean dryRun) {
        this.dockerJavaClient = dockerJavaClient;
        this.dryRun = dryRun;
    }

    public List<Image> listImages() {
        return this.dockerJavaClient.listImages();
    }

    public void deleteAllImages() {
        final List<Image> images = this.listImages();
        images.forEach(image -> deleteImage(image.id()));
    }

    private void deleteImage(final String imageId) {
        LOG.info("Removing image: {}", imageId);
        if (!dryRun) {
            this.dockerJavaClient.deleteImage(imageId);
        }
    }
}
