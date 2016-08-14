package org.timw.docker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.timw.docker.service.CleanerService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CleanAppTest {

    @Mock
    private CleanerService cleanerService;

    @Mock
    private DockerJavaClient dockerJavaClient;

    private CleanApp testSubject;

    @Before
    public void setup() {
        this.testSubject = new CleanApp(this.cleanerService, this.dockerJavaClient, false);
    }

    @Test
    public void run() throws Exception {
        this.testSubject.run();
        verify(this.cleanerService).clean();
        verify(this.dockerJavaClient).close();
    }

}