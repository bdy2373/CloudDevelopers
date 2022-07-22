# Adding Custom Config class to Spring Boot App
We will re-use the app from [previous lab: developing-spring-boot-app](lab-app-developing-spring-boot-app.md)

# Lab
- optinally, you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-building-spring-boot-app/)

### 1. Add MyConfig class 
Within your editor/IDE complete the following steps:

- Create a new class named MyConfig under the package com.example.cloudnativespring;
 package and edit: cloud-native-spring/src/main/java/com/example/cloudnativespring/MyConfig.java
```
package com.example.cloudnativespring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Component
@RefreshScope
public class MyConfig {

	private String special;

	public MyConfig(@Value("${cook.special:none}") String special) {
		this.special = special;
	}

	public String getSpecial() {
		return special;
	}

}
```

### 2. Add RequestMapping 
add a request mapping to the MyController class: cloud-native-spring/src/main/java/com/example/cloudnativespring/MyController.java

```
package com.example.cloudnativespring;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MyController {

	private final MyConfig menu;

	public MyController(MyConfig menu) {
		this.menu = menu;
	}

    @GetMapping("/")
    public String hello() {
        return "Hello World!";
    }


	@GetMapping("/menu")
	public String restaurant() {
		return String.format("Today's special is: %s", menu.getSpecial());
	}

}
```

### 3. application.yml
add a configuration file: cloud-native-spring/src/main/resources/application.yml

```
spring:
  application.name: cloud-native-spring
  profiles.active: dev

server.port: 8080


management:
  endpoints:
    web:
      exposure:
        include: '*'


cook.special: "Kim Chi in cloud-native-spring app"
```

### 4. Build the application
we'll package the application as an executable artifact (that can be run on its own because it will include all transitive dependencies along with embedding a servlet container)
```
  gradle clean build
  mvn clean package
```

### 5. Run the application
Now we're ready to run the application
```
  gradle bootRun
  mvn spring-boot:run
```

### 6. Testing the application on local PC
open web browser and access url https://localhost:8080/menu

or
```
curl http://localhost:8080/menu

Today's special is: Kim Chi in cloud-native-spring app%

```
Stop the application. In the terminal window type *Ctrl + C*

*The End of this lab*

---
