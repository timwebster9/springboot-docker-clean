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
public class ImageService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
    private DockerJavaClient dockerJavaClient;
    private ContainerService containerService;
    private boolean dryRun;

    @Autowired
    public ImageService(final DockerJavaClient dockerJavaClient,
                        final ContainerService containerService,
                        @Value("${dryrun}") boolean dryRun) {
        this.dockerJavaClient = dockerJavaClient;
        this.containerService = containerService;
        this.dryRun = dryRun;
    }

    public List<Image> listAllImages() {
        return this.dockerJavaClient.listImages();
    }

    public List<Image> listImagesFromNonRunningContainers() {
        final List<Image> allImages = this.listAllImages();
        final List<Container> runningContainers = this.containerService.listRunningContainers();
        final List<String> imagesForRunningContainers = runningContainers.stream().map(Container::imageId).collect(Collectors.toList());
        final List<Image> result = allImages.stream().filter(image -> !imagesForRunningContainers.contains(image.id())).collect(Collectors.toList());
        return null;
    }

    public void deleteAllImages() {
        final List<Image> images = this.listAllImages();
        images.forEach(image -> deleteImage(image.id()));
    }

    private boolean notUsedInRunningContainer(final String imageId, final List<Container> runningContainers) {
        return false;
    }

    private void deleteImage(final String imageId) {
        LOG.info("Removing image: {}", imageId);
        if (!dryRun) {
            this.dockerJavaClient.deleteImage(imageId);
        }
    }
}
