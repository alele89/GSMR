%ECHO OFF
%ECHO Starting GSMR System
PAUSE
%ECHO SwitchBoard Console
START "Switch Board CONSOLE" /NORMAL java SwitchBoard %1
%ECHO Train A Monitoring  Console
START "Train A CONSOLE" /NORMAL java TrainA %1
%ECHO Train B Monitoring Console
START "Train B CONSOLE" /NORMAL java TrainB %1
%ECHO Train C Monitoring Console
START "Train C CONSOLE" /NORMAL java TrainC %1