mvn clean package -Dmaven.test.skip=true
docker build -t saf-sample-allinone-service:1.0.0-SNAPSHOT .
