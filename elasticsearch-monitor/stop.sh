#!/usr/bin/env bash

kill -9 `ps -ef|grep elasticsearch-monitor-0.1.0.jar|grep -v grep|awk '{print $2}'`
