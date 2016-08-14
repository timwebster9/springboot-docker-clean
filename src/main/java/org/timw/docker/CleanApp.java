package org.timw.docker;

import com.spotify.docker.client.messages.Image;
import java.util.List;
import org.timw.docker.service.CleanerService;
import org.timw.docker.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CleanApp implements CommandLineRunner {

    @Autowired
    private CleanerService cleanerService;

    public void run(final String ... args) throws Exception {
        this.cleanerService.clean();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CleanApp.class, args);
    }
}
