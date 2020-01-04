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

Resulting pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>petstore-v1</artifactId>
    <version>1</version>
    <name>petstore-v1${deployment.suffix}</name>
    <packaging>pom</packaging>
    <pluginRepositories>
        <pluginRepository>
            <id>central</id>
            <name>Maven Plugin Repository</name>
            <url>http://repo1.maven.org/maven2</url>
            <layout>default</layout>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <releases>
                <updatePolicy>never</updatePolicy>
            </releases>
        </pluginRepository>
    </pluginRepositories>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <deployment.suffix>-${user.name}</deployment.suffix>
        <proxy.basename>petstore-v1${deployment.suffix}</proxy.basename>
        <apigee.username>${env.APIGEE_USERNAME}</apigee.username>
        <apigee.password>${env.APIGEE_PASSWORD}</apigee.password>
        <apigee.config.dir>${project.basedir}/target/edge</apigee.config.dir>
    </properties>
    <profiles>
        <profile>
            <id>qa</id>
            <properties>
                <apigee.profile>qa</apigee.profile>
                <apigee.org>org-trial</apigee.org>
                <apigee.env>test</apigee.env>
                <api.testtag>@apiproxy,@services,@test</api.testtag>
            </properties>
        </profile>
        <profile>
            <id>org-eval-test</id>
            <properties>
                <apigee.profile>org-eval-test</apigee.profile>
                <apigee.org>org-eval</apigee.org>
                <apigee.env>test</apigee.env>
                <api.testtag>@apiproxy,@services,@test</api.testtag>
            </properties>
        </profile>
        <profile>
            <id>org-trial-prod</id>
            <properties>
                <apigee.profile>org-trial-prod</apigee.profile>
                <apigee.org>org-trial</apigee.org>
                <apigee.env>prod</apigee.env>
                <api.testtag>@apiproxy,@services,@prod</api.testtag>
            </properties>
        </profile>
        <profile>
            <id>org-eval-prod</id>
            <properties>
                <apigee.profile>org-eval-prod</apigee.profile>
                <apigee.org>org-eval</apigee.org>
                <apigee.env>prod</apigee.env>
                <api.testtag>@apiproxy,@services,@prod</api.testtag>
            </properties>
        </profile>
        <profile>
            <id>integration-test</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.apigee.edge.config</groupId>
                        <artifactId>apigee-config-maven-plugin</artifactId>
                        <version>1.3.7</version>
                        <executions>
                            <execution>
                                <id>create-config-apiproduct</id>
                                <phase>verify</phase>
                                <goals>
                                    <goal>apiproducts</goal>
                                </goals>
                            </execution>
                            <execution>
                                <id>create-config-developer</id>
                                <phase>verify</phase>
                                <goals>
                                    <goal>developers</goal>
                                </goals>
                            </execution>
                            <execution>
                                <id>create-config-app</id>
                                <phase>verify</phase>
                                <goals>
                                    <goal>apps</goal>
                                </goals>
                            </execution>
                            <execution>
                                <id>get-app-keys</id>
                                <phase>verify</phase>
                                <goals>
                                    <goal>exportAppKeys</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <artifactId>maven-antrun-plugin</artifactId>
                        <executions>
                            <execution>
                                <id>generate-sources</id>
                                <phase>verify</phase>
                                <configuration>
                                    <tasks>
                                        <mkdir dir="${project.build.directory}/results/integration"/>
                                    </tasks>
                                </configuration>
                                <goals>
                                    <goal>run</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>1.3.2</version>
                        <executions>
                            <!--run integration tests-->
                            <execution>
                                <id>apickli</id>
                                <phase>verify</phase>
                                <goals>
                                    <goal>exec</goal>
                                </goals>
                                <configuration>
                                    <executable>node</executable>
                                    <commandlineArgs>
                                        ./node_modules/cucumber/bin/cucumber.js target/test/integration/features --format json:${project.build.directory}/results/integration/test.json ${integration.format} --tags ${api.testtag}
                                    </commandlineArgs>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
        <profile>
            <id>performance-test</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.lazerycode.jmeter</groupId>
                        <artifactId>jmeter-maven-plugin</artifactId>
                        <version>2.2.0</version>
                        <executions>
                            <execution>
                                <id>jmeter-tests</id>
                                <phase>verify</phase>
                                <goals>
                                    <goal>jmeter</goal>
                                </goals>
                            </execution>
                        </executions>
                        <configuration>
                            <testFilesDirectory>target/test/performance</testFilesDirectory>
                            <testResultsTimestamp>false</testResultsTimestamp>
                            <resultsDirectory>${project.build.directory}/results/performance</resultsDirectory>
                            <ignoreResultFailures>true</ignoreResultFailures>
                            <suppressJMeterOutput>false</suppressJMeterOutput>
                            <propertiesUser>
                                <testData>${performance.testData}</testData>
                                <threads>${performance.threads}</threads>
                                <loopCount>${performance.loopCount}</loopCount>
                                <rampUpPeriodSecs>${performance.rampUpPeriodSecs}</rampUpPeriodSecs>
                            </propertiesUser>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>io.apigee.build-tools.enterprise4g</groupId>
                    <artifactId>apigee-edge-maven-plugin</artifactId>
                    <version>1.0.2</version>
                </plugin>
            </plugins>
        </pluginManagement>
        <plugins>
            <!-- load yaml settings -->
            <plugin>
                <groupId>it.ozimov</groupId>
                <artifactId>yaml-properties-maven-plugin</artifactId>
                <version>1.1.2</version>
                <executions>
                    <execution>
                        <id>read-external-global-properties</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>read-project-properties</goal>
                        </goals>
                        <configuration>
                            <files>
                                <file>${project.basedir}/config/global-config.yaml</file>
                            </files>
                        </configuration>
                    </execution>
                    <execution>
                        <id>read-external-profile-properties</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>read-project-properties</goal>
                        </goals>
                        <configuration>
                            <files>
                                <file>${project.basedir}/config/${apigee.profile}.yaml</file>
                            </files>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <!-- copy the full apiproxy folder to target folder -->
            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <version>2.6</version>
                <executions>
                    <!-- copy resources to target directory -->
                    <execution>
                        <id>copy-resources</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <nonFilteredFileExtensions>
                                <nonFilteredFileExtension>jar</nonFilteredFileExtension>
                            </nonFilteredFileExtensions>
                            <overwrite>true</overwrite>
                            <resources>
                                <resource>
                                    <directory>${project.basedir}</directory>
                                    <filtering>true</filtering>
                                    <includes>
                                        <include>apiproxy/**</include>
                                        <include>edge/api/petstore-v1/**</include>
                                        <include>edge/org/**</include>
                                        <include>test/**</include>
                                    </includes>
                                </resource>
                            </resources>
                            <outputDirectory>${project.build.directory}</outputDirectory>
                        </configuration>
                    </execution>
                    <!-- copy profiled proxy dependencies -->
                    <execution>
                        <id>copy-profile-resources</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <nonFilteredFileExtensions>
                                <nonFilteredFileExtension>jar</nonFilteredFileExtension>
                            </nonFilteredFileExtensions>
                            <overwrite>true</overwrite>
                            <resources>
                                <resource>
                                    <directory>${project.basedir}/edge/env/${apigee.profile}</directory>
                                    <filtering>true</filtering>
                                    <includes>
                                        <include>**</include>
                                    </includes>
                                </resource>
                            </resources>
                            <outputDirectory>${project.build.directory}/edge/env/${apigee.env}/</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>com.github.iarellano</groupId>
                <artifactId>replacer</artifactId>
                <version>1.6.0</version>
                <executions>
                    <execution>
                        <id>replace-xmls</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>replace</goal>
                        </goals>
                        <configuration>
                            <basedir>${project.build.directory}</basedir>
                            <includes>
                                <include>apiproxy/policies/*.xml</include>
                                <include>apiproxy/proxies/*.xml</include>
                                <include>apiproxy/targets/*.xml</include>
                                <include>apiproxy/*.xml</include>
                            </includes>
                            <excludes>
                                <!-- Add here those files you do not want to be replaced -->
                            </excludes>
                            <replacements>
                                <replacement>
                                    <xpath>/ProxyEndpoint/HTTPProxyConnection/VirtualHost/text()</xpath>
                                    <token>^(.*)$</token>
                                    <value>${northbound.virtualhost}</value>
                                </replacement>
                                <replacement>
                                    <xpath>/TargetEndpoint/HTTPTargetConnection/VirtualHost/text()</xpath>
                                    <token>^(.*)$</token>
                                    <value>${targetEndpoint.url}</value>
                                </replacement>
                            </replacements>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>io.apigee.build-tools.enterprise4g</groupId>
                <artifactId>apigee-edge-maven-plugin</artifactId>
                <version>1.1.7</version>
                <executions>
                    <execution>
                        <id>configure-bundle-step</id>
                        <phase>package</phase>
                        <goals>
                            <goal>configure</goal>
                        </goals>
                    </execution>
                    <!--deploy bundle -->
                    <execution>
                        <id>deploy-bundle-step</id>
                        <phase>install</phase>
                        <goals>
                            <goal>deploy</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!-- Configuration -->
            <plugin>
                <groupId>com.apigee.edge.config</groupId>
                <artifactId>apigee-config-maven-plugin</artifactId>
                <version>1.3.7</version>
                <executions>
                    <execution>
                        <id>create-config-cache</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>caches</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>create-config-targetserver</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>targetservers</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>create-config-kvm</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>keyvaluemaps</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.3.2</version>
                <executions>
                    <!-- npm install -->
                    <execution>
                        <id>npm-install</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>exec</goal>
                        </goals>
                        <configuration>
                            <executable>npm</executable>
                            <commandlineArgs>install</commandlineArgs>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```