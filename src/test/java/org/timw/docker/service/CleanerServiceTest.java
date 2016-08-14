package org.timw.docker.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CleanerServiceTest {

    @Mock
    private ContainerService containerService;

    @InjectMocks
    private CleanerService testSubject;

    @Test
    public void clean() throws Exception {
        this.testSubject.clean();
        verify(this.containerService).deleteNonRunningContainers();
    }

}