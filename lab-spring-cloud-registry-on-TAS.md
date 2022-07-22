# Running apps with the SCS(SpringCloudServices) Service Registry Instance on TAS

- [TAS official guide](https://docs.pivotal.io/spring-cloud-services/3-1/common/service-registry/index.html)

## Prerequisites
- [dev-env prerequisites](lab-prerequisites-dev-env.md)

# Lab

## Create a Service Registry instance on TAS

```
$ cf target -o myorg -s development

$ cf marketplace
service              plans         description                                                                                                broker
p.config-server      standard      Config Server for Spring Cloud Applications                                                                p-spring-cloud-services
p.service-registry   standard      Service Registry for Spring Cloud Applications                                                             p-spring-cloud-services


$ cf marketplace -s p.service-registry
Getting service plan information for service p.service-registry as admin...
OK

service plan   description     free or paid
standard       Standard Plan   free

$ cf create-service p.service-registry standard greeter-service-registry

$ cf service greeter-service-registry
Showing info of service service-registry in org myorg / space development as user...

name:            greeter-service-registry
service:         p.service-registry
bound apps:
tags:

[...]

Showing status of last operation from service service-registry...

status:    create succeeded
```

## Deploy backend Apps on TAS
we will re-use the app from [developing backend app on local](lab-spring-cloud-registry-local-backend-app.md).

also you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-spring-cloud-registry)

### 1. check dependency (PROJECT_HOME/pom.xml)
- [official guide](https://docs.pivotal.io/spring-cloud-services/3-1/common/client-dependencies.html)
- check dependencies: `web`,  `reactive web` ,`service-registry(TAS)`, spring-security, spring-boot-actuator 


### 2. [Disable HTTP Basic Authentication(only for development)](lab-spring-cloud-config-on-TAS.md#2-disable-http-basic-authenticationonly-for-development)


### 3. Edit PROJECT_HOME/src/main/resources/application.yml
```
server.port: 9001

spring:
  profiles.active: dev
  application:
    name: greeter-messages

## you can comment out all eureka config on TAS
eureka:
  client:
    serviceUrl:
      defaultZone: ${vcap.services.greeter-service-registry.credentials.uri:http://127.0.0.1:8761}/eureka/

```
> server.port: no affect on TAS. always 8080
> spring.application.name will be registered in registry service.
> eureka.client.serverUrl.defaultZone: you can comment out all eureka config on TAS or vcap.services.<service-instance-name>.credentials.uri

### 4. Create PROJECT_HOME/manifest.yml
[app manifest reference](lab-cf-manifest.md) 


```
applications:
  - name: greeter-messages
    random-route: true
    instances: 1
    path: ./target/greeter-messages-0.0.1-SNAPSHOT.jar
    buildpacks:
      - java_buildpack_offline
    services:
      - greeter-service-registry
    #env:
    #  SPRING_PROFILES_ACTIVE: dev
```


### 5. Build the application
```
cd PROJECT_HOME
mvn clean package -DskipTests
```


### 6. Push application into Cloud Foundry
[lab-app-cf-push](lab-app-cf-push.md)
```
cd PROJECT_HOME
cf push -f manifest.yml
```

### 7. Check the app on Service Registry Dashboard.
the app should be registered on the service registry in a few second.


### 8. check the app.
on web-browser:
```
$ cf apps

Getting apps in org test / space dev as admin...
OK

name                  requested state   instances   memory   disk   urls
greeter-messages      started           1/1         1G       1G     greeter-messages-responsive-lemur-wy.apps.pez.pcfdemo.net


https://<TAS-APP-URL>/greeting?salutation=hi&name=J

{"message":"hi, J!"}%

```


## [deploying frontend app on TAS]
we will re-use the app from [developing frontend app on local ](lab-spring-cloud-registry-local-frontend-app.md)

### 1. check dependency (PROJECT_HOME/pom.xml)
- [official guide](https://docs.pivotal.io/spring-cloud-services/3-1/common/client-dependencies.html)
- check dependencies: web, reactive web, spring-security, service-registry(TAS), spring-boot-actuator


### 2. [Disable HTTP Basic Authentication(only for development)](lab-spring-cloud-config-on-TAS.md#2-disable-http-basic-authenticationonly-for-development)


### 3. Edit PROJECT_HOME/src/main/resources/application.yml
```
server.port: 9002

spring:
  application:
    name: greeter
  profiles.active: dev

## you can comment out all eureka config on TAS
eureka:
  client:
    serviceUrl:
      defaultZone: ${vcap.services.greeter-service-registry.credentials.uri:http://127.0.0.1:8761}/eureka/

management:
  endpoints:
    web:
      exposure:
        include: '*'

```
> server.port: no affect on TAS. always 8080
> spring.application.name will be registered in registry service.
> eureka.client.serverUrl.defaultZone: you can comment out all eureka config on TAS or vcap.services.<service-instance-name>.credentials.uri

### 4. Create a PROJECT_HOME/manifest.yml
[app manifest reference](lab-cf-manifest.md)
```
applications:
  - name: greeter
    random-route: true
    instances: 1
    path: ./target/greeter-0.0.1-SNAPSHOT.jar
    buildpacks:
      - java_buildpack_offline
    services:
      - greeter-service-registry

    #env:
    #  SPRING_PROFILES_ACTIVE: dev
```


### 5. Build application
```
cd PROJECT_HOME
mvn clean package -DskipTests
```

### 6. Push application into Cloud Foundry
[lab-app-cf-push](lab-app-cf-push.md)
```
cd PROJECT_HOME
cf push -f manifest.yml
```

### 7. Check Service Registry Dashboard.
the app should be registered on the service registry in a few second.

### 8. check the app
```
$ cf apps

Getting apps in org test / space dev as admin...
OK

name                  requested state   instances   memory   disk   urls
greeter-messages      started           1/1         1G       1G     greeter-messages-responsive-lemur-wy.apps.pez.pcfdemo.net
greeter               started           1/1         1G       1G     greeter-forgiving-possum-rl.apps.pez.pcfdemo.net

```
on web-browser:
```
https://<TAS-APP-URL>/hello?salutation=HI&name=John

Hi, John!
```

##  Container-to-Container Networking (using `apps.internal` domain )
Before a client app can use the Service Registry to reach this directly-registered app, you must add a network policy that allows traffic from the client app to this app.
https://docs.pivotal.io/spring-cloud-services/3-1/common/service-registry/writing-client-applications.html#consume-using-c2c


### Prerequisites
- TAS admin should enable 'network policy by developer' on TAS tile in advance.

### 1. unmap route

```
$ cf apps
Getting apps in org test / space dev as admin...
OK

name                  requested state   instances   memory   disk   urls
greeter               started           1/1         1G       1G     greeter-forgiving-possum-rl.apps.pez.pcfdemo.net
greeter-messages      started           1/1         1G       1G     greeter-messages-unique-host.apps.pez.pcfdemo.net


$ cf unmap-route greeter-messages apps.pez.pcfdemo.net --hostname greeter-messages-unique-host

$ cf apps

Getting apps in org test / space dev as admin...
OK

name                  requested state   instances   memory   disk   urls
greeter               started           1/1         1G       1G     greeter-forgiving-possum-rl.apps.pez.pcfdemo.net
greeter-messages      started           1/1         1G       1G     


```
### 2. redeploy greeter-messages 

Edit PROJECT_HOME/src/main/resources/application.yml


```
spring:
  profiles.active: dev
  application:
    name: greeter-messages
eureka:
  ## tells about the Eureka server details and its refresh time
  instance:
    non-secure-port-enabled: true  # uncomment to use 'apps.internal' domain on TAS.
    non-secure-port: 8080          # uncomment to use 'apps.internal' domain on TAS. do not uncomment on other env. it will chnage port 8080
    secure-port-enabled: false     # uncomment to use 'apps.internal' domain on TAS.

      
management:
  endpoints:
    web:
      exposure:
        include: '*'

#logging.level.root: debug

```
> eureka.instance.non-secure-port-enabled
> eureka.non-secure-port
> eureka.secure-port-enabled: false


Build the application
```
cd PROJECT_HOME
mvn clean package -DskipTests
```

redeploy app
```

cf d greeter-messages -f

$ vi manifest.yml
applications:
  - name: greeter-messages
    random-route: true
    instances: 1
    path: ./target/greeter-messages-0.0.1-SNAPSHOT.jar
    buildpacks:
      - java_buildpack_offline
    services:
      - greeter-service-registry
    routes:
    - route: greeter-messages.apps.internal

$ cf push 
```

###  3. set network-policy 

```
$ cf network-policies
Listing network policies in org myorg / space dev as user...

source   destination   protocol   ports

$ cf add-network-policy greeter --destination-app greeter-messages --protocol tcp --port 8080
Adding network policy to app greeter in org myorg / space dev as user...
OK

$ cf network-policies
Listing network policies in org myorg / space dev as user...

source    destination          protocol   ports
greeter   greeter-messages   tcp        8080

```

The Greeter app can now use container networking to access the Messages app via the Service Registry. For more information about configuring container networking, see Administering Container-to-Container Networking.


```

cf ssh greeter

vcap@3ce15889-a7aa-44f4-5cdb-2ae8:~$ curl http://greeter-messages.apps.internal:8080/greeting
{"message":"Hello, Bob!"}


```


### 4. Check Service Registry Dashboard.
the app `greeter-messages` should be registered on the service registry with greeter-messages.apps.internal in a few second.

### 5. check the app

```
$ cf apps

Getting apps in org test / space dev as admin...
OK

name                  requested state   instances   memory   disk   urls
greeter-messages      started           1/1         1G       1G     greeter-messages.apps.internal
greeter               started           1/1         1G       1G     greeter-forgiving-possum-rl.apps.pez.pcfdemo.net

```
on web-browser:
```
https://<TAS-APP-URL>/hello?salutation=HI&name=John

Hi, John!
```



*The End of this lab*

---

## Reference
- https://github.com/Tanzu-Solutions-Engineering/devops-workshop/blob/master/labs/05-adding_service_registration_and_discovery_with_spring_cloud.adoc
