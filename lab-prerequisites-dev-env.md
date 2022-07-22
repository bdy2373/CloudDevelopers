# Prerequisites for Development environment. 
check following tools and network connectivity

## Connectivity
#### IP Assignment by Seat Number
- windows jumpbox: 192.168.xxx.#Seat (TBD)
- ubuntu jumpbox: 192.168.xxx.#Seat (TBD)
- Jenkins IP, Account: TBD 
- Gitlab IP, Account: TBD
- TAS apps manager id/password: edu#Seat / ---- (TBD)

#### Install VPN client
to connect from PC to lab envirionment.
- download client and install from https://www.fortinet.com/support/product-downloads
- Establish VPN connection 

#### Access to windows Jumpbox PC
- install microsoft remote desktop

#### Access ubuntu Jumpbox 
- for proxy, NFS testing, ...
- https://www.chiark.greenend.org.uk/~sgtatham/putty/latest.html
- putty: ssh ubuntu@UBUNTU_JUMPBOX_IP

#### Access gitlab UI 
http://GITLAB_IP:18080/

#### Access jenkins UI   
http://JENKINS_IP:28080/


## Tools

### 1. telnet for port checking
#### for windows
```
telnet 
```
#### for ubuntu
```
nc 
```

### 2. JDK
#### for Windows
- JDK 8: Please ensure you have a JDK installed and not just a JRE, JAVA_HOME
1. open https://github.com/ojdkbuild/ojdkbuild
2. download java-1.8.0-openjdk-1.8.0.332-1.b09.ojdkbuild.windows.x86_64.msi 
3. install. there might be security warning, click on `additional info` and click `execute`.
4. after installation, verify entries automatically registered to System VariablesWindows 
- File Explorer > right mouse click on `This PC`> Properties > Advanced System Settings > System variables > double click on `Path` > verify the entries beginning with `C:\Program Files\ojdkbuild\`
4. register `JAVA_HOME` system variable
- Windows File Explorer > right mouse click on `This PC`> Properties > Advanced System Settings > Environment Variables > System variables > New
```
JAVA_HOME: C:\Program Files\ojdkbuild\java-1.8.0-openjdk-1.8.0.332-1
```
> make sure the real path from C:\Program Files\ojdkbuild

6. check version
```
java -version
```

#### for Ubuntu OS
refer to https://openjdk.org/install/
```
sudo apt install openjdk-8-jdk -y
```
check version
```
java -version
openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-8u292-b10-0ubuntu1~18.04-b10)
OpenJDK 64-Bit Server VM (build 25.292-b10, mixed mode)
```

#### for Mac
```
brew tap AdoptOpenJDK/openjdk
brew install --cask adoptopenjdk8
/usr/libexec/java_home -v 1.8
```

vi ~/.bash_profile
```
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH
```


### 3. Git
git-for-windows provides `git` cli and `git bash`.

1. visit https://gitforwindows.org/
2. download `Git-2.37.0-64-bit.exe` and install it with all default options 
4. test git clone
```
git clone https://github.com/cloudfoundry-samples/spring-music

Cloning into 'spring-music'...
remote: Enumerating objects: 1344, done.
remote: Counting objects: 100% (12/12), done.
remote: Compressing objects: 100% (10/10), done.
remote: Total 1344 (delta 2), reused 10 (delta 1), pack-reused 1332
6 MiB/s
Receiving objects: 100% (1344/1344), 960.53 KiB | 1.43 MiB/s, done.
Resolving deltas: 100% (463/463), done.
```



### 4. Install IDE
source code editor
#### STS for windows
1. https://spring.io/tools
2. download `Spring Tools 4 for Eclipse` and extract
```
C:\Users\admin\Downloads> java -jar spring-tool-suite-4-4.15.1.RELEASE-e4.24.0-win32.win32.x86_64.self-extracting.jar
```
3. execute
```
C:\Users\admin\Downloads\sts-4.15.1.RELEASE>SpringToolSuite4.exe
```
4. set java compiler
- on Menu, Windows > perferences > Java > Installed JREs > Add > Standard VM
- browse to JDK directory: C:\Program Files\ojdkbuild\java-1.8.0-openjdk-1.8.0.332-1
- check the newly added JDK with checkbox.
- uncheck the existing `jre` from sts
- Finish
5. gradle env.
- on Menu, Windows > perferences > Gradle  > choose `Specific Gradle version`
- apply

6. import a sample app for testing
- import> Gracle > Existing Gradle Project > Project root directory to `C:\Users\Administrator\workspace\spring-music` and next > choose gradle >   finish.
- check any build error
- test java auto completion


#### (optional) VSCode
#### (optional) IntelliJ: https://www.jetbrains.com/ko-kr/idea/download/
- Intellij cloud foundry plugin: https://plugins.jetbrains.com/plugin/13854-cloud-foundry


### 5. Install Gradle
gradle v6+ installed
1. visit https://gradle.org/releases/ and download `binary only` (`gradle-7.4.2-bin.zip`)
2. `Extract all` to `C:\Users\Administrator\Documents`
3. register the `bin` path to System Variables
-  Windows File Explorer > right mouse click on `This PC`> Properties > Advanced System Settings > System variables > double click on `Path` 
```
C:\Users\Administrator\Documents\gradle-7.4.2\bin
```
> make sure the real path
4. check version
```
gradle --version

