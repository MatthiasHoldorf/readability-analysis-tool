@echo off
java -jar lib/rat-executor-cmd-${project.version}.jar -o examples/output-directory/ examples/multiple-files/*
pause