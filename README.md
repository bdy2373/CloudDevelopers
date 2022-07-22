# Tanzu Application Service (TAS, Cloud Foundry) Developer Workshop

### Course introduction
This program is focused on enabling customers by doing rather than talking. Students will gain a deep understanding of Tanzu Application Service (TAS) and will learn the fundamentals of Spring, Spring Boot and Spring Cloud Service.

### Learning Outcomes
- Demonstrate the ability to build greenfield, cloud native applications using Java, and Spring Boot
- Using TAS for web application deployments
- Discuss the pros and cons of moving a monolith to a microservice-based architecture
- Explain how to implement common distributed systems patterns to mitigate costs/limitations on an existing microservices-based codebase
- deliverable: course lab guide, sample source code. 

### Ice Breaking
introduction to
- name
- current role at your company.
- previous knowledge/skills on java, spring framework, spring boot, STS
- previous TAS experience
- expectation to this course


---
# Day 1
Understanding Cloud Foundry Basic features

### Lab Environment checking
- [lab prerequisites](lab-prerequisites-dev-env.md)

### TAS(Tanzu Application Service) Overview
- [cloud native app concepts(12 factor)](https://12factor.net/ko/config)
- slides: cloud foundry concepts: PaaS platform
- [videos](https://www.youtube.com/watch?v=Z2oghhwoEO0)
- [labs: apps-manager](lab-apps-manager.md)

### Publishing java app
- slides `stanging & running app`: how the staging process works, describe the nature and lifecycle of buildpacks, and explain how they determine the runtime configuration of applications
- [lab: building spring boot app locally](lab-app-developing-spring-boot-app.md)
- [lab: pushing app with `cf push`](lab-app-cf-push.md)
- [Traffic flow on TAS](https://docs.pivotal.io/application-service/2-11/adminguide/troubleshooting_slow_requests.html)
- [Diego Components and Architecture](https://docs.pivotal.io/application-service/2-11/concepts/diego/diego-architecture.html)
- [Deploying large app](https://docs.cloudfoundry.org/devguide/deploy-apps/large-app-deploy.html)

### Logging & metrics
- slides
- [Architecture](https://docs.cloudfoundry.org/loggregator/architecture.html)
- [lab: app logging and metrics](lab-logging-metrics.md)

### Resiliency
- slides
- [blog: 4 level HA](https://tanzu.vmware.com/content/blog/the-four-levels-of-ha-in-pivotal-cf)
- [labs: resiliency](lab-app-resiliency.md)

---
# Day 2
Using Backing Service and updating app

### SSH into app container
- [lab: cf-ssh](lab-cf-ssh.md)

### Backing services
- slides
- [lab: backing services](lab-services.md)

### Draining logs
- [lab: drain app log](lab-app-drain-log.md)

### Zero-Downtime app deploy 
- slides: ‘blue green’ deploy
- [lab: updating app](lab-app-updating.md) using revision history
- [lab: blue green deployment](lab-app-blue-green-deploy.md)

---
# Day 3
Running cloud native apps
- slides: microservices

### spring cloud config
- [lab: developing spring cloud config server locally ](lab-spring-cloud-config-local-server.md)
- [lab: building spring boot app with config](lab-app-developing-spring-boot-app-config.md)
- [lab: building spring cloud config client app to access local config server ](lab-spring-cloud-config-client-app.md)
- [lab: running app on TAS](lab-spring-cloud-config-on-TAS.md)

### spring cloud registry
- [lab: developing eureka-server locally](lab-spring-cloud-registry-local-server.md)
- [lab: developing backend app](lab-spring-cloud-registry-local-backend-app.md)
- [lab: developing frontend app](lab-spring-cloud-registry-local-frontend-app.md)
- [lab: running apps on TAS](lab-spring-cloud-registry-on-TAS.md)

### spring cloud gateway
- [lab: spring-cloud-gateway](lab-spring-cloud-gateway.md)

---
# Additional
Advanced topics including Persistent data and leveraging external CI/CD

### NFS volume mounts
- [lab: using nfs sevice](lab-nfs-using-nfs-service.md)

### session state clustering
- [labs: lab-session-state-caching using gemfire](lab-session-state-caching.md)

