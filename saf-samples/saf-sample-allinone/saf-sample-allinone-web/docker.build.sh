mvn clean package -Dmaven.test.skip=true
docker build -t saf-sample-allinone-web:1.0.1-SNAPSHOT .
