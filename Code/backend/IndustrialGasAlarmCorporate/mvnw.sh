#!/bin/bash
# WSL Maven wrapper - uses Windows paths so java.exe can find the JARs
# Usage: bash mvnw.sh <maven args>
MAVEN_HOME_WIN="C:\\Users\\Public\\apache-maven"
M2_JAR_WIN="C:\\Users\\Public\\apache-maven\\boot\\plexus-classworlds-2.8.0.jar"
PROJECT_DIR_WIN="D:\\BaiduSyncdisk\\Project(IndustrialGasAlarmCorporate)\\Code\\backend\\IndustrialGasAlarmCorporate"

cd "/mnt/d/BaiduSyncdisk/Project(IndustrialGasAlarmCorporate)/Code/backend/IndustrialGasAlarmCorporate" || exit 1

JAVA_OPTS="-Dclassworlds.conf=$MAVEN_HOME_WIN\\bin\\m2.conf"
JAVA_OPTS="$JAVA_OPTS -Dmaven.home=$MAVEN_HOME_WIN"
JAVA_OPTS="$JAVA_OPTS -Dmaven.multiModuleProjectDirectory=$PROJECT_DIR_WIN"

exec /mnt/d/java/jdk/bin/java $JAVA_OPTS -jar "$M2_JAR_WIN" "$@"
