FROM ubuntu:18.04
LABEL maintainer="user"
RUN apt-get update \
    && apt-get install -y apache2
RUN echo "TEST WEB" > /var/www/html/index.html
EXPOSE 80
CMD [ "/usr/sbin/apache2ctl", "-DFOREGROUND"]