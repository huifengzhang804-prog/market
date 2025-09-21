FROM openjdk:17-jdk-slim-buster
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ENV TZ=Asia/Shanghai
ADD config/ /config/
ADD lib/ /lib/
ADD gruul-mall-overview-service-1.0.jar gruul-mall-overview-service-1.0.jar
ENTRYPOINT ["java","-jar", "--add-opens=java.base/java.lang=ALL-UNNAMED","--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED","--add-opens=java.base/java.math=ALL-UNNAMED","-Xms256m","-Xmx256m" ,"gruul-mall-overview-service-1.0.jar"]
