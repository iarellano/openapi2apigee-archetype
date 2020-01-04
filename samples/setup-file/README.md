## Sample generation using a setup file

Create a project by executing the archetype as follows:
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.github.iarellano \
  -DarchetypeArtifactId=openapi2apigee-archetype \
  -DinteractiveMode=false \
  -DgroupId=com.example \
  -DartifactId=petstore-v1 \
  -Dversion=1.0 \
  -Dspec=https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml \
  -Dbasepath=/petstore/v1 \
  -DedgeSetupFile=./setup.yaml
```

That will create you the following project:
```
.
├── proxy-template
│   ├── apiproxy
│   │   ├── policies
│   │   ├── proxies
│   │   │   └── default.xml
│   │   ├── proxy-template.xml
│   │   └── targets
│   │       └── default.xml
│   ├── config
│   │   ├── global-config.yaml
│   │   ├── org-eval-prod.yaml
│   │   ├── org-eval-test.yaml
│   │   ├── org-trial-prod.yaml
│   │   └── qa.yaml
│   ├── doc
│   │   └── petstore.yaml
│   ├── edge
│   │   ├── api
│   │   │   └── proxy-template
│   │   │       └── maskconfigs.json
│   │   ├── env
│   │   │   ├── org-eval-prod
│   │   │   │   └── kvms.json
│   │   │   ├── org-eval-test
│   │   │   │   └── kvms.json
│   │   │   ├── org-trial-prod
│   │   │   │   └── kvms.json
│   │   │   └── qa
│   │   │       └── kvms.json
│   │   └── org
│   │       ├── apiProducts.json
│   │       ├── developerApps.json
│   │       ├── developers.json
│   │       └── kvms.json
│   ├── mock
│   │   ├── apiproxy
│   │   │   ├── mock-proxy-template.xml
│   │   │   ├── policies
│   │   │   │   └── add-cors.xml
│   │   │   ├── proxies
│   │   │   │   └── default.xml
│   │   │   ├── resources
│   │   │   │   └── hosted
│   │   │   │       ├── api
│   │   │   │       │   ├── openapi.yaml
│   │   │   │       │   └── petstore.yaml
│   │   │   │       ├── app.yaml
│   │   │   │       ├── config.js
│   │   │   │       ├── controllers
│   │   │   │       │   ├── Controller.js
│   │   │   │       │   ├── index.js
│   │   │   │       │   └── PetsController.js
│   │   │   │       ├── expressServer.js
│   │   │   │       ├── index.js
│   │   │   │       ├── logger.js
│   │   │   │       ├── package.json
│   │   │   │       ├── README.md
│   │   │   │       ├── services
│   │   │   │       │   ├── index.js
│   │   │   │       │   ├── PetsService.js
│   │   │   │       │   └── Service.js
│   │   │   │       └── utils
│   │   │   │           └── openapiRouter.js
│   │   │   └── targets
│   │   │       └── default.xml
│   │   └── pom.xml
│   ├── package.json
│   ├── pom.xml
│   ├── README.md
│   └── test
│       ├── integration
│       │   ├── config.json
│       │   ├── features
│       │   │   ├── api.feature
│       │   │   ├── errorHandling.feature
│       │   │   ├── step_definitions
│       │   │   │   ├── apickli-gherkin.js
│       │   │   │   ├── dev-apps.js
│       │   │   │   ├── error-handling.js
│       │   │   │   ├── hmac.js
│       │   │   │   └── json-replacer.js
│       │   │   └── support
│       │   │       ├── env.js
│       │   │       ├── hmac-util.js
│       │   │       ├── init.js
│       │   │       └── utils.js
│       │   └── sample-payloads
│       │       └── sample-payload.json
│       └── performance
│           ├── testdata.csv
│           └── test.jmx
└── setup.yaml
```

While creating the project the following properties are available for your templates:
```yaml
config-exportdir: target
specFileName: petstore.yaml
config-dir: target/edge
edgeSetupFile: ./setup.yaml
clientsecret: edgeclisecret
proxyscheme: https
tokenurl: https://login.apigee.com/oauth/token
spec: https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml
package: com.github.iarellano
authtype: basic
setupMap:
  qa:
    proxyport: 443
    tokenurl: https://login.apigee.com/oauth/token
    clientid: edgecli
    org: org-trial
    basepath: /proxy-template/v1
    clientsecret: edgeclisecret
    profile: qa
    apiversion: v1
    config-exportdir: target
    env: test
    authtype: basic
    mockserver: true
    cors-policy-name: AM-AddCORS
    delay: 1000
    proxydomain: org-trial-test.apigee.net
    options: update
    additional-settings:
      setting1: value1
      setting2: value2
    hosturl: https://api.enterprise.apigee.com
    enable-cors: false
    config-dir: target/edge
    proxyscheme: https
    targeturl: https://org-trial-test.apigee.net:443/mock/proxy-template/v1
    virtualhost: secure
    config-options: create
  org-eval-test:
    proxyport: 443
    tokenurl: https://login.apigee.com/oauth/token
    clientid: edgecli
    org: org-eval
    basepath: /proxy-template/v1
    clientsecret: edgeclisecret
    profile: org-eval-test
    apiversion: v1
    config-exportdir: target
    env: test
    authtype: basic
    mockserver: false
    cors-policy-name: AM-AddCORS
    delay: 1000
    proxydomain: org-eval-test.apigee.net
    options: update
    hosturl: https://api.enterprise.apigee.com
    enable-cors: false
    config-dir: target/edge
    proxyscheme: https
    targeturl: https://httpbin.org
    virtualhost: secure
    config-options: create
  org-trial-prod:
    proxyport: 443
    tokenurl: https://login.apigee.com/oauth/token
    clientid: edgecli
    org: org-trial
    basepath: /proxy-template/v1
    clientsecret: edgeclisecret
    profile: org-trial-prod
    apiversion: v1
    config-exportdir: target
    env: prod
    authtype: basic
    mockserver: false
    cors-policy-name: AM-AddCORS
    delay: 1000
    proxydomain: org-trial-prod.apigee.net
    options: override
    additional-settings:
      setting1: value1
      setting2: value2
    hosturl: https://api.enterprise.apigee.com
    enable-cors: false
    config-dir: target/edge
    proxyscheme: https
    targeturl: https://httpbin.org
    virtualhost: secure
    config-options: update
  org-eval-prod:
    proxyport: 443
    tokenurl: https://login.apigee.com/oauth/token
    clientid: edgecli
    org: org-eval
    basepath: /proxy-template/v1
    clientsecret: edgeclisecret
    profile: org-eval-prod
    apiversion: v1
    config-exportdir: target
    env: prod
    authtype: basic
    mockserver: false
    cors-policy-name: AM-AddCORS
    delay: 1000
    proxydomain: org-eval-prod.apigee.net
    options: override
    hosturl: https://api.enterprise.apigee.com
    enable-cors: false
    config-dir: target/edge
    proxyscheme: https
    targeturl: https://httpbin.org
    virtualhost: secure
    config-options: update
enable-cors: false
basepath: /proxy-template/v1
proxyport: 443
options: update
artifactId: proxy-template
apigee-config-maven-plugin: 1.3.7
config-options: update
flows: [APIProxyFlow{name='listPets', desc='List all pets', path='/pets', verb='GET'}, APIProxyFlow{name='createPets', desc='Create a pet', path='/pets', verb='POST'}, APIProxyFlow{name='showPetById', desc='Info for a specific pet', path='/pets/{petId}', verb='GET'}]
mockserver: false
delay: 1000
apigee-deploy-plugin-version: 1.1.7
hosturl: https://api.enterprise.apigee.com
profiles: [qa, org-eval-test, org-trial-prod, org-eval-prod]
cors-policy-name: AM-AddCORS
version: 1.0
groupId: com.github.iarellano
clientid: edgecli
apiversion: v1
additional-settings:
  setting2:
    sub-setting1: value1
    sub-setting2: value2
  setting1: value1
``` 