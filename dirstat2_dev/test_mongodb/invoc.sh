#!/bin/sh

exec java -ea -cp .:mongo-java-driver-2.11.3.jar:org-netbeans-swing-outline.jar ma.dirstat.Main "$@"
