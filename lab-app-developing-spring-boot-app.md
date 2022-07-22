# Building a Spring Boot Application
In this lab we'll build and deploy a simple https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/[Spring Boot] application to Cloud Foundry whose sole purpose is to reply with a standard greeting.

## Prerequisites
[dev-env prerequisites](lab-prerequisites-dev-env.md)

# Lab
- optinally, you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-building-spring-boot-app/)

##  Build a app template
#### 1. Build a app template on spring initializr 
While we could visit spring initializr https://start.spring.io to create a new Spring Boot project, we will start with a skeleton
- project: mvn or gradle
- language: java
- spring boot: 2.6.* : [dev-env prerequisites](lab-prerequisites-dev-env.md)
- project metadata> name: cloud-native-spring
- project metadata> packaging: jar
- project metadata> java: 8
- dependencies: web, actuator
or download from this link:  https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.6.9&packaging=jar&jvmVersion=1.8&groupId=com.example&artifactId=cloud-native-spring&name=cloud-native-spring&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.cloud-native-spring&dependencies=actuator,web

#### 2. Download as zip 
#### 3. Open a Terminal (e.g., _cmd_ or _bash_ shell)
#### 4. Unzip 
#### 5. Open this project in your editor/IDE of choice.


## Develop the app

#### 1. Add an Endpoint
Within your editor/IDE complete the following steps:

- Create a new class named MyController under the package com.example.cloudnativespring;
 package
- edit MyController.java

```
package com.example.cloudnativespring;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MyController {

    @GetMapping("/")
    public String hello() {
        return "Hello World!";
    }

}
```

#### 2. Build the application
Return to the Terminal session you opened previously and make sure your working directory.  we'll package the application as an executable artifact (that can be run on its own because it will include all transitive dependencies along with embedding a servlet container)
```
  gradle clean build
  gradle clean build --exclude-task test

  mvn clean package
  mvn clean package -DskipTests
  
```

#### 3. Run the application
Now we're ready to run the application
```
  gradle bootRun
  mvn spring-boot:run
```

You should see the application start up an embedded Apache Tomcat server on port 8080 (review terminal output):
```
.   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::       (v2.3.11.RELEASE)

2021-06-01 16:55:54.795  INFO 61076 --- [           main] c.e.c.CloudNativeSpringApplication       : Starting CloudNativeSpringApplication on kminseok-a01.vmware.com with PID 61076 (/Users/minseok/_dev/spring-cloud-sample/cloud-native-spring/target/classes started by minseok in /Users/minseok/_dev/spring-cloud-sample/cloud-native-spring)
2021-06-01 16:55:54.797  INFO 61076 --- [           main] c.e.c.CloudNativeSpringApplication       : The following profiles are active: dev
2021-06-01 16:55:55.414  INFO 61076 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2
'''

2021-06-01 16:56:01.893  INFO 61076 --- [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2021-06-01 16:56:01.899  INFO 61076 --- [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 6 ms

```

#### 4. Testing the application on local PC
open web browser and access url http://localhost:8080/
or
```
curl http://localhost:8080/
```


##### Using Curl
Edit the curl command below with your information. 

```
  curl  http://<YOUR-APP-URL>/
```

##### Using WebBrowser

(optional)  If you use chrome, you might want to install a JSON formatter like JSONView: https://chrome.google.com/webstore/detail/jsonview/chklaanhfefbnpoihckbnefhakgolnmc

#### (optional) Using Postman
install postman: https://www.getpostman.com/. On the Builder page, you will set up your request.


#### 5. Stop the application. 
In the terminal window type *Ctrl + C*


## Deploy the app on TAS
We've built and run the application locally.  Now we'll deploy it to Cloud Foundry. 

#### 1. Target and login on TAS
```
cf api https://api.TAS-SYSTEM-DOMAIN-URL --skip-ssl-validation

cf login
```
or 
```
cf login -a https://api.TAS-SYSTEM-DOMAIN-URL -u USERNAME -p PASSWORD -o ORG -s SPACE
```

#### 2. Push application into Cloud Foundry
Use cf push --help to determine how to push your app. Be sure to do the following:
- Name your app. space scope
- Push the jar file by providing the ‘-p’ flag pointing to the jar file on your laptop
- put unique-app-domain-name by providing '-n' flag. 
- Create one instance(default)
- Allocate 750M of memory
- Use the java_buildpack buildpack
- Use --random-route to avoid route collisions. It appends two random words to the name of your app as a dev tool to help avoid route collisions. DO NOT use this in production. 
- app route is TAS scope.

```
cf push APP_NAME -p /PATH/to/the/jar  -n <unique-app-domain-name> -b java_buildpack_offline

```
*Congratulations!* You’ve just completed your first Spring Boot application.

*The End of this lab*

---

## Reference
- https://github.com/Tanzu-Solutions-Engineering/devops-workshop/blob/master/labs/01-building_a_spring_boot_application.adoc
- 

## more sample apps
- https://github.com/pivotal-education/pcf-articulate-code
- https://github.com/spring-projects/spring-petclinic
- https://github.com/myminseok/articulate
- https://github.com/myminseok/attendee-service
