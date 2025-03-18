# Bước 1: Sử dụng image Java làm base image
FROM openjdk:17-jdk-slim AS build

# Bước 2: Cài đặt Maven
RUN apt-get update && apt-get install -y maven

# Bước 3: Đặt thư mục làm việc
WORKDIR /app

# Bước 4: Sao chép file pom.xml và tải các dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Bước 5: Sao chép mã nguồn
COPY src ./src

# Bước 6: Xây dựng ứng dụng Spring Boot (tạo file .jar)
RUN mvn clean package -DskipTests

# Bước 7: Sử dụng image khác để chạy ứng dụng
FROM openjdk:17-jdk-slim

# Bước 8: Sao chép file jar từ container build sang container chạy
COPY --from=build /app/target/*.jar /app/app.jar

# Bước 9: Mở cổng ứng dụng (ví dụ cổng 8080)
EXPOSE 8080

# Bước 10: Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
