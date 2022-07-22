
# Running apps with Gemfire on TAS

- [official guide](https://docs.pivotal.io/p-cloud-cache/1-13/index.html)
- you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-session-state-caching)
- this guide references the [spring boot session state sample app](https://github.com/gemfire/spring-for-apache-geode-examples/tree/main/session-state)


## Prerequisites
- [dev-env prerequisites](lab-prerequisites-dev-env.md)
- we will re-use the apps from [lab: lab-spring-cloud-registry-server-local](lab-spring-cloud-registry-server-local.md)
# Lab

## Create a Service instance on TAS
https://docs.pivotal.io/p-cloud-cache/1-13/create-instance.html

### 1. create a service instance

```
$ cf target -o myorg -s development

$ cf marketplace -s p-cloudcache

Getting service plan information for service p-cloudcache as minseok...
OK

service plan   description        free or paid
extra-small    Plan Description   free
small          Plan Description   free
dev-plan       Plan Description   free


cf create-service p-cloudcache dev-plan my-cloudcache


cf service my-cloudcache
```


### 2. find dashboard url and access to the dashboard url
```
cf service my-cloudcache

name:             my-cloudcache
service:          p-cloudcache
tags:             
plan:             dev-plan
description:      VMware Tanzu GemFire(version "1.13.1-build.31", Apache Geode version
                  "gemfire/apache-geode-1.13.2.tgz") offers the ability to deploy a VMware GemFire cluster as a
                  service in VMware Tanzu Application Service for VMs.
documentation:    http://docs.pivotal.io/p-cloud-cache/
dashboard:        https://cloudcache-3f30656a-44e1-4048-8178-63f3bc6977a9.sys.data.kr/pulse
service broker:   cloudcache-broker

Showing status of last operation from service my-cloudcache...

status:    update succeeded
message:   

```

### 3. access to the dashboard and check the automatically created region `ClusteredSpringSessions`

https://docs.vmware.com/en/VMware-Tanzu-GemFire-for-VMs/1.14/tgf-vms/GUID-content-accessing-instance.html


create service key on gemfire cluster
```
$ cf help -a | grep key
   create-service-key                     Create key for a service instance
   service-keys                           List keys for a service instance
   service-key                            Show service key info
   delete-service-key                     Delete a service key

$ cf create-service-key my-cloudcache test-key
Creating service key test for service instance my-cloudcache as minseok...


$  cf service-key my-cloudcache test-key
Getting key test for service instance my-cloudcache as minseok...

{
 "distributed_system_id": "0",
 "gfsh_login_string": "connect --url=https://cloudcache-3f30656a-44e1-4048-8178-63f3bc6977a9.sys.data.kr/gemfire/v1 --user=cluster_operator_d9xMuZZbHu6hGw8ziK65Ag --password=m2Nqe2SbMGF6Raq27cHqQ --skip-ssl-validation",
 "locators": [
  "ed489b85-471f-468f-8104-a4e9f40947d8.locator-server.cmp-net.service-instance-3f30656a-44e1-4048-8178-63f3bc6977a9.bosh[55221]"
 ],
...
```
copy `gfsh_login_string` string.

## Develop sample app.
- optinally, you can reference the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample/tree/master/lab-session-state-caching/complete)

### 1. app template 
put the same param on spring initializr https://start.spring.io with additional dependencies. 
- project metadata> name: session-state-caching
- dependencies: web, spring-geode-starter, spring-boot-actuator, spring-geode-starter-session (it is not in start.spring.io. add to pom.xml manually)

### 2. Download as zip 
### 3. Open a Terminal (e.g., _cmd_ or _bash_ shell)
### 4. Unzip 
### 5. Open this project in your editor/IDE of choice.

## Develop the app
### 1. paste the dependencies to the PROJECT_HOME/pom.xml 
`spring-geode-starter-session` is not in start.spring.io. add manually to pom.xml
```
  <properties>
		<java.version>1.8</java.version>
		<springGeodeVersion>1.4.0</springGeodeVersion>
	</properties>
  ...
                <dependency>
			<groupId>org.springframework.geode</groupId>
			<artifactId>spring-geode-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.geode</groupId>
			<artifactId>spring-geode-starter-session</artifactId>
		</dependency>
  </dependencies>
    ...
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.geode</groupId>
				<artifactId>spring-geode-bom</artifactId>
				<version>${springGeodeVersion}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
    ...
```


### 2. Enable Gemfire Configuration
Edit SessionStateCachingApplication.java
```
import org.springframework.geode.config.annotation.EnableClusterAware;

@SpringBootApplication
@EnableClusterAware
public class SessionStateCachingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionStateCachingApplication.class, args);
	}

}

```

