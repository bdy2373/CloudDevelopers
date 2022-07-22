

# App Manifest Attribute
The app manifest is a method for applying bulk configuration to an app and its underlying processes in manifest.yml or cf cli options. Use cf push --help to determine how to push your app.

## reference
- https://docs.cloudfoundry.org/devguide/deploy-apps/manifest-attributes.html
- https://v3-apidocs.cloudfoundry.org/version/3.76.0/index.html#the-app-manifest-specification
```
cf push -f /PATH/manifest.yml
```

### manifest.yml
``` 
---
applications:
- name: my-app
  memory: 512M
  instances: 2
  path: /path/to/app/bits

  buildpacks:
  - buildpack_URL
  command: bundle exec rake VERBOSE=true
  disk_quota: 1024M

  health-check-type: port
  health-check-http-endpoint: /health

  no-route: true
  random-route: true
  routes:
  - route: example.com
  - route: www.example.com/foo
  - route: tcp-example.com:1234
  
  timeout: 180
  env:
    JAVA_OPTS: -Djava.security.egd=file:///dev/urandom
    JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+ }, memory_calculator: { stack_threads: 100 } }'
    MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: 'info,health,palTrackerFailure'
    MANAGEMENT_HEALTH_PROBES_ENABLED: true
    WELCOME_MESSAGE: Hello from the review environment
   
  services:
  - instance_ABC
  - instance_XYZ

```
> name: (required) application name to manage.

> memory: (optional) Use the memory attribute to specify the memory limit for all instances of an app. This attribute requires a unit of measurement: M, MB, G, or GB, in upper case or lower case. The default memory limit is 1G. The command line option that overrides this attribute is -m

> disk_quota: (optional) Default: 1024 MB. disk_quota attribute to allocate the disk space for your app instance. This attribute requires a unit of measurement: M, MB, G, or GB, in upper case or lower case. The command line option that overrides this attribute is -k

> instances: (optional) The default number of instances is 1. To ensure that platform maintenance does not interrupt your app, run at least two instances

> stack: (optional) The root filesystem to use with the buildpack, for example cflinuxfs3

> buildpacks: (required)
  - By name: MY-BUILDPACK.
  - By GitHub URL: https://github.com/cloudfoundry/java-buildpack.git.
  - By GitHub URL with a branch or tag: https://github.com/cloudfoundry/java-buildpack.git#v3.3.0 for the v3.3.0 tag

> path: (required) You can use the path attribute to tell Cloud Foundry the directory location where it can find your app. The directory specified as the path, either as an attribute or as a parameter on the command line, becomes the location where the buildpack Detect script executes. The command line option that overrides this attribute is -p

> command: (optional) optional. cf push my-app -c "bundle exec rake VERBOSE=true"

> health-check-type:  port, process or http. defaults to port. The command line option that overrides this attribute is -u.

> no-route: (optional) By default, cf push assigns a route to every app. But, some apps process data while running in the background and should not be assigned routes.
You can use the no-route attribute with a value of true to prevent a route from being created for your app
The command line option that overrides this attribute is --no-route.

> random-route: (optional) You can use the random-route attribute to generate a unique route and avoid name collisions

> routes: (optional) Use the routes attribute to provide multiple HTTP and TCP routes. Each route for this app is created if it does not already exist

> timeout: (optional) App start timeout maximum in second. Default: 60 seconds. Controls the maximum time that Cloud Foundry allows to elapse between starting an app and the first healthy response from the app. When you use this flag, the cf CLI ignores any app start timeout value set in the manifest. Value set in seconds
It is related to the health-check-type attribute. you can set it to any value up to the Cloud Controller’s cc.maximum_health_check_timeout property.
cc.maximum_health_check_timeout defaults to the maximum of 180 seconds, but your Cloud Foundry operator can set it to a different value.
* cf push -t TIMEOUT: (optional) App start timeout maximum
Default: 60 seconds. Controls the maximum time that Cloud Foundry allows to elapse between starting an app and the first healthy response from the app. When you use this flag, the cf CLI ignores any app start timeout value set in the manifest. Value set in seconds

> timeout(environment variable): (optional)  You configure Cloud Foundry Command Line Interface (cf CLI) staging, startup, and timeout settings to override settings in the manifest, as necessary:
- CF_STAGING_TIMEOUT: Controls the maximum time that the cf CLI waits for an app to stage after Cloud Foundry successfully uploads and packages the app. Value set in minutes. cf CLI environment variable
Default: 15 minutes
- CF_STARTUP_TIMEOUT: Controls the maximum time that the cf CLI waits for an app to start. Value set in minutes. cf CLI environment variable. Default: 5 minutes. cf -t ignores this variable.
  ```
  export CF_STAGING_TIMEOUT=60
  cf push myapp
  ```
> services: (optional) An array of service-instances to bind to the app. Apps can bind to services such as databases, messaging, and key-value stores. An app can only bind to services instances that exist in the target App Space before the app is deployed.

> env: A key-value hash of environment variables to be used for the app when running. valid for your application container only. 
> java version:  If you built the app with JDK 11 and want to deploy it you will need to make an additional entry in your manifest, just below `JAVA_OPTS`, add 
```
    JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+ } }'
```

## (optional) manifest variable files
You can use variables to create app manifests with values shared across all applicable environments in combination with references to environment-specific differences defined in separate files.

In addition, using variables enables you to store sensitive data in a separate file that the app manifest would reference, making the security sensitive data easier to manage and keep secure.

vars.yml
```
instances: 2
memory: 1G
```

manifest.yml

```
---
applications:
- name: test-app
  instances: ((instances))
  memory: ((memory))
  buildpacks:
  - go_buildpack
  env:
    GOPACKAGENAME: go_calls_ruby
  command: go_calls_ruby
```

```
cf push --vars-file /PATH/vars.yml


```

*The End of this lab*

---
