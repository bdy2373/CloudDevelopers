# Draining app logs to out side of platform


## prepare the Log Management Service
Cloud Foundry uses the syslog URL to route messages to the service. The syslog URL has a scheme of syslog, syslog-tls, or https, and can include a port number. For example:
```
syslog://logs.example.com:1234
```
> ELK, Splunk, syslog server 


## Draining app logs with General cf CLI Service Commands

### Single app with user-provided-service

```
$ cf create-user-provided-service DRAIN-NAME -l SYSLOG-URL
$ cf bind-service YOUR-APP-NAME DRAIN-NAME
```

### Single app with drain plugin
```
$ cf drain --help
NAME:
   drain - Creates a user provided service for syslog drains and binds it to a given application.

USAGE:
   drain <app-name> <syslog-drain-url> [options]

OPTIONS:
   --drain-name         The name of the drain that will be created. If excluded, the drain name will be `cf-drain-UUID`.
   --type               The type of logs to be sent to the syslog drain. Available types: `logs`, `metrics`, and `all`. Default is `logs`

```

### Draining All apps logs in a space with drain plugin
To bind a drain to all apps in a space with a single command, you must use the CF Drain CLI Plugin as described in the previous section.
- install cf plugin: To install the CF Drain CLI plugin, see the Installing Plugin instructions in the plugin source repository on GitHub.(https://github.com/cloudfoundry/cf-drain-cli#installing-plugin
)
- create drain app
```
cf drain-space --help
NAME:
   drain-space - Pushes app to bind all apps in the space to the configured syslog drain.

USAGE:
   drain-space SYSLOG_DRAIN_URL [--drain-name NAME] [--path PATH] [--type TYPE]

OPTIONS:
   --drain-name       Name for the space drain.
   --path             Path to the space drain app to push. If omitted the latest release will be downloaded.
   --type             Which log type to filter on (logs, metrics, all). Default is all.
```
After a short delay, logs begin to flow automatically.

*The End of this lab*

---