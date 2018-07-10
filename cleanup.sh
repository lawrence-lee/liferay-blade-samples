#!/bin/bash
set +e
ps -Af| grep org.apache.catalina.startup.Bootstrap | grep -v grep | awk '{print$2}' | xargs kill -9