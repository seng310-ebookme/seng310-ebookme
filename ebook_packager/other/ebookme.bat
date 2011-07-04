@echo off

SET EB_JAVA_OPTS=%EB_JAVA_OPTS% -Xmx512m

java %EB_JAVA_OPTS% -jar EBookME.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
