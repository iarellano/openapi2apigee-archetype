# openapi2apigee-archetype

Scaffolds an api proxy from and openapi v3 spec, it also creates a mock server to be consumed by the proxy.

## installing the archetype
clone this repo then from the in cloned folder install the archetype
```bash
mvn install
```
In case you get an error about the archetype not being able to download the dependencies, then install as
```bash
mvn install -Dgrape.config=./grapeConfig.xml
```
----
Be aware that **archetypeId** is used as name of the API Proxy.

Maven plugins in generated project
* [apigee-deploy-maven-plugin](https://github.com/apigee/apigee-deploy-maven-plugin)
* [apigee-config-maven-plugin](https://github.com/apigee/apigee-config-maven-plugin)
* [yaml-properties-maven-plugin](https://github.com/ozimov/yaml-properties-maven-plugin)
* [maven-replacer-plugin](https://github.com/iarellano/maven-replacer-plugin)


## archetype parameters
|   parameter | required | description | default |
| ----------- | -------- | ----------- | ------- |
| spec | yes | url or file location of the spec file |
| org  | yes | edge organization name  |
| envs | yes | comma separated names, e.g. test,prod  |
| hosturl | no | edge management api url  | https://api.enterprise.apigee.com |
| apiversion | no | edge management api version | v1 |
| options | no | apigee-deploy-maven-plugin apigee.options parameter | update |
| delay | no | apigee-deploy-maven-plugin apigee.delay parameter | 1000 |
| authtype | no | apigee-deploy-maven-plugin apigee.authtype parameter | basic |
| basepath | yes | api proxy basepath |  |
| virtualhost | yes | virtualhost to access the api proxy |  |
| proxydomain | yes | domain of the server to consume the api proxy |  |
| proxyport | no | remote port to access the api proxy |  |
| proxyscheme | no | to use either https or http to access the api proxy | https |
| targeturl | no | target url to be consumed by proxy, ignore if mockserver=true |  |
| mockserver | no | whether to create a mock server | false |
| apigee-deploy-plugin-version | no | version of apigee-deploy-maven-plugin to use | 1.1.7 |
| apigee-config-plugin-version | no | version of apigee-config-maven-plugin to use | 1.3.7 |
| config-options | no | apigee-config-maven-plugin apigee.config.options parameter | update |
| config-exportdir | no | apigee-config-maven-plugin apigee.config.exportDir parameter | target |
| config-config-dir | no | apigee-config-maven-plugin apigee.config.dir parameter | target/ege |
| enable-cors | no | add a CORS policy | false |
| cors-policy-name | no | name of the CORS policy only if <code>enable-cors=true</code> | AM-AddCORS |


## generate files/folders

|   name | description | 
|  ----- | ----------- |
| apiproxy | apiproxy implementation |
| config   | files configuration variables per environment |
| edge     | configuration files for apigee entities |
| mock     | mock server when <code>mockserver=true</code> |
| test     | basic integration tests with cucumberjs + [Apickli](https://github.com/apickli/apickli) |

instruction for deploying and testing the apiproxy will be in generate README.mc file

## usage
Asuming you already have apigee edge account with two environmets test and prod and if you don't no problem, register for trial org at [apigee.com](https://apigee.com.) 


To create an api proxy with mock server for the petstore spec, go to your project folder where you want to create your project, then in your console:
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.github.iarellano \
  -DarchetypeArtifactId=openapi2apigee-archetype \
  -DinteractiveMode=false \
  -DgroupId=com.example \
  -DartifactId=petstore-v1 \
  -Dversion=1.0 \
  -Dspec=https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml \
  -Dorg=[APIGEE_ORG] \
  -Denvs=test,prod \
  -Dbasepath=/petstore/v1 \
  -Dhosturl=https://api.enterprise.apigee.com \
  -Dproxydomain=[EDGE ORG DOMAIN] \
  -Dvirtualhost=secure \
  -Dmockserver=true
```

That will gereate a forlder petstore-v1 containing all your artifacts. Follow the instructions in the generated README.md file.
