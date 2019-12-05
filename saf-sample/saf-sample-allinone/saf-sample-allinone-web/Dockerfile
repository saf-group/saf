FROM hpy253215039/oraclejdk-linux-64:8u221

ADD target/saf-sample-allinone-web-1.0.1-SNAPSHOT.jar /app/inc/apps/

RUN chown -R inc:inc /app/inc/ /data/inc/ /opt/ && mkdir -p /data/inc/logs/saf-sample-allinone-web

COPY run.sh /app/inc/apps/
WORKDIR /app/inc/apps

EXPOSE 8080
EXPOSE 9145

ENTRYPOINT /bin/bash run.sh start && tail -f /dev/null
