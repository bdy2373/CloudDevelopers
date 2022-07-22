# Running apps with the SCS(SpringCloudServices) Config Server Instance on TAS

[official guide about Spring Cloud Config Server on TAS](https://docs.pivotal.io/spring-cloud-services/3-1/common/config-server/index.html)

Optinally, you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-spring-cloud-config/complate)


## Prerequisites
- [dev-env prerequisites](lab-prerequisites-dev-env.md)
- we will re-use the app from [previous lab: lab-spring-cloud-config-client-app](lab-spring-cloud-config-client-app.md)

# Lab

## Create a SCS Config Server Instance on TAS

[offical guide](https://docs.pivotal.io/spring-cloud-services/3-1/common/config-server/configuring-with-git.html)

- scs 2.x tile's service name: 'p-config-server'
- scs 3.x tile's service name: 'p.config-server'

```
$ cf target -o MY_ORG -s MY_SPACE

$ cf marketplace
service              plans         description                                                                                                broker
p.config-server      standard      Config Server for Spring Cloud Applications                                                                p-spring-cloud-services
p.service-registry   standard      Service Registry for Spring Cloud Applications                                                             p-spring-cloud-services

$ cf marketplace -s p.config-server
Getting service plan information for service p.config-server as user...
OK

service plan   description       free or paid
standard       A standard plan   free
```

please make sure the the git url should be accessible from spring-cloud-service broker VM.
```
$ cf create-service -c '{ "git": { "uri": "https://github.com/myminseok/spring-cloud-sample-configrepo" }, "count": 1 }' p.config-server standard my-config-server
```

do as following if the repo requires credential,
```
$ cf create-service -c '{ "git": { "uri": "https://github.com/myminseok/spring-cloud-sample-configrepo.git", "label": "master", "username": "YOUR GIT ID", "password": "YOUR-GIT-PASSWORD", "skipSslValidation": true}, "count": 1 }' p.config-server standard my-config-server
```

for example,  the repo in the lab envirionment
```
 cf create-service -c '{ "git": { "uri": "http://gitlab01.ds.global:18080/tas-dev-workshop/spring-cloud-sample-configrepo.git" }, "count": 1 }' p.config-server standard my-config-server
```

#### ssh repo access 

- generate privateKey in single line.
make sure to remove all of spaces in the result.
```
$ cat ~/.ssh/id_rsa | awk '{printf "%s\\n", $0}'
```


```
$ cf create-service -c '{ "git": { "uri": "ssh://git@github.com/myminseok/spring-cloud-sample-configrepo.git", "label": "master", "strictHostKeyChecking": false, "privateKey": "-----BEGIN RSA PRIVATE KEY-----\nMIIEpAI ...   =\n-----END RSA PRIVATE KEY-----"}, "count": 1 }' p.config-server standard my-config-server

# An equivalent Secure Copy Protocol (SCP) style URI might be specified as shown in the following command:

$ cf create-service -c '{ "git": { "uri": "git@github.com:myminseok/spring-cloud-sample-configrepo.git", "label": "master", "strictHostKeyChecking": false, "privateKey": "-----BEGIN RSA PRIVATE KEY-----\nMIIEpAI ...   =\n-----END RSA PRIVATE KEY-----"}, "count": 1 }' p.config-server standard my-config-server


```
> scs 2.x tile: service name is 'p-config-server'

> ssh-access privateKey: The private key that identifies the Git user, with all newline characters replaced by \n. Passphrase-encrypted private keys are not supported.  https://docs.pivotal.io/spring-cloud-services/3-1/common/config-server/configuring-with-git.html#ssh-repository-access


```
$ cf service my-config-server
Showing info of service my-config-server in org myorg / space dev as user...

name:            my-config-server
service:         p.config-server
tags:
plan:            standard
[...]

Showing status of last operation from service my-config-server...

status:    create succeeded

```
update service instance config
```
$ cf update-service -c '{ "git": { "uri": "https://github.com/myminseok/spring-cloud-sample-configrepo" }, "count": 1 }' my-config-server
```


## Develop the app(cloud-native-app)
we will re-use the app from [previous lab-spring-cloud-config-client-app](lab-spring-cloud-config-client-app.md)

### 1. Edit dependency(pom.xml)
[official guide](https://docs.pivotal.io/spring-cloud-services/3-1/common/client-dependencies.html)

use spring initializr https://start.spring.io with additional dependencies
- dependencies: web,config-client(TAS), spring-security, spring-boot-actuator 

### 2. bootstrap.yml
on TAS, you can comment out spring.cloud.config.uri. TAS will automatically connect if you bind app with config server instance.

```
spring:
  cloud:
    config:
      #uri: ${vcap.services.my-config-server.credentials.uri:http://localhost:8888} 
      fail-fast: true
      
```
> fail-fast: In some cases, you may want to fail startup of a service if it cannot connect to the Config Server. If this is the desired behavior, set the bootstrap configuration property spring.cloud.config.fail-fast=true to make the client halt with an Exception


### 3. application.yml
expose actuator endpoints on cloud-native-spring/src/main/resources/application.yml for development purpose only.

```
spring:
  application:
    name: cloud-native-spring

management:
  endpoints:
    web:
      exposure:
        include: '*'

cook.special: "Kim Chi in cloud-native-spring app"
```


### 4. [Disable HTTP Basic Authentication(only for development)](lab-spring-cloud-config-client-app.md#2-disable-http-basic-authenticationonly-for-development)

### 5. Build the application
```
  mvn clean package -DskipTests
```


## Deploy the app on TAS

### 1. Create an application manifest
[app manifest reference](lab-cf-manifest.md). edit lab-spring-cloud-config/cloud-native-spring/manifest.yml

```
applications:
- name: cloud-native-spring
  random-route: true
  instances: 1
  path: ./target/cloud-native-spring-0.0.1-SNAPSHOT.jar
  buildpacks:
  - java_buildpack_offline
  services:
  - my-config-server
  #env:
  #  SPRING_PROFILES_ACTIVE: prod
```
> 
    


### 2. Push application into Cloud Foundry
[lab-app-cf-push](lab-app-cf-push.md)
```
cf push
```
check the application logs 
```
cf logs cloud-native-spring
```

### 3. Testing the application app on TAS
changing git config and check the app. reference the test procecdure from the [previous lab-spring-cloud-config-client-app](lab-spring-cloud-config-client-app.md)

```
curl -k https://<APP-URL-TAS>/menu

Today's special is: Kim Chi(git-repo> application-cloud.yml)
```

check if the SPRING_PROFILES_ACTIVE is set.

```
applications:
- name: cloud-native-spring
  random-route: true
  instances: 1
  path: ./build/libs/cloud-native-spring-1.0-SNAPSHOT.jar
  services:
  - my-config-server
  env:
    SPRING_PROFILES_ACTIVE: prod
```

```
cf push

curl -k https://<APP-URL-TAS>/menu

Today's special is: Kim Chi(git-repo> application-prod.yml)
```

## Modifying the configuration on git repo


cf update-service -c '{ "git": { "uri": "https://github.com/myminseok/spring-cloud-sample-configrepo", "refreshRate": 10} }'  my-config-server


### 1. change file on config repo.
change 'application-dev.yml' on github.com/<YOUR-PROJECT>/spring-cloud-sample-configrepo.git

### 2. restart app to refresh

### 3. check configuraton changes on client app.
```
curl -k https://<APP-URL-TAS>/menu

```
	


*The End of this lab*

---

## Additional Resources
- (optional) cf plugin: https://github.com/pivotal-cf/spring-cloud-services-cli-plugin/blob/main/docs/cli.md. https://plugins.cloudfoundry.org/#spring-cloud-services

```
 cf install-plugin ./spring-cloud-services-cli-plugin-linux-amd64-1.0.22 

 cf config-server-sync-mirrors my-config-server
Successfully refreshed mirrors

cf scs-stop my-config-server

cf scs-start my-config-server

cf scs-restart my-config-server

```