------------------------------------------------------------
Gradle 7.4.2
------------------------------------------------------------

Build time:   2022-03-31 15:25:29 UTC
Revision:     540473b8118064efcc264694cbcaa4b677f61041

Kotlin:       1.5.31
Groovy:       3.0.9
Ant:          Apache Ant(TM) version 1.10.11 compiled on July 10 2021
JVM:          1.8.0_332 (ojdkbuild 25.332-b09)
OS:           Windows 10 10.0 amd64

```
5. build sample app
go to the sample app

```
cd C:\Users\Administrator\workspace\spring-music
```
```
gradle clean assemble
```


### 6. Install maven
follow instruction from https://maven.apache.org/install.html
1. visit https://maven.apache.org/download.cgi and download `apache-maven-3.8.6-bin.zip`
3. `Extract all` to `C:\Users\Administrator\Documents`
4. register the `bin` path to System Variables
-  Windows File Explorer > right mouse click on `This PC`> Properties > Advanced System Settings > System variables > double click on `Path` 
```
C:\Users\Administrator\Documents\apache-maven-3.8.6\bin
```
> make sure the real path
4. check version
```
mvn --version

Apache Maven 3.8.6 (84538c9988a25aec085021c365c560670ad80f63)
Maven home: C:\Users\admin\Documents\apache-maven-3.8.6
Java version: 1.8.0_332, vendor: ojdkbuild, runtime: C:\Program Files\ojdkbuild\java-1.8.0-openjdk-1.8.0.332-1\jre
Default locale: en_GB, platform encoding: Cp1252
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```

5. build sample app
download sample app from spring Initializr
- java 1.8
- spring-boot
- maven
- download sample app template from start.spring.io: https://start.spring.io/starter.zip?name=demo&groupId=com.example&artifactId=demo-maven&version=0.0.1-SNAPSHOT&description=Demo+project+for+Spring+Boot&packageName=com.example.demo&type=maven-project&packaging=jar&javaVersion=1.8&language=java&bootVersion=2.6.9&dependencies=actuator&dependencies=web

- unzip or extract all
```
unzip demo-maven.zip
```
- execute
```
cd demo-maven
```

```
gradle clean assemble
```
```
mvn spring-boot:run
```
- access to http://localhost:8080/actuator/health



## TAS environment

### 1. Install cf cli for TAS
[offical doc](https://docs.cloudfoundry.org/cf-cli/v7.html)
1. download  cf cli from public doc or apps manager.
- cf cli v7 for TAS 2.10+
- https://github.com/cloudfoundry/cli/wiki/V7-CLI-Installation-Guide
- https://apps.sys.data.kr/tools
2. install cli
3. check version
```
C:\Users\Administrator>cf -v
cf version 7.5.0+0ad1d6398.2022-06-04
```


###  2. Verify DNS lookup 
In normal environment, DNS lookup for TAS should look like as following on windows jumpbox:
```
nslookup api.sys.data.kr
nslookup apps.sys.data.kr
nslookup login.sys.data.kr
nslookup uaa.sys.data.kr
nslookup log-cache.sys.data.kr
nslookup metric-store.sys.data.kr
nslookup cloud-native-spring.apps.data.kr
```
> should be resolved to LB -> goRouter VM.
```
nslookup ssh.sys.data.kr => 
```
> should be resolved to LB -> Diego Brain VM.

```
nslookup tcp.apps.data.kr 
```
> should be resolved to LB -> TCP router vm.

### 3. Verify access to TAS apps manager
- https://apps.sys.data.kr
- edu#Seat / ----

* if the lab environment has an issues accessing to apps manager, [setup ssh tunneling](lab-prerequisites-tas-appsman-ssh-tunneling.md) as a workaroiund.


### 4. Check cf cli connectivity

```
$ cf login -a API-URL -u USERNAME -p PASSWORD -o ORG -s SPACE --skip-ssl-validation
```
```
cf login -a api.sys.data.kr --skip-ssl-validation
```
```
API endpoint: api.sys.data.kr
Email:
```
> Email or Id: edu#Seat


### 5. Spring boot version
The Spring Cloud Services v3.1 tile is based on Spring Cloud
- [tile version info](https://docs.vmware.com/en/Spring-Cloud-Services-for-VMware-Tanzu/3.1/spring-cloud-services/GUID-client-dependencies.html) 
- Spring Cloud Release `2021.0.x`. we will use boot 2.6.* which is minimum suppored from spring initializr https://start.spring.io 


#### Additional materials
- [Cloud Foundry Cheat Sheet](http://www.appservgrid.com/refcards/refcards/dzonerefcards/rc207-010d-cloud-foundry.pdf)