### 3. Add an Endpoint
SessionTest.java

```
package com.example.cloudnativespring;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SessionTest {

	@GetMapping("/getSessionNotes")
	public List<String> getSessionNotes(HttpServletRequest request) {
		List<String> notes = (List<String>) request.getSession().getAttribute("NOTES");
		return notes;
	}

	@PostMapping("/addSessionNote")
	public void addSessionNote(@RequestBody String note, HttpServletRequest request) {
		List<String> notes = (List<String>) request.getSession().getAttribute("NOTES");

		if (notes == null) {
			notes = new ArrayList<>();
		}

		notes.add(note);
		request.getSession().setAttribute("NOTES", notes);
	}

	@PostMapping("/invalidateSession")
	public void invalidateSession(HttpServletRequest request) {
		request.getSession(false).invalidate();
	}

}

```

### 4. check app configuration PROJECT_HOME/src/main/resource/application.yml

```
spring:
  application:
    name: cloud-native-spring
  main:
    allow-bean-definition-overriding: true
  profiles.active: dev

server.port: 8080


management:
  endpoints:
    web:
      exposure:
        include: '*'


```
 
### 5. Build the application
```
  cd PROJECT_HOME
  mvn spring-boot:run
```
check UI with
- http://localhost:8080/actuator/mappings


## Deploy the app to TAS


### 1. create manifest.yml
[lab-app-cf-push](lab-app-cf-push.md)

```
cd PROJECT_HOME
mvn clean package -DskipTests
```

edit  PROJECT_HOME/manifest.yml
```
applications:
  - name: session-state-caching
    random-route: true
    instances: 1
    path: ./target/session-state-caching-0.0.1-SNAPSHOT.jar
    buildpacks:
      - java_buildpack_offline
    services:
      - my-cloudcache
```

```
cf push
```

## Testing the app 

### 1. create a session and set data.
use following command. this example doesn't work with web browser.
```
curl -k https://session-state-caching.apps.data.kr/addSessionNote -H "Content-type: application/json" -d 'data1' -X POST -c session.txt
```
> -c option will save the session info to "session.txt" file

check the SESSION ID from the previous command. 

```
$ cat session.txt

# Netscape HTTP Cookie File
# http://curl.haxx.se/docs/http-cookies.html
# This file was generated by libcurl! Edit at your own risk.

#HttpOnly_session-state-caching.apps.data.kr	FALSE	/	TRUE	0	SESSION	NThhODEwOTAtZDVhZS00NDhiLThiZDUtZGYxY2IwMmQxNTBl

```
### 2. get the session data
```
curl -k https://session-state-caching.apps.data.kr/getSessionNotes -H "cookie: SESSION=NThhODEwOTAtZDVhZS00NDhiLThiZDUtZGYxY2IwMmQxNTBl"

["data1"]
```
### 3. restart the app

```
cf restart session-state-caching

```

### 4. check the session data is presisted on the gemfire cluster.

```
$ curl -k https://session-state-caching.apps.data.kr/getSessionNotes -H "cookie: SESSION=NThhODEwOTAtZDVhZS00NDhiLThiZDUtZGYxY2IwMmQxNTBl"

["data1"]
```

## troubleshooting creating region in gemfire cluster


### 1. create service key on gemfire cluster

```
$ cf help -a | grep key
   create-service-key                     Create key for a service instance
   service-keys                           List keys for a service instance
   service-key                            Show service key info
   delete-service-key                     Delete a service key

$ cf create-service-key my-cloudcache test-key
Creating service key test for service instance my-cloudcache as minseok...


$  cf service-key my-cloudcache test-key
Getting key test for service instance my-cloudcache as minseok...

{
 "distributed_system_id": "0",
 "gfsh_login_string": "connect --url=https://cloudcache-3f30656a-44e1-4048-8178-63f3bc6977a9.sys.data.kr/gemfire/v1 --user=cluster_operator_d9xMuZZbHu6hGw8ziK65Ag --password=m2Nqe2SbMGF6Raq27cHqQ --skip-ssl-validation",
 "locators": [
  "ed489b85-471f-468f-8104-a4e9f40947d8.locator-server.cmp-net.service-instance-3f30656a-44e1-4048-8178-63f3bc6977a9.bosh[55221]"
 ],
...
```
copy `gfsh_login_string` string.


