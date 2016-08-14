FROM centos:7

MAINTAINER Tim Webster <tim.webster@gmail.com>

COPY docker.repo /etc/yum.repos.d

RUN yum -y update \
 && yum -y install java-1.8.0-openjdk docker-engine \
 && yum -y clean all

COPY @project.build.finalName@.jar /

ENTRYPOINT ["java", "-jar", "/@project.build.finalName@.jar"]