
# Adding spring cloud config client on your app
we will re-use the app from [previous lab: building-spring-boot-app-config](lab-app-developing-spring-boot-app-config.md)

optinally, you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-spring-cloud-config/complate)

## Prerequisites
- [app from previous lab](lab-app-developing-spring-boot-app-config.md)
- we will re-use the app from [previous lab: building-spring-boot-app-config](lab-app-developing-spring-boot-app-config.md)

# Lab
- optinally, you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-spring-cloud-config)


## Adding library
### 1. app template 
use spring initializr https://start.spring.io with additional dependencies
- [lab: building spring boot app locally](lab-app-developing-spring-boot-app.md)
- project metadata> name: cloud-native-spring
- dependencies: web, config-client(TAS), spring-security,  spring-boot-actuator

### 2. click `EXPLORER CTRL+SPACE` button and `copy` button.

### 3. paste the dependencies to the pom.xml 
- check the changes. reference to https://docs.vmware.com/en/Spring-Cloud-Services-for-VMware-Tanzu/3.1/spring-cloud-services/GUID-client-dependencies.html
- generate pom.yml depencency from https://start.spring.io

### 4. application.yml
```
spring:
  application:
    name: cloud-native-spring
  config:
    import: configserver:http://localhost:8888
	#import: optional:configserver:http://localhost:8888

management:
  endpoints:
    web:
      exposure:
        include: '*'

cook.special: "Kim Chi in cloud-native-spring app"
```
> management.endpoints.web.exposure: expose actuator endpoints for development purpose only.
>
https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_spring_cloud_config_client
https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#config-first-bootstrap


### Disable HTTP Basic Authentication(only for development)
The Spring Cloud Config Client starter has a dependency on Spring Security. Unless your app has other security configuration, this will cause all app endpoints to be protected by HTTP Basic authentication. you can disable by overriding WebSecurityConfigurerAdapter. 

- add dependency to pom.xml
```
	<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-config</artifactId>
			<version>5.3.9.RELEASE</version>
	</dependency>

```
- If starting app fails, then add @EnableWebSecurity. then add src/main/java/com/example/cloudnativespring/SecurityConfiguration.java.

```
***************************
APPLICATION FAILED TO START
***************************

Description:
  Parameter 0 of method managementSecurityFilterChain in org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration required a bean of type 'org.springframework.security.config.annotation.web.builders.HttpSecurity' that could not be found.
```

add src/main/java/com/example/cloudnativespring/SecurityConfiguration.java.
```
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Profile({"dev","cloud","prod","default"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests().anyRequest().permitAll()
        .and()
        .httpBasic().disable()
        .csrf().disable();
  }
}
```
> @EnableWebSecurity: key to resolve the issues.



## Build the application

Next we'll package the application as an executable artifact (that can be run on its own because it will include all transitive dependencies along with embedding a servlet container)
```
  mvn clean package -DskipTests
```

## Run and Test the application

Now we're ready to run the application
```
  mvn clean spring-boot:run -DskipTests


 .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::       (v2.3.11.RELEASE)

2021-06-04 18:44:59.897  INFO 21154 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at : http://localhost:8888
2021-06-04 18:45:00.511  INFO 21154 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=cloud-native-spring, profiles=[default], label=null, version=80ed78226fb8bcba2612c2118bff29c7bdf1d0bb, state=null
2021-06-04 18:45:00.512  INFO 21154 --- [           main] b.c.PropertySourceBootstrapConfiguration : Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/myminseok/spring-cloud-sample-configrepo/application.yml'}]
2021-06-04 18:45:00.517  INFO 21154 --- [           main] c.e.c.CloudNativeSpringApplication       : The following profiles are active: dev1
2021-06-04 18:45:00.995  INFO 21154 --- [           main] o.s.cloud.context.scope.GenericScope     : BeanFactory id=6f83338b-ffd3-3fb7-bda7-4b7076d95f8c
2021-06-04 18:45:01.176  INFO 21154 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2021-06-04 18:45:01.185  INFO 21154 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-06-04 18:45:01.185  INFO 21154 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.46]
2021-06-04 18:45:01.243  INFO 21154 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-06-04 18:45:01.243  INFO 21154 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 717 ms
```
> check the configuraton file list
> check the active profile

### Testing the application
```
curl http://localhost:8080/menu

Today's special is: Kim Chi(git-repo> application.yml)
```
Stop the application. In the terminal window type *Ctrl + C*

### Testing the application with different profile
re-run app with `dev` profile.

```
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev

curl http://localhost:8080/menu

Today's special is: Kim Chi(git-repo> application-dev.yml)
```


## Modifying the configuration on git repo

### 1. change file on config repo.
change 'application-dev.yml' on github.com/<YOUR-PROJECT>/spring-cloud-sample-configrepo.git

### 2. refresh client app  using actuator
```
curl -X POST http://localhost:8080/actuator/refresh

["cook.special"]
```

### 3. check configuraton changes on client app.
```
curl http://localhost:8080/menu

```
Stop the application. In the terminal window type *Ctrl + C*


*The End of this lab*

---
