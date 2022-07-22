# App Resiliency on TAS
Cloud Foundry contains [4 levels of recoverability](https://tanzu.vmware.com/content/blog/the-four-levels-of-ha-in-pivotal-cf). In this exercise, you will demonstrate the application recovery capability of the platform

### Healthcheck lifecycle
- app healthcheck while deploying: every 2 seconds 
- app healthcheck while deploying timeout: default 60 sec, max 180 second. set with `cf push -t`
- app healthcheck after container become healthy: every 30 second.
see https://docs.cloudfoundry.org/devguide/deploy-apps/healthchecks.html#health_check_timeout


# Lab

### 1. Push app
use sample app https://github.com/myminseok/articulate

[lab: compiling java app and pushing](lab-publishing-app-java.md)


### 2. Horizontal Scaling App
- Ensure you have two instances of your app running
- [official app scaling doc](https://docs.cloudfoundry.org/devguide/deploy-apps/cf-scale.html)
```
cf scale articulate -i 2
```
```
...
type:           web
sidecars:
instances:      1/2
memory usage:   1024M
     state      since                  cpu    memory         disk           details
#0   running    2022-07-06T06:28:58Z   0.3%   201.5M of 1G   144.5M of 1G
#1   starting   2022-07-06T06:31:53Z   0.0%   0 of 1G        81.2M of 1G
```


### 3. How to find out Diego Cell VM IP where the container is running on
get application guid.
```
cf app articulate --guid
```
find out the app guid from the result
```
$ cf curl /v2/apps/51e4d474-05a7-41f7-9ca3-8581b75571fa/stats
```

or all at once

```
cf curl /v2/apps/$(cf app articulate --guid)/stats
```
and filter out the Diego Cell VM IP
```windows
cf curl //v2/apps/$(cf app articulate --guid)/stats | findstr host
```
```ubuntu
cf curl /v2/apps/$(cf app articulate --guid)/stats | grep host
```

### 4. Auto-healing testing.
The `articulate` app has an endpoint that allows you to programmatically kill an instance. if you kill a container, then another instance will spin up in seconds by TAS automatically.

you can check the application container ID after recovered.

*The End of this lab*

---


