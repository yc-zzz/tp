#!/usr/bin/env bash

# change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Resolve today's date in yyyy-MM-dd format
TODAY=$(date +%Y-%m-%d)

# Replace {TODAY} placeholder with today's date in a temp expected file
sed "s/{TODAY}/$TODAY/g" EXPECTED.TXT > EXPECTED-RESOLVED.TXT

cp EXPECTED-RESOLVED.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL.TXT
diff EXPECTED-UNIX.TXT ACTUAL.TXT
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi