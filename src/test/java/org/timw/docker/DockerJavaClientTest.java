package org.timw.docker;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.Image;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DockerJavaClientTest {

    @Mock
    private DockerClient dockerClient;

    @Spy
    private DockerJavaClient testSubject;

    @Test
    public void list_images() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        when(dockerClient.listImages()).thenReturn(new ArrayList<>());

        final List<Image> images = testSubject.listImages();
        assertThat(images).isEmpty();
    }

    @Test(expected = CleanerException.class)
    public void list_images_throws_exception() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        when(dockerClient.listImages()).thenThrow(new DockerException("some exception"));

        testSubject.listImages();
    }

    @Test
    public void list_all_containers() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        when(dockerClient.listContainers(any(DockerClient.ListContainersParam.class))).thenReturn(new ArrayList<>());

        final List<Container> containers = testSubject.listAllContainers();
        assertThat(containers).isEmpty();
    }

    @Test(expected = CleanerException.class)
    public void list_all_containers_throws_exception() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        when(dockerClient.listContainers(any(DockerClient.ListContainersParam.class))).thenThrow(new DockerException("some exception"));

        testSubject.listAllContainers();
    }

    @Test
    public void list_non_running_containers() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        when(dockerClient.listContainers(any())).thenReturn(new ArrayList<>());

        final List<Container> containers = testSubject.listNonRunningContainers();
        assertThat(containers).isEmpty();
    }

    @Test
    public void list_running_containers() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        when(dockerClient.listContainers(any())).thenReturn(new ArrayList<>());

        final List<Container> containers = testSubject.listRunningContainers();
        assertThat(containers).isEmpty();
    }

    @Test(expected = CleanerException.class)
    public void list_running_containers_throws_exception() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        when(dockerClient.listContainers(any())).thenThrow(new DockerException("some exception"));

        testSubject.listRunningContainers();
    }

    @Test(expected = CleanerException.class)
    public void list_non_running_containers_throws_exception() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        when(dockerClient.listContainers(any())).thenThrow(new DockerException("some exception"));

        testSubject.listNonRunningContainers();
    }

    @Test
    public void delete_container() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();

        final String containerId = "1234";
        testSubject.deleteContainer(containerId);
        verify(dockerClient).removeContainer(containerId);
    }

    @Test(expected = CleanerException.class)
    public void delete_container_throws_exception() throws Exception {
        final String containerId = "1234";
        doReturn(dockerClient).when(testSubject).getClient();
        doThrow(new DockerException("some exception")).when(dockerClient).removeContainer(containerId);

        testSubject.deleteContainer(containerId);
    }

    @Test
    public void delete_image() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();

        final String imageId = "1234";
        testSubject.deleteImage(imageId);
        verify(dockerClient).removeImage(imageId, true, false);
    }

    @Test
    public void delete_image_swallows_exception() throws Exception {
        doReturn(dockerClient).when(testSubject).getClient();
        final String imageId = "1234";
        doThrow(new DockerException("some exception")).when(dockerClient).removeImage(imageId, true, false);

        this.testSubject.deleteImage(imageId);
    }

    @Test
    public void close() {
        Whitebox.setInternalState(this.testSubject, "dockerClient", this.dockerClient);
        this.testSubject.close();
        verify(this.dockerClient).close();
    }

    @Test
    public void close_when_docker_client_null() {
        this.testSubject.close();
        verifyZeroInteractions(this.dockerClient);
    }
}