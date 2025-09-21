FROM openjdk:17-jdk-slim-buster
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ENV TZ=Asia/Shanghai
ADD config/ /config/
ADD addon-coupon-1.0.jar addon-coupon-service-1.0.jar
ADD lib/ /lib/
ENTRYPOINT ["java","--add-opens=java.base/java.lang=ALL-UNNAMED","--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED","--add-opens=java.base/java.math=ALL-UNNAMED","-jar","-Xms256m","-Xmx256m" ,"addon-coupon-service-1.0.jar"]

