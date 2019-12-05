FROM hpy253215039/oraclejdk-linux-64:8u221

ADD target/saf-sample-allinone-service-1.0.1-SNAPSHOT.jar /app/inc/apps/

RUN chown -R inc:inc /app/inc/ /data/inc/ /opt/ && mkdir -p /data/inc/logs/saf-sample-allinone-service

COPY run.sh /app/inc/apps/
WORKDIR /app/inc/apps

#web端口
EXPOSE 8080
#prometheus端口
EXPOSE 9145
#passport-rpc端口
EXPOSE 10010

ENTRYPOINT /bin/bash run.sh start && tail -f /dev/null
