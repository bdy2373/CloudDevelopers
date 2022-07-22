# Running apps with the SCG(Spring cloud gateway) Instance on TAS
[official guide about Spring cloud gateway on TAS](https://docs.pivotal.io/spring-cloud-gateway/1-1//getting-started.html)

## Prerequisites
- [dev-env prerequisites](lab-prerequisites-dev-env.md)
- we will re-use the app from [previous lab: lab-spring-cloud-registry-local-frontend-app](lab-spring-cloud-registry-local-frontend-app.md)

# Lab

## Create a Spring cloud gateway Instance on TAS
[offical guide](https://docs.pivotal.io/spring-cloud-gateway/1-1/managing-service-instances.html)

### 1. create a gateway service with domain url: mygateway.apps.data.kr
```
$ cf target -o myorg -s development

$ cf create-service p.gateway standard my-gateway -c '{ "host": "mygateway", "domain": "apps.data.kr" }'

```

find dashboard url and access to the dashboard.

```
cf service my-gateway

Showing info of service my-gateway in org edu1 / space TEST as minseok...

name:             my-gateway
service:          p.gateway
tags:             
plan:             standard
description:      Spring Cloud Gateway for VMware Tanzu
documentation:    
dashboard:        https://mygateway.apps.data.kr/scg-dashboard
service broker:   scg-service-broker

Showing status of last operation from service my-gateway...

status:    create succeeded
message:   create service instance completed
started:   2021-06-07T02:22:59Z
updated:   2021-06-07T02:25:09Z

bound apps:

```
open https://mygateway.apps.data.kr/scg-dashboard for the dashboard. you will see the gateway routes setting.
 you need to allow the gateway app to access your uaa.

### 2. Testing the gateway.
bind an app with the gateway. and check on the dashboard.

optionally, you can reuse any app from the [complete sample app on github](https://github.com/myminseok/spring-cloud-sample)

```
cf bind-service cloud-native-spring my-gateway -c '{ "routes": [ { "path": "/menu/**" } ] }'
```

```
curl -k https://mygateway.apps.data.kr/menu/
```
check the output and modify the instance. please note that the gateway service instance will route all requests to the bound app with the path after stripping the first part of route (`StringPrefix=1` by default). here `https://mygateway.apps.data.kr/menu/**` will be `https://mygateway.apps.data.kr/**` at the backing app.

check the options from [the guide](https://docs.vmware.com/en/Spring-Cloud-Gateway-for-VMware-Tanzu/1.2/spring-cloud-gateway/GUID-configuring-routes.html)

```
cf unbind-service cloud-native-spring my-gateway
```
```
cf bind-service cloud-native-spring my-gateway -c '{ "routes": [ { "path": "/menu/**" ,"filters": ["StripPrefix=0"] } ] }'
```
```
curl -k https://mygateway.apps.data.kr/menu/
```


### 3. bind another apps to the gateway.

deploy `spring-music` app from previous [services lab](lab-services.md) and bind the app to the gateway.

```
cf bind-service spring-music my-gateway -c '{ "routes": [ { "path": "/music/**" } ] }'
```

```
curl -k https://mygateway.apps.data.kr/music
```



*The End of this lab*

---
## Additional tasks

### Cross-Origin Resource Sharing (CORS)
see [the guide](https://docs.pivotal.io/spring-cloud-gateway/1-1/managing-service-instances.html#cors) for more info.

```
cf bind-service cloud-native-spring my-gateway -c '{ "routes": [ { "path": "/menu/**" ,"filters": ["StripPrefix=0"] } ] , "cors": { "allowed-origins": [ "https://example.com" ]}'
```


### accessing to a isolation segment 
([the guide](https://docs.pivotal.io/spring-cloud-gateway/1-1/managing-service-instances.html#isolation-segments))

```
cf bind-service cloud-native-spring my-gateway -c '{ "routes": [ { "path": "/menu/**" ,"filters": ["StripPrefix=0"] } ] ,"isolation-segments": [ "my-isolation-segment" ] }'

```
