#!/usr/bin/env bash

nohup java -Xmx128m -Dfile.encoding=utf-8 -jar ./elasticsearch-monitor-0.1.0.jar >>logs/elasticsearch-monitor.log 2>&1 &

# 启动远程调试
#nohup java -Xmx128m -Dfile.encoding=utf-8 -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n -jar ./elasticsearch-monitor-0.1.0.jar >>logs/elasticsearch-monitor.log 2>&1 &


