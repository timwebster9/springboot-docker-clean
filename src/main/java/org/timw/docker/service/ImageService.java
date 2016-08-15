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

    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
    private DockerJavaClient dockerJavaClient;
    private ContainerService containerService;
    private List<String> exclusions;
    private boolean dryRun;

    @Autowired
    public ImageService(final DockerJavaClient dockerJavaClient,
                        final ContainerService containerService,
                        @Value("${exclusions.images}") List<String> exclusions,
                        @Value("${dryrun}") boolean dryRun) {
        this.dockerJavaClient = dockerJavaClient;
        this.containerService = containerService;
        this.exclusions = exclusions;
        this.dryRun = dryRun;
    }

    public List<Image> listAllImages() {
        final List<Image> allImages = this.dockerJavaClient.listImages();
        LOG.info("Listing all images:");
        return allImages;

//        return allImages.stream()
//                .sorted((o1, o2) -> Long.compare(Long.parseLong(o1.created()), Long.parseLong(o2.created())))
//                .collect(Collectors.toList());
    }

    public void deleteImagesFromNonRunningContainers() {
        final List<Image> allImages = this.listAllImages();
        LOG.info("Listing all sorted images:");
        allImages.forEach(this::logImage);
        final List<String> imageIds = this.containerService.listImageIdsFromRunningContainers();
        allImages.stream()
                 .filter(image -> !imageIds.contains(image.id()))
                 .forEach(image -> this.deleteImage(image.id()));
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
