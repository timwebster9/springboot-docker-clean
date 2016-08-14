package org.timw.docker.service;

import com.google.common.collect.Lists;
import com.spotify.docker.client.messages.Container;
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
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ContainerServiceTest {

    @Mock
    private DockerJavaClient dockerJavaClient;

    @InjectMocks
    private ContainerService testSubject;

    @Test
    public void list_non_running_containers() {
        when(this.dockerJavaClient.listNonRunningContainers()).thenReturn(Lists.newArrayList());
        assertThat(this.testSubject.listNonRunningContainers()).isEmpty();
    }

    @Test
    public void delete_non_running_containers() {
        final Container container1 = mock(Container.class);
        final Container container2 = mock(Container.class);
        final List<Container> nonRunningContainers = Lists.newArrayList(container1, container2);

        when(this.dockerJavaClient.listNonRunningContainers()).thenReturn(nonRunningContainers);
        when(container1.id()).thenReturn("1");
        when(container2.id()).thenReturn("2");

        this.testSubject.deleteNonRunningContainers();

        verify(this.dockerJavaClient).deleteContainer("1");
        verify(this.dockerJavaClient).deleteContainer("2");
    }

}