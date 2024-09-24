# Используем базовый образ OpenJDK
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Скопируйте JAR-файл в контейнер
COPY build/libs/task-tracker-1.0-SNAPSHOT.jar /app/task-tracker-1.0-SNAPSHOT.jar

# Открываем порт, на котором будет работать ваше приложение
EXPOSE 8080

# Укажите команду для запуска JAR-файла
ENTRYPOINT ["java", "-jar", "/app/task-tracker-1.0-SNAPSHOT.jar"]



