package org.timw.docker.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spotify.docker.client.messages.Image;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.timw.docker.DockerJavaClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {

    @Mock
    private DockerJavaClient dockerJavaClient;

    @Mock
    private ContainerService containerService;

    private ImageService testSubject;

    @Before
    public void setup() {
        this.testSubject = new ImageService(this.dockerJavaClient, this.containerService, Lists.newArrayList(), false);
    }

    @Test
    public void listImages() throws Exception {
        final Image image1 = mock(Image.class);
        final Image image2 = mock(Image.class);
        final Image image3 = mock(Image.class);
        final List<Image> images = Lists.newArrayList(image1, image2, image3);

        when(this.dockerJavaClient.listImages()).thenReturn(images);
        assertThat(this.testSubject.listAllImages()).containsExactly(image1, image2, image3);
    }

    @Test
    public void delete_non_running() {
        final Image image1 = mock(Image.class);
        final Image image2 = mock(Image.class);
        final Image image3 = mock(Image.class);
        final List<Image> allImages = Lists.newArrayList(image1, image2, image3);

        // running - should NOT be deleted
        final String imageId1 = "1";
        final String imageId2 = "2";
        final List<String> runningImageIds = Lists.newArrayList(imageId1, imageId2);

        // not running - should be deleted
        final String imageId3 = "3";

        when(this.dockerJavaClient.listImages()).thenReturn(allImages);
        when(this.containerService.listImageIdsFromRunningContainers()).thenReturn(runningImageIds);

        when(image1.id()).thenReturn(imageId1);
        when(image2.id()).thenReturn(imageId2);
        when(image3.id()).thenReturn(imageId3);

        when(image1.repoTags()).thenReturn(ImmutableList.of("tag1"));
        when(image2.repoTags()).thenReturn(ImmutableList.of("tag2"));
        when(image3.repoTags()).thenReturn(ImmutableList.of("tag3"));

        this.testSubject.deleteImagesFromNonRunningContainers();

        // verify 2 and 3 not deleted
        verify(this.dockerJavaClient, never()).deleteImage(imageId1);
        verify(this.dockerJavaClient, never()).deleteImage(imageId2);

        // verify 3 is deleted
        verify(this.dockerJavaClient).deleteImage(imageId3);
    }

    @Test
    public void filter_images_single_tag() {
        final Image image1 = mock(Image.class);
        final Image image2 = mock(Image.class);
        final Image image3 = mock(Image.class);
        final List<Image> allImages = Lists.newArrayList(image1, image2, image3);

        when(image1.repoTags()).thenReturn(ImmutableList.of("tag1"));
        when(image2.repoTags()).thenReturn(ImmutableList.of("tag2"));
        when(image3.repoTags()).thenReturn(ImmutableList.of("tag3"));

        when(this.dockerJavaClient.listImages()).thenReturn(allImages);

        // "tag3" is an exclusion
        this.testSubject = new ImageService(this.dockerJavaClient, this.containerService, Lists.newArrayList("tag3"), false);
        final List<Image> actual = this.testSubject.filteredImages();
        assertThat(actual).containsExactly(image1, image2);
    }

    @Test
    public void filter_images_multiple_tag() {
        final Image image1 = mock(Image.class);
        final Image image2 = mock(Image.class);
        final Image image3 = mock(Image.class);
        final List<Image> allImages = Lists.newArrayList(image1, image2, image3);

        when(image1.repoTags()).thenReturn(ImmutableList.of("tag1"));
        when(image2.repoTags()).thenReturn(ImmutableList.of("tag2", "tag3"));
        when(image3.repoTags()).thenReturn(ImmutableList.of("tag3"));

        when(this.dockerJavaClient.listImages()).thenReturn(allImages);

        // "tag3" is an exclusion
        this.testSubject = new ImageService(this.dockerJavaClient, this.containerService, Lists.newArrayList("tag3"), false);
        final List<Image> actual = this.testSubject.filteredImages();
        assertThat(actual).containsExactly(image1);
    }
}