# Building and Running Spring Cloud Config Server on your PC
[spring cloud eureka reference doc](https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html)

## Prerequisites
- [dev-env prerequisites](lab-prerequisites-dev-env.md)

# Lab

## Create a new app
- optinally, you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-spring-cloud-registry)

### 1. Build a app template on spring initializr 
use spring initializr https://start.spring.io with additional dependencies
- [lab: building spring boot app locally](lab-app-developing-spring-boot-app.md)
- project metadata> name: eureka-server
- dependencies: eureka-server,  spring-boot-actuator

### 2. Download as zip 
### 3. Open a Terminal (e.g., _cmd_ or _bash_ shell)
### 4. Unzip 
### 5. Open this project in your editor/IDE of choice.
### 6. Add configuration(application.yml)

vi eureka-server/src/main/resources/application.yaml.
```
spring:
  application:
    name: eureka-server 
server:
  port: 8761

eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false

```

## Develop app

### 1. Enable spring cloud config server on spring boot application
add @EnableEurekaServer annotation

```
package com.example.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```

### 2. Run spring cloud config server locally.
Now we're ready to run the application
```
mvn spring-boot:run
```
```
gradle bootRun
```
access http://localhost:8761 on web browser.


### 3. Run the apps 
We will build and run the application locally.
- run eureka-server app
- [build backend app](lab-spring-cloud-registry-local-backend-app.md)
- [build frontend app](lab-spring-cloud-registry-local-frontend-app.md)

access to the eureka-dashboard http://localhost:8761 on web browser and check if frontend and backend apps are registered

### 4. Testing apps on Local 
call frontend app. the message will comes from the backend app. on web-browser:
```
http://localhost:8080/hello?salutation=HI&name=John

Hi, John!
```

*The End of this lab*

---
