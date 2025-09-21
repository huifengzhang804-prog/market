FROM openjdk:17-jdk-slim-buster
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ENV TZ=Asia/Shanghai
ADD config/ /config/
ADD gruul-mall-afs-service-1.0.jar gruul-mall-afs-service-1.0.jar
ADD lib/ /lib/
ENTRYPOINT ["java","-jar", "--add-opens=java.base/java.lang=ALL-UNNAMED","--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED","--add-opens=java.base/java.math=ALL-UNNAMED","-Xms256m","-Xmx256m" ,"gruul-mall-afs-service-1.0.jar"]
