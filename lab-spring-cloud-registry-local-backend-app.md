# Build a new backend application (greeter-messages)
- [spring cloud eureka reference doc](https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html)
- [TAS official guide](https://docs.pivotal.io/spring-cloud-services/3-1/common/client-dependencies.html)

## Prerequisites
- [dev-env prerequisites](lab-prerequisites-dev-env.md)

# Lab

## Create a new app
- optinally,  you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-spring-cloud-registry)

### 1. Build a app template on spring initializr 
use spring initializr https://start.spring.io with additional dependencies
- [lab: building spring boot app locally](lab-app-developing-spring-boot-app.md)
- project metadata> name: `greeter-messages`
- dependencies: web, reactive web, spring-security, service-registry(TAS), spring-boot-actuator

### 2. `GENERATE as zip`
### 3. Open a Terminal (e.g., _cmd_ or _bash_ shell)
### 4. Unzip 
```
unzip greeter-messages.zip
```
### 5. Open this project in your editor/IDE of choice.

## Develop app

### 1. check PROJECT_HOME/pom.xml

### 2. Enable Discovery Client
add @EnableDiscoveryClient annotation to GreeterMessagesApplication.java
```
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class GreeterMessagesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreeterMessagesApplication.class, args);
	}

}


```
### 3. Build some logic
create MessagesController.java

```
package com.example.greetermessages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesController {

	private Log log = LogFactory.getLog(MessagesController.class);

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "salutation", defaultValue = "Hello") String salutation, @RequestParam(value = "name", defaultValue = "Bob") String name) {
		log.info(String.format("Now saying \"%s\" to %s", salutation, name));
		return new Greeting(salutation, name);
	}

}

class Greeting {

	private static final String template = "%s, %s!";

	private String message;

	public Greeting(String salutation, String name) {
		this.message = String.format(template, salutation, name);
	}

	public String getMessage() {
		return this.message;
	}

}

```

### 4. [Disable HTTP Basic Authentication(only for development)](lab-spring-cloud-config-client-app.md#2-disable-http-basic-authenticationonly-for-development)


### 5. Edit PROJECT_HOME/src/main/resources/application.yml
Set the spring.application.name property in application.yml.

```
server.port: 9001

spring:
  application:
    name: greeter-messages
  profiles.active: dev

eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:8761/eureka/

management:
  endpoints:
    web:
      exposure:
        include: '*'

```

### 6. Run the app
Now we're ready to run the application. [check your local eureka server is running](lab-spring-cloud-registry-local-server.md)
```
  cd PROJECT_HOME

  mvn spring-boot:run
```

### 7. Test the app on local PC

on web browser: 
```
http://localhost:9001/greeting?salutation=hi&name=J

{"message":"hi, J!"}%

```


*The End of this lab*

---

- original sample code https://github.com/myminseok/spring-cloud-sample/blob/master/lab-spring-cloud-registry/complete/greeter-messages/pom.xml


