# Setup Lab Env (for Admin)
following lab-environment should be prepared by lab admin

### 1. IP Assignment by Seat Number(TBD)


| Instructor Desk |    |    |
|---|---|---|
|  1  |  2   |   3  |
|  4  |  5   |   6  |
|  8  |  9   |   10  |
|  11  |  12   |   13  |


| SEAT | TAS ID/gitlab/jenkins |  windows jumpbox | ubuntu jumpbox  | 
|------|-------|------------|-----------|
| 1     | edu1  |   192.168.160.188                  | 192.168.160.177 |
| 2     | edu2  |   192.168.160.189                  | 192.168.160.178 |
| ...    |        |                                    |                 |
| 10     | edu10  |   192.168.160.197                  | 192.168.160.186 |
| 11     | edu11  |   192.168.160.198                  | 192.168.160.187 |


### 2. Windows PC and  Windows Jumpbox for each student
#### network connectivity
- Physical PC: VPN client --> Windows Jumpbox VM(Lab network ) -> TAS

#### tools
- VPN client
- java(1.8)
- IDE 
- cf7 cli
- mysql cli
- chrome/firefox
- putty

### 3. Ubuntu jumpbox 
#### network connectivity
- ubuntu jumpbox <--> TAS network (Diego cell VM)
- ubuntu jumpbox <--> TAS network (loggregator system VM)

#### tools
- java
- cf7 cli
- [mysql cli](lab-services.md#4-option2-install-perconamysql-cli)
- nfs server for [lab setup nfs server for dev](lab-nfs-setup-nfsserver-for-dev.md)
- syslog server (centralized log server for App Log draining testing)

#### Syslog server on ubuntu jumpbox for App Log draining test.
on ubuntu jumpbox
```
systemctl status rsyslog
```
check configuration
```
cat  /etc/rsyslog.conf

# provides UDP syslog reception
module(load="imudp")
input(type="imudp" port="514")

# provides TCP syslog reception
module(load="imtcp")
input(type="imtcp" port="514")
```

### 4. TAS
```
opsmanager: 2.10.43
TAS: 2.11.21 
- diego cell capacity: 8cpu, 32GB ram, 256GB disk * 10 EA
- Diego brain IP: 10.100.1.101
- HA proxy router: 10.100.1.100
healthwatch 2.2.2
metric store: 1.5.4
app mertric 2.1.3
apps manager org quota: 10G=> diego cell capacity
service- gemfire 1.14.4 : 10 instances, latest
- dev-plan: 1cpu, 8gb mem, 8gb disk. 10gb persistent disk
service-mysql 2.10.7 :  - optinoal:  10 instances, latest
- db-small : 1cpu, 8gb mem, 8gb disk. 10gb persistent disk
spring cloud services: latest 3.1.37
- spring gateway, spring cloud service  org quota : 100GB 
- diego cell capacity: 8cpu, 32GB ram, 256GB disk * 10 EA
spring cloud gateway: latest, 1.2.3

```

Each TAS developer account

As TAS admin
```
cf create-user edu1
cf set-space-role edu1 test-org dev-space SpaceDeveloper
```

TAS uaa in opsman jumpbox.
```
uaac member add network.admin edu1
```

#### for loop
As TAS admin
```
for i in {1..11}; do  cf create-user edu$i; cf set-space-role edu$i test-org dev-space SpaceDeveloper; done
```

TAS uaa in opsman jumpbox.
```
for i in {1..11}; do  uaac member add network.admin edu$i; done
```

