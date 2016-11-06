@echo off
type ascii-art.txt

set PROJECT_NAME=RAT SEU
set THIS_VOL=%cd:~0,1%
:: set SEU_HOME=%THIS_VOL%:
set SEU_HOME=M:
set SOFTWARE_DIR=M:\software
set HOME=M:\home
set CODEBASE=M:\workspace\00_codebase
set DOCBASE=M:\workspace\01_docbase

:: load software settings
for %%f in (%SOFTWARE_DIR%\set-env-*.cmd) do call %%f

:: currently defaults to JDK8
set JAVA_HOME=%JAVA8_HOME%
set JAVA_OPTS=%JAVA_OPTS% -Xmx4096M -Xms512M -Dfile.encoding=UTF-8

set IDEA_JDK_64=%JAVA_HOME%
set IDEA_JDK=%JAVA_HOME%
set JDK_HOME=%JAVA_HOME%
set JRE_HOME=%JAVA_HOME%\jre

set M2_HOME=%MAVEN_HOME%
set M2_REPO=%SEU_HOME%\repository
set M3_HOME=%MAVEN_HOME%
set M3_REPO=%SEU_HOME%\repository

set PATH=%JAVA_HOME%\bin;%PATH%