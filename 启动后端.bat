@echo off
echo ====================================
echo 期末复习题库系统 - 后端启动脚本
echo ====================================
echo.

cd backend

echo [1/3] 清理旧的编译文件...
call mvn clean

echo.
echo [2/3] 编译项目...
call mvn package -DskipTests

echo.
echo [3/3] 启动项目...
java -jar target\exam-practice-0.0.1.jar

pause
