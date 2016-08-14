package org.timw.docker.service;

import com.google.common.collect.Lists;
import com.spotify.docker.client.messages.Image;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.timw.docker.DockerJavaClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {

    @Mock
    private DockerJavaClient dockerJavaClient;

    @InjectMocks
    private ImageService testSubject;

    @Test
    public void listImages() throws Exception {
        final Image image1 = mock(Image.class);
        final Image image2 = mock(Image.class);
        final Image image3 = mock(Image.class);
        final List<Image> images = Lists.newArrayList(image1, image2, image3);

        when(this.dockerJavaClient.listImages()).thenReturn(images);
        assertThat(this.testSubject.listImages()).containsExactly(image1, image2, image3);
    }

}