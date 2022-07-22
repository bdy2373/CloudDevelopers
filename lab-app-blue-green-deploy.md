# Using Blue-Green Deployment for Zero-Downtime

Reference to [guide](https://docs.pivotal.io/application-service/2-10/devguide/deploy-apps/blue-green.html)

## Prerequisites
- we will re-use the app from [previous lab: building-spring-boot-app-config](lab-app-developing-spring-boot-app-config.md)

# Lab

### 1. cf push `blue` application

vi manifest-blue.yml
``` 
applications:
  - name: blue-edu1
    #random-route: true
    instances: 1
    path: ./target/cloud-native-spring-0.0.1-SNAPSHOT.jar
    buildpacks:
      - java_buildpack_offline
    routes:
      - route: service-edu1-apps.data.kr
      - route: blue-edu1.apps.data.kr
```
```
cf push -f manifest-blue.yml
```

### 2. cf push `green` application
manifest-green.yml
``` 
applications:
  - name: green-edu1
    #random-route: true
    instances: 1
    path: ./target/cloud-native-spring-0.0.1-SNAPSHOT.jar
    buildpacks:
      - java_buildpack_offline
    routes:
      - route: green-edu1.apps.data.kr

```
push and test the app
```
cf push -f manifest-green.yml
```

### 3. switch routes

```
watch -n 1 curl -k https://service-edu1.apps.data.kr/
```
```
cf map-route green-edu1  apps.data.kr --hostname service
cf unmap-route blue-edu1 apps.data.kr --hostname service
cf stop blue-edu1
```


now the new app is servicing from  `service-edu1-apps.data.kr`.


*Congratulations!* You’ve just completed your first Spring Boot application.

*The End of this lab*

---