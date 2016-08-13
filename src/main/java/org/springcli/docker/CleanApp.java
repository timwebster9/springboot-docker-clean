package org.springcli.docker;

import com.spotify.docker.client.messages.Image;
import java.util.List;
import org.springcli.docker.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CleanApp implements CommandLineRunner {

    @Autowired
    private ImageService imageService;

    public void run(final String ... args) throws Exception {
        final List<Image> images = this.imageService.listImages();
        images.forEach(image -> System.out.println(image.id()));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CleanApp.class, args);
    }

//    public void listImages() throws DockerException, InterruptedException {
//        final List<Image> images = DOCKER.listImages();
//        images.forEach(image -> image.repoTags().forEach(System.out::println));
//    }
//
//
//    public void clean() throws DockerException, InterruptedException {
//        final List<Image> images = DOCKER.listImages();
//        images.forEach(image -> remove(image.id()));
//    }
//
//
//    private static void remove(final String imageId) {
//        try {
//            final List<RemovedImage> removedImages = DOCKER.removeImage(imageId, true, true);
//            removedImages.forEach(removedImage -> System.out.println(removedImage.toString()));
//        }
//        catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }
}