### 2. get the service instance url.
```
cf service my-cloudcache

name:             my-cloudcache
service:          p-cloudcache
tags:             
plan:             dev-plan
description:      VMware Tanzu GemFire(version "1.13.1-build.31", Apache Geode version
                  "gemfire/apache-geode-1.13.2.tgz") offers the ability to deploy a VMware GemFire cluster as a
                  service in VMware Tanzu Application Service for VMs.
documentation:    http://docs.pivotal.io/p-cloud-cache/
dashboard:        https://cloudcache-3f30656a-44e1-4048-8178-63f3bc6977a9.sys.data.kr/pulse
```
copy the dashboard domain url.

### 3. ssh turnneling to gemfire cluster via app.
open another terminal and run following command. refer to [lab-cf-ssh](lab-cf-ssh.md)
```
$ cf ssh -L 443:cloudcache-3f30656a-44e1-4048-8178-63f3bc6977a9.sys.data.kr:443 cloud-native-spring

vcap@e8dd18f9-724d-4c2e-5d56-9d74:~$ 
```
now, turnnel to the gemfire cluster is ready on the terminal session 

### 4. download gfsh cli
 https://docs.pivotal.io/p-cloud-cache/1-13/accessing-instance.html#establish-https> `Source [TGZ]`> click `TGZ` 

```
wget https://apache.org/dyn/closer.cgi/geode/1.13.2/apache-geode-1.13.2.tgz
tar xf apache-geode-1.13.2.tgz

```

### 5. connect gemfire cluster from gfash via ssh turnnel.
 set  JAVA_HOME env variable and run gfsh cli.
```
$ cd apache-geode-1.13.2/bin
$ ./gfsh


    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  / 
 / /__/ / ____/  _____/ / /    / /  
/______/_/      /______/_/    /_/    1.13.2

Monitor and Manage Apache Geode
gfsh>

```
### 6. paste the connection string from the service key. 
and press enter until you see prompt of `Cluster-0 gfsh>`

```
gfsh> connect --url=https://cloudcache-3f30656a-44e1-4048-8178-63f3bc6977a9.sys.data.kr/gemfire/v1 --user=cluster_operator_d9xMuZZbHu6hGw8ziK65Ag --password=m2Nqe2SbMGF6Raq27cHqQ --skip-ssl-validation
key-store: 
key-store-password: 
key-store-type(default: JKS): 
trust-store: 
trust-store-password: 
trust-store-type(default: JKS): 
ssl-ciphers(default: any): 
ssl-protocols(default: any): 
ssl-enabled-components(default: all): 
Successfully connected to: GemFire Manager HTTP service @ https://cloudcache-3f30656a-44e1-4048-8178-63f3bc6977a9.sys.data.kr/gemfire/v1

You are connected to a cluster of version: 1.13.2
```

### 7. check and create a ClusteredSpringSessions region
https://docs.pivotal.io/p-cloud-cache/1-13/spring-session.html
```

Cluster-0 gfsh>list regions
List of regions
------------------------


Cluster-0 gfsh> create region --name=ClusteredSpringSessions --type=PARTITION_HEAP_LRU
                     Member                      | Status | Message
------------------------------------------------ | ------ | --------------------------------------------------------
cacheserver-ed489b85-471f-468f-8104-a4e9f40947d8 | OK     | Region "/ClusteredSpringSessions" created on "cacheser..

Cluster configuration for group 'cluster' is updated.


Cluster-0 gfsh>list regions
List of regions
------------------------
ClusteredSpringSessions


# Cluster-0 gfsh> destroy region --name ClusteredSpringSessions 
```
*The End of this lab*

---
## Additonal materials
### tomcat app for session state caching on gemfire
- guide: https://docs.pivotal.io/p-cloud-cache/1-13/Spring-SessionState.html#tomcat
- guide: https://docs.pivotal.io/p-cloud-cache/1-13/session-caching.html
- sample: https://github.com/pivotal-cf/http-session-caching

### sample app for data caching on gemfire
- https://docs.pivotal.io/p-cloud-cache/1-13/example-applications.html

- https://tanzu.vmware.com/application-modernization-recipes/replatforming/offload-http-sessions-with-spring-session-and-redis

# Advanced topics
- enable single instance upgrade: https://docs.pivotal.io/p-cloud-cache/1-12/upgrade.html#enable-individual-upgrades
- limitation: https://docs.pivotal.io/p-cloud-cache/1-12/usage.html
- backup: https://docs.pivotal.io/p-cloud-cache/1-12/backupandrestore.html
- compaction: https://gemfire.docs.pivotal.io/910/geode/managing/disk_storage/compacting_disk_stores.html#compacting_disk_stores
