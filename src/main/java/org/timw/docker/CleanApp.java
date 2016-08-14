package org.timw.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.timw.docker.service.CleanerService;

@SpringBootApplication
public class CleanApp implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CleanApp.class);

    private CleanerService cleanerService;
    private DockerJavaClient dockerJavaClient;
    private boolean dryrun;

    @Autowired
    public CleanApp(final CleanerService cleanerService,
                    final DockerJavaClient dockerJavaClient,
                    @Value("${dryrun}") boolean dryRun) {
        this.cleanerService = cleanerService;
        this.dockerJavaClient = dockerJavaClient;
        this.dryrun = dryRun;
    }

    @Override
    public void run(final String ... args) throws Exception {
        if (this.dryrun) {
            LOG.info("Dry run is {}", dryrun ? "enabled" : "disabled");
        }

        try {
            this.cleanerService.clean();
        }
        finally {
            this.dockerJavaClient.close();
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CleanApp.class, args);
    }
}
