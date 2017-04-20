package org.timw.docker.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spotify.docker.client.messages.Container;
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
public class ContainerServiceTest {

    @Mock
    private DockerJavaClient dockerJavaClient;

    private ContainerService testSubject;

    @Before
    public void setup() {
        this.testSubject = new ContainerService(this.dockerJavaClient, Lists.newArrayList(), false);
    }

    @Test
    public void list_non_running_containers() {
        when(this.dockerJavaClient.listNonRunningContainers()).thenReturn(Lists.newArrayList());
        assertThat(this.testSubject.listNonRunningContainers()).isEmpty();
    }

    @Test
    public void list_running_containers() {
        when(this.dockerJavaClient.listRunningContainers()).thenReturn(Lists.newArrayList());
        assertThat(this.testSubject.listRunningContainers()).isEmpty();
    }

    @Test
    public void list_image_ids_from_running_containers() {
        final Container container1 = mock(Container.class);
        final Container container2 = mock(Container.class);

        final String imageId1 = "image1";
        final String imageId2 = "image2";

        when(container1.imageId()).thenReturn(imageId1);
        when(container2.imageId()).thenReturn(imageId2);
        when(this.dockerJavaClient.listRunningContainers())
                .thenReturn(Lists.newArrayList(container1, container2));

        final List<String> actual = this.testSubject.listImageIdsFromRunningContainers();
        assertThat(actual).containsExactly(imageId1, imageId2);
    }

    @Test
    public void delete_non_running_containers() {
        final String containerId1 = "1";
        final String containerId2 = "2";
        final String containerName1 = "name1";
        final String containerName2 = "name2";
        final Container container1 = mock(Container.class);
        final Container container2 = mock(Container.class);
        final List<Container> nonRunningContainers = Lists.newArrayList(container1, container2);

        when(this.dockerJavaClient.listNonRunningContainers()).thenReturn(nonRunningContainers);
        when(container1.id()).thenReturn(containerId1);
        when(container2.id()).thenReturn(containerId2);
        when(container1.names()).thenReturn(ImmutableList.of(containerName1));
        when(container2.names()).thenReturn(ImmutableList.of(containerName2));

        this.testSubject.deleteNonRunningContainers();

        verify(this.dockerJavaClient).deleteContainer(containerId1);
        verify(this.dockerJavaClient).deleteContainer(containerId2);
    }

    @Test
    public void delete_non_running_containers_with_exclusions() {
        final String containerId1 = "1";
        final String containerId2 = "2";
        final String containerId3 = "3";

        final String containerName1 = "/name1";
        final String containerName2 = "/name2";
        final String containerName3 = "/name3";

        final Container container1 = mock(Container.class);
        final Container container2 = mock(Container.class);
        final Container container3 = mock(Container.class);
        final List<Container> nonRunningContainers = Lists.newArrayList(container1, container2, container3);

        when(this.dockerJavaClient.listNonRunningContainers()).thenReturn(nonRunningContainers);
        when(container2.id()).thenReturn(containerId2);

        when(container1.names()).thenReturn(ImmutableList.of(containerName1));
        when(container2.names()).thenReturn(ImmutableList.of(containerName2));
        when(container3.names()).thenReturn(ImmutableList.of(containerName3));

        this.testSubject = new ContainerService(this.dockerJavaClient, Lists.newArrayList("name1","name3"), false);
        this.testSubject.deleteNonRunningContainers();

        verify(this.dockerJavaClient, never()).deleteContainer(containerId1);
        verify(this.dockerJavaClient, never()).deleteContainer(containerId3);
        verify(this.dockerJavaClient, never()).deleteContainer(null);
        verify(this.dockerJavaClient).deleteContainer(containerId2);
    }

    @Test
    public void delete_non_running_containers_dryrun() {
        this.testSubject = new ContainerService(this.dockerJavaClient, Lists.newArrayList(), true);

        final Container container1 = mock(Container.class);
        final Container container2 = mock(Container.class);
        final List<Container> nonRunningContainers = Lists.newArrayList(container1, container2);

        final String containerName1 = "/name1";
        final String containerName2 = "/name2";

        when(this.dockerJavaClient.listNonRunningContainers()).thenReturn(nonRunningContainers);
        when(container1.id()).thenReturn("1");
        when(container2.id()).thenReturn("2");

        when(container1.names()).thenReturn(ImmutableList.of(containerName1));
        when(container2.names()).thenReturn(ImmutableList.of(containerName2));

        this.testSubject.deleteNonRunningContainers();

        verify(this.dockerJavaClient, never()).deleteContainer("1");
        verify(this.dockerJavaClient, never()).deleteContainer("2");
    }

}