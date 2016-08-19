package org.timw.docker.service;

import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.messages.Image;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.timw.docker.DockerJavaClient;

import static java.util.Optional.ofNullable;

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
                        @Value("${exclusions.images}") List<String> exclusions,
                        @Value("${dryrun}") boolean dryRun) {
        this.dockerJavaClient = dockerJavaClient;
        this.containerService = containerService;
        this.exclusions = exclusions;
        this.dryRun = dryRun;
    }

    List<Image> listAllImages() {
        return this.dockerJavaClient.listImages();
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
        LOG.info("Images exclusions are: {}", this.exclusions);
        return allImages.stream()
           .filter(image -> ofNullable(image.repoTags()).orElse(ImmutableList.of()).stream()
               .noneMatch(this.exclusions::contains))
           .collect(Collectors.toList());
    }

    private void deleteImage(final String imageId) {
        LOG.info("Removing image: {}", imageId);
        if (!dryRun) {
            this.dockerJavaClient.deleteImage(imageId);
        }
    }
}
