# springboot-docker-clean
## Containerised Spring Boot application that cleans Docker repositories

This application runs as a Docker container, and does the following:
* removes non-running containers
* removes images from non-running containers.  --force and 'prune' are both enabled.
* has 'dry run' feature (on by default)
* allows exclusion of containers (by name)
* allows exclusion of images (by repo:tag)

## Building

To build with Maven:
    
    mvn clean package

### Note:
* this will also tag your image using ${registry.url}, although the untagged version will still be present.
* `clean install` will attempt to push to your docker registry.  The install phase is used to avoid invoking the Maven deploy plugin.  If you really want to do this you'll have to add the `<distributionManagement/>` section to the pom.xml, or pass in the required system properties.  Also, make sure to override the ${registry.url} property in the pom.xml to something appropriate for your environment.  You can do this by passing it in as a system property. e.g. `mvn clean package -Dregistry.url=hostname:port`

## Running

To run the container, you do something like this:
    
      docker run --env dryrun=false \
                 --rm \
                 -v /var/run/docker.sock:/var/run/docker.sock \
                 -v /tmp/docker-clean:/tmp/docker-clean \
                 springboot-docker-clean

### Arguments:
* `--env dryrun=false` turns off dry run.  Perhaps try it without this first!
* `--rm` removes itself after it finishes
* `-v /var/run/docker.sock:/var/run/docker.sock` maps the Docker host UNIX socket to the container, allowing access to the host Docker instance
* `-v /tmp/docker-clean:/tmp/docker-clean` maps log file directory to the host filesystem
* `springboot-docker-clean` the name of the container to run

## Exclusions

To exclude containers or images, include the following (the identifiers are examples only):
    
      --env exclusions.containers=compassionate_pare,drunk_yalow \
      --env exclusions.images=ubuntu:latest  
    
  
### Note:
* containers are excluded by name.
* images are excluded by repo:tag (make sure you remember to include the tag!)

## Credits
Powered by: [Spring Boot](http://projects.spring.io/spring-boot/), [docker-maven-plugin](https://github.com/spotify/docker-maven-plugin), [docker-client](https://github.com/spotify/docker-client)

Tested by: [Mockito](http://mockito.org/), [AssertJ](http://joel-costigliola.github.io/assertj/)

Inspired by: [docker-gc](https://github.com/spotify/docker-gc)

