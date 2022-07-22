# Build a frontend application ( greeter)
- [spring cloud eureka reference doc](https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html)
- [TAS official guide](https://docs.pivotal.io/spring-cloud-services/3-1/common/client-dependencies.html)

## Prerequisites
- [dev-env prerequisites](lab-prerequisites-dev-env.md)

# Lab

## Create a new app
- optinally, you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-spring-cloud-registry)

### 1. Build a app template on spring initializr 
use spring initializr https://start.spring.io with additional dependencies
- [lab: building spring boot app locally](lab-app-developing-spring-boot-app.md)
- project metadata> name: `greeter`
- dependencies: web, reactive web, spring-security, service-registry(TAS), spring-boot-actuator

### 2. `GENERATE as zip`
### 3. Open a Terminal (e.g., _cmd_ or _bash_ shell)
### 4. Unzip 
```
unzip greeter.zip
```
### 5. Open this project in your editor/IDE of choice.

## Develop app

### 1. check PROJECT_HOME/pom.xml

### 2. Enable Discovery Client
- add @EnableDiscoveryClient annotation to CloudNativeSpringApplication.java
```
@SpringBootApplication
@EnableDiscoveryClient
public class GreeterApplication {

	@Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

	@Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

	public static void main(String[] args) {
		SpringApplication.run(GreeterApplication.class, args);
	}

}
```

### 3. Build some logic

add GreeterController.java
```
...
@RestController
public class GreeterController {

    private GreeterService greeterService;

    public GreeterController(GreeterService greeterService){
        this.greeterService = greeterService;
    }

    @RequestMapping(value = "/")
    public String home(){
        return this.hello("hello", "default");
    }


    @RequestMapping(value = "/hello")
    public String hello(@RequestParam(value = "salutation", defaultValue = "Hello") String salutation,
                        @RequestParam(value = "name", defaultValue = "Bob") String name) {
        Greeting greeting = greeterService.greetRestTemplate(salutation, name);
        return greeting.getMessage();
    }


    @RequestMapping("/hello-webclient")
    public Mono<String> helloWebclient(@RequestParam(value = "salutation", defaultValue = "Hello") String salutation,
                                       @RequestParam(value = "name", defaultValue = "Bob") String name) {

        return greeterService.greetWebClient(salutation, name);
    }

}
```

add GreeterService.java

```
@Service
public class GreeterService {


    private final RestTemplate rest;

    private WebClient.Builder webClient;

    private static final String BACKEND_SERVICE="greeter-messages";

    private static final String URI_TEMPLATE = UriComponentsBuilder.fromUriString("http://"+BACKEND_SERVICE+"/greeting")
            .queryParam("salutation", "{salutation}")
            .queryParam("name", "{name}")
            .build()
            .toUriString();

    public GreeterService(RestTemplate restTemplate, WebClient.Builder webClient) {
        this.rest = restTemplate;
        this.webClient= webClient;
    }

    public Greeting greetRestTemplate(String salutation, String name) {
        Assert.notNull(salutation, "salutation is required");
        Assert.notNull(name, "name is required");

        return rest.getForObject(URI_TEMPLATE, Greeting.class, salutation, name);
    }

    public Mono<String> greetWebClient(String salutation, String name) {
        Assert.notNull(salutation, "salutation is required");
        Assert.notNull(name, "name is required");

        StringBuffer uri = new StringBuffer().append("/greeting?salutation=").append(salutation).append("&name=").append(name);

        return webClient.baseUrl("http://"+BACKEND_SERVICE)
                .build().get()
                .uri(uri.toString())
                .retrieve().bodyToMono(String.class);

    }

}

class Greeting {

    private final String message;

    @JsonCreator
    public Greeting(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
```
> make sure to import proper jsckson dependency:  
import com.fasterxml.jackson.annotation.JsonCreator; import com.fasterxml.jackson.annotation.JsonProperty;


### 4. [Disable HTTP Basic Authentication(only for development)](lab-spring-cloud-config-client-app.md#2-disable-http-basic-authenticationonly-for-development)


### 5. Edit PROJECT_HOME/src/main/resources/application.yml
Set the spring.application.name property in application.yml.

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
      defaultZone: http://127.0.0.1:8761/eureka/

management:
  endpoints:
    web:
      exposure:
        include: '*'

#logging.level.root: debug

```


### 6. Run the application
Now we're ready to run the application
```
  cd PROJECT_HOME
  
  gradle bootRun

  mvn spring-boot:run
```

### 7. Testing the application on local PC
on web browser: 

```
http://localhost:9002/hello?salutation=HI&name=John

Hi, John!
```

*The End of this lab*

---
