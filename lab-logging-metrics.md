# Logging and Metric system on TAS.
- Loggregator Architecture
[Architecture](https://docs.pivotal.io/application-service/2-10/loggregator/architecture.html)

## Tailing App Logs
Cloud Foundry allows developers and operators to stream logs or view recent logs.
- Use `cf logs --help` to determine how to view recent logs for your app
- Use `cf logs` command to stream logs for your app
- Access your app via url multiple times to see access logs
Note: You can exit streaming with CTRL-C or you can continue to stream and start a new terminal window.

```
cf logs spring-music

Connected, tailing logs for app spring-music in org example / space development as admin@example.com...

   2022-07-06T11:16:39.17+0900 [APP/PROC/WEB/0] OUT 2022-07-06 02:16:39.173  INFO 26 --- [nio-8080-exec-6] o.s.web.servlet.DispatcherServlet        : Completed initialization in 5 ms
   2022-07-06T11:16:39.38+0900 [RTR/1] OUT spring-music-excellent-rhinocerous-gr.apps.dhaka.cf-app.com - [2022-07-06T02:16:38.759809160Z] "GET / HTTP/2.0" 304 0 0 "-" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36" "14.34.56.123:62595" "10.0.4.15:61007" x_forwarded_for:"14.34.56.123" x_forwarded_proto:"https" vcap_request_id:"fa127386-81c3-4ad2-43d8-f497d4c31add" response_time:0.620847 gorouter_time:0.001602 app_id:"9534d542-d3a4-4c2c-b207-cbaa6aa174c0" app_index:"0" instance_id:"2cc7ff77-da63-4111-7ced-2591" x_cf_routererror:"-" x_b3_traceid:"288dd582fb272552150a78f876772c2d" x_b3_spanid:"150a78f876772c2d" x_b3_parentspanid:"-" b3:"288dd582fb272552150a78f876772c2d-150a78f876772c2d"

```

## Filtering Logs
To view some subset of log output, run cf logs APP-NAME in conjunction with filtering commands of your choice. Replace APP-NAME with the name of your app. In the example below, grep -v RTR excludes all Gorouter logs.
### windows
```
cf logs spring-music --recent | findstr APP

   2022-07-06T11:16:39.16+0900 [APP/PROC/WEB/0] OUT 2022-07-06 02:16:39.168  INFO 26 --- [nio-8080-exec-6] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
   2022-07-06T11:16:39.17+0900 [APP/PROC/WEB/0] OUT 2022-07-06 02:16:39.173  INFO 26 --- [nio-8080-exec-6] o.s.web.servlet.DispatcherServlet        : Completed initialization in 5 ms
    ...
```
### linux
```
cf logs spring-music --recent | grep APP

   2022-07-06T11:16:39.16+0900 [APP/PROC/WEB/0] OUT 2022-07-06 02:16:39.168  INFO 26 --- [nio-8080-exec-6] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
   2022-07-06T11:16:39.17+0900 [APP/PROC/WEB/0] OUT 2022-07-06 02:16:39.173  INFO 26 --- [nio-8080-exec-6] o.s.web.servlet.DispatcherServlet        : Completed initialization in 5 ms
    ...
```

## App Metric  & Metric Store
[Architecture](https://docs.pivotal.io/app-metrics/2-0/architecture.html)


## Tailing App Metrics
```
cf tail APP-NAME
```

## Querying via Prometheus-Compatible HTTP Endpoints (Metric Store API)

As a PAS developer, you can query metrics for applications you have access to. When querying with PromQL, you must specify the source_id label with the application guid as the label value. [official docs]( https://docs.pivotal.io/metric-store/1-5/using.html#using)

```
curl -H "Authorization: $(cf oauth-token)" -G "https://metric-store.SYSTEM-DOMAIN/api/v1/query" \
  --data-urlencode "query=cpu{source_id=\"$(cf app --guid your-app-name)\"}"
```
> metric: cpu, memory, http_total ...


```
$ curl -H "Authorization: $(cf oauth-token)" -G "https://metric-store.SYSTEM-DOMAIN/api/v1/query_range" \
    --data-urlencode 'query=http_latency{source_id="source_id_0"}' \
    --data-urlencode "start=$(date -d '24 hours ago' +%s)" \
    --data-urlencode "end=$(date +%s)" \
    --data-urlencode 'step=1h'
```
> 
- query is a Prometheus expression query string.
- start is a UNIX timestamp in seconds or RFC3339. (e.g. date -d '24 hours ago' +%s). Start time is inclusive. [start..end)
- end is a UNIX timestamp in seconds or RFC3339. (e.g. date +%s). End time is exclusive. [start..end)
- step is a query resolution step width in duration format or float number of seconds


## Log Ordering for Multi-line Java message issues

Diego uses a nanosecond-based timestamp that can be ingested properly by Integrated Log system.

Java app developers may want to convert stacktraces into a single log entity. To simplify log ordering for Java apps, use [Multi-line Java message workaround](https://github.com/cloudfoundry/loggregator-release/blob/main/docs/java-multi-line-work-around.md) to convert your multi-line stack traces into a single log entity. 

There is a work around involving modifing the Java log output to have your app reformat your stacktrace messages so any newline characters are replaced by a token; and then have your log parsing code replace that token with newline characters again. The following example is for the Java Logback library and ELK, but you may be able to use the same strategy for other Java log libraries and log aggregators.

With the Java Logback library you do this by adding %replace(%xException){'\n','\u2028'}%nopex to your logging config , and then use the following logstash conf.
```
# Replace the unicode newline character \u2028 with \n, which Kibana will display as a new line.
    mutate {
      gsub => [ "[@message]", '\u2028', "
"]
# ^^^ Seems that passing a string with an actual newline in it is the only way to make gsub work
    }
```
to replace the token with a regular newline again so it displays "properly" in Kibana.


*The End of this lab*

---

## Reference
- https://docs.cloudfoundry.org/devguide/deploy-apps/streaming-logs.html#overview

- App metric UI [official doc](https://docs.pivotal.io/app-metrics/2-0/architecture.html)
