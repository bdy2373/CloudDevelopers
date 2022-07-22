
# Deploy the app to TAS
[offical cf-cli doc](https://docs.cloudfoundry.org/cf-cli/getting-started.html#overview)

## Prerequisites
[lab: building-spring-boot-app](lab-app-developing-spring-boot-app.md)

# Lab

#### 1. Create an application manifest in the application folder.
- [app manifest reference](lab-cf-manifest.md)
- vi manifest.yml
```
applications:
- name: cloud-native-spring
  random-route: true
  instances: 1
  path: ./target/cloud-native-spring-1.0-SNAPSHOT.jar
  buildpacks: 
  - java_buildpack_offline

    
```
> path: application artifact path. 
>  gradle build => ./build/libs/cloud-native-spring-1.0-SNAPSHOT.jar
>  mvn package => ./target/cloud-native-spring-1.0-SNAPSHOT.jar


The above manifest entries will work with Java Buildpack 4.x series and JDK 8.  If you built the app with JDK 11 and want to deploy it you will need to make an additional entry in your manifest, just below `JAVA_OPTS`, add 
```
    JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+ } }'
```

#### 2. Target a foundation and login
[offical doc](https://docs.cloudfoundry.org/cf-cli/getting-started.html#overview)

```
cf api https://api.TAS-SYSTEM-DOMAIN-URL --skip-ssl-validation

cf login
```
or 
```
cf login -a https://api.TAS-SYSTEM-DOMAIN-URL -u USERNAME -p PASSWORD -o ORG -s SPACE
```

#### 3. Push application into Cloud Foundry
```
cf push
```

To specify an alternate manifest and buildpack, you could update the above to be e.g.

```
cf push -f manifest.yml -b java_buildpack
```

Assuming the offline buildpack was installed and available for use with your targeted foundation.  You can check for which buildpacks are available by executing
```
cf buildpacks
```
Find the URL created for your app in the health status report. Browse to your app's /hello endpoint.

#### 4. Testing the application app on TAS
open web browser and access url https://<APP-TAS-URL/
```
$ curl -k https://<APP-TAS-URL/
```


#### 5. Check the log output
```
$ cf logs cloud-native-spring
$ cf logs cloud-native-spring --recent
```


#### 6. cf env
```
cf env <app-name>
```
CF Environment Variables: https://docs.cloudfoundry.org/devguide/deploy-apps/environment-variable.html



*The End of this lab*

---
