# Rolling(no downtime) App Deployments
- [official doc](https://docs.cloudfoundry.org/devguide/deploy-apps/rolling-deploy.html)    


## prerequisites
- cf cli v7+

# Lab

### 1. pushing app with no downtime
to monitor the rolling upgrade clearly, scale app more than 2 containers 
```
cf scale APP-NAME -i 2
cf push APP-NAME --strategy rolling
```
and open another terminal and monitor the rolling status
```
cf app APP-NAME
```

```
name:              spring-music
requested state:   started
routes:            spring-music.apps.data.kr
last uploaded:     Fri 08 Jul 17:48:51 KST 2022
stack:             cflinuxfs3
buildpacks:
        name                     version                                                                 detect output   buildpack name
        java_buildpack_offline   v4.49-offline-git@github.com:cloudfoundry/java-buildpack.git#5d5900c0   java            java

type:           web
sidecars:
instances:      2/2
memory usage:   1024M
     state     since                  cpu      memory         disk           details
#0   running   2022-07-08T08:50:03Z   1.4%     267.2M of 1G   169.2M of 1G
#1   running   2022-07-08T08:51:25Z   251.6%   251.9M of 1G   169.2M of 1G

type:           task
sidecars:
instances:      0/0
memory usage:   1024M
There are no running instances of this process.
```

```
name:              spring-music
requested state:   started
routes:            spring-music.apps.data.kr
last uploaded:     Fri 08 Jul 17:48:51 KST 2022
stack:             cflinuxfs3
buildpacks:
        name                     version                                                                 detect output   buildpack name
        java_buildpack_offline   v4.49-offline-git@github.com:cloudfoundry/java-buildpack.git#5d5900c0   java            java

type:           web
sidecars:
instances:      2/2
memory usage:   1024M
     state     since                  cpu    memory         disk           details
#0   running   2022-07-08T08:50:53Z   1.3%   270.7M of 1G   170.2M of 1G
#1   running   2022-07-08T08:51:15Z   1.2%   256.3M of 1G   170.2M of 1G

type:           web
sidecars:
instances:      0/1
memory usage:   1024M
     state      since                  cpu    memory   disk     details
#0   starting   2022-07-08T08:54:41Z   0.0%   0 of 0   0 of 0

type:           task
sidecars:
instances:      0/0
memory usage:   1024M
There are no running instances of this process.

```

```
...
type:           web
sidecars:
instances:      1/1
memory usage:   1024M
     state     since                  cpu    memory         disk           details
#0   running   2022-07-08T08:50:53Z   1.4%   270.7M of 1G   170.2M of 1G

type:           web
sidecars:
instances:      1/2
memory usage:   1024M
     state      since                  cpu    memory         disk           details
#0   running    2022-07-08T08:55:03Z   0.0%   210.3M of 1G   169.2M of 1G
#1   starting   2022-07-08T08:55:06Z   0.0%   0 of 0         0 of 0

type:           task

```


```
name:              spring-music
requested state:   started
routes:            spring-music.apps.data.kr
last uploaded:     Fri 08 Jul 17:48:51 KST 2022
stack:             cflinuxfs3
buildpacks:
        name                     version                                                                 detect output   buildpack name
        java_buildpack_offline   v4.49-offline-git@github.com:cloudfoundry/java-buildpack.git#5d5900c0   java            java

type:           web
sidecars:
instances:      2/2
memory usage:   1024M
     state     since                  cpu      memory         disk           details
#0   running   2022-07-08T08:55:03Z   1.4%     267.2M of 1G   169.2M of 1G
#1   running   2022-07-08T08:55:25Z   251.6%   251.9M of 1G   169.2M of 1G

type:           task
sidecars:
instances:      0/0
memory usage:   1024M
There are no running instances of this process.
```


### 2. restart app with no downtime
```
cf restart APP-NAME --strategy rolling
```

### 3. stop app deployment
```
cf cancel-deployment APP-NAME
```


### 4. App Revision
Rolling back to a previous revision: This allows you to deploy a version of the app that you had running previously without needing to track that previous state yourself or have multiple apps running. When you create a deployment and reference a revision, the revision deploys as the current version of your app.
to Roll Back to a Previous Revision , use Apps Manager UI. see [official doc](https://docs.pivotal.io/application-service/2-10/devguide/revisions.html)


*The End of this lab*

---



### View the Status of Rolling Deployments  in details

Find the GUID of your app by running
```
cf app APP-NAME --guid
```
Find the deployment for that app by running. 

WARNING: do not use `git bash` provided by `git-for-windows` as it malfunctions. use `cmd` termal on windows.
```
cf curl GET /v3/deployments?app_guids=APP-GUID&status_values=ACTIVE
```
> Where APP-GUID is the GUID of the app. Deployments are listed in chronological order, with the latest deployment displayed as the last in a list

```
cf curl GET /v3/deployments?app_guids=(cf app spring-music --guid)&status_values=ACTIVE
```

Get deployment status
```
cf curl GET /v3/deployments/DEPLOYMENT-GUID
```
> status.value: Indicates if the deployment is ACTIVE or FINALIZED.

> status.reason: Provides detail about the deployment status.

> status.details: Provides the timestamp for the most recent successful health check. The value of the status.details property can be nil if there is no successful health check for the deployment. For example, there might be no successful health check if the deployment was cancelled
