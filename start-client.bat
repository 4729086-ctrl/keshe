@echo off
set M2=%USERPROFILE%\.m2\repository
set CP=target\classes
set CP=%CP%;%M2%\com\fasterxml\jackson\core\jackson-databind\2.14.2\jackson-databind-2.14.2.jar
set CP=%CP%;%M2%\com\fasterxml\jackson\core\jackson-core\2.14.2\jackson-core-2.14.2.jar
set CP=%CP%;%M2%\com\fasterxml\jackson\core\jackson-annotations\2.14.2\jackson-annotations-2.14.2.jar
set CP=%CP%;%M2%\com\fasterxml\jackson\datatype\jackson-datatype-jsr310\2.15.4\jackson-datatype-jsr310-2.15.4.jar
cd /d "d:\ke she\client"
start javaw -cp "%CP%" com.shanxi.shopping.client.ShoppingClient
