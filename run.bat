@echo off

echo Сборка приложения...
call mvnw clean package -DskipTests

echo Запуск контейнеров...
call docker-compose up --build -d

echo Контейнеры запущены. Нажмите любую клавишу для выхода.
cmd /k