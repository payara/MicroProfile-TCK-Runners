#!/bin/sh

if [ -z $1 ]; then
   echo Usage: $0 '<tck report file> <format:jUnitReport|summaryTxt|testSet|failsafeSummary> [testSuiteName] [<output report path>]'
   exit
fi

rm -rf target/
mvn package
java -jar target/tck-summarizer-1.0-SNAPSHOT.jar $1 $2 $3 $4
