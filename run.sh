#!/bin/sh

MAIN_CLASS=matching.ContentMatching
MAIN_JAR=target/find-multikeyword-0.0.1-SNAPSHOT.jar
lib_project=lib
JAVA=$JAVA_HOME/bin/java
CLASS_PATH=$JAVA_HOME/lib/*:$lib_project/*:$MAIN_JAR
java -classpath $CLASS_PATH $MAIN_CLASS
