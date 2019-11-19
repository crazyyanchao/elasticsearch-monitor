#!/usr/bin/env bash

nohup java -Xmx128m -Dfile.encoding=utf-8 -jar ./elasticsearch-monitor-0.1.0.jar >>logs/elasticsearch-monitor.log 2>&1 &
