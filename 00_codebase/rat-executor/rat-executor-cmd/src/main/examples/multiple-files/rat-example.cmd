@echo off
java -jar ../../lib/rat-executor-cmd-${project.version}.jar -c ../../config/rat-config.xml *.docx
pause