#!/usr/bin/env bash

rm -rf test


#rm -rf ~/.groovy/grapes

rm -rf ~/.m2/repository/com/github/iarellano/openapi2apigee-archetype

mvn clean install

mkdir -p test

cd test

mvn -X archetype:generate \
  -DarchetypeGroupId=com.github.iarellano \
  -DarchetypeArtifactId=openapi2apigee-archetype \
  -DinteractiveMode=false \
  -DgroupId=com.github.iarellano \
  -DartifactId=proxy-template \
  -Dversion=1.0 \
  -Dspec=https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml \
  -Dorg=isciad94-trial \
  -Denvs=test,prod \
  -Dbasepath=/proxy-template/v1 \
  -Dhosturl=https://api.enterprise.apigee.com \
  -Dproxydomain=isciad94-trial-test.apigee.net \
  -Dproxyprotocol=https \
  -Dproxyport=443 \
  -Dvirtualhost=secure \
  -Dtargeturl=https://httpbin.org \
  -Dmockserver=true


cd ..

