package org.timw.docker.service;

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
class ImageService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
    private DockerJavaClient dockerJavaClient;
    private ContainerService containerService;
    private List<String> exclusions;
    private boolean dryRun;

    @Autowired
    ImageService(final DockerJavaClient dockerJavaClient,
                        final ContainerService containerService,
                        @Value("{'${exclusions.images}'.split(',')}") List<String> exclusions,
                        @Value("${dryrun}") boolean dryRun) {
        this.dockerJavaClient = dockerJavaClient;
        this.containerService = containerService;
        this.exclusions = exclusions;
        this.dryRun = dryRun;
    }

    List<Image> listAllImages() {
        final List<Image> allImages = this.dockerJavaClient.listImages();
        LOG.info("Listing all images:");
        LOG.info("Listing all sorted images:");
        allImages.forEach(this::logImage);
        return allImages;
    }

    void deleteImagesFromNonRunningContainers() {
        final List<Image> filteredImages = this.filteredImages();
        final List<String> imageIds = this.containerService.listImageIdsFromRunningContainers();
        filteredImages.stream()
                 .filter(image -> !imageIds.contains(image.id()))
                 .forEach(image -> this.deleteImage(image.id()));
    }

    List<Image> filteredImages() {
        final List<Image> allImages = this.listAllImages();
        System.out.println("Exclusions: ");
        this.exclusions.stream().forEach(System.out::println);
        return allImages.stream()
                     .filter(image -> image.repoTags().stream()
                             .noneMatch(this.exclusions::contains))
                .collect(Collectors.toList());
    }

    private void logImage(final Image image) {
        LOG.info("Image ID: {}, Created: {}", image.id(), image.created());
    }

    private void deleteImage(final String imageId) {
        LOG.info("Removing image: {}", imageId);
        if (!dryRun) {
            this.dockerJavaClient.deleteImage(imageId);
        }
    }
}
