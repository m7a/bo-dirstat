#!/bin/sh

exec java -cp .:mysql-connector-java-5.1.27-bin.jar ma.dirstat.Main "$@"
