# CI/CD(Gitlab, Jenkins) 실습
check following tools and network connectivity

## Connectivity

#### Access gitlab UI 
http://192.168.150.191:18080/

#### Access jenkins UI   
http://192.168.150.191:28080/


## GitLab 실습

### 1. 프로젝트 생성(Git Bash 사용)
- sample app 다운로드 및 .git 삭제
```
git clone http://gitlab01.ds.global:18080/nh-cloud/jenkins.git
cd jenkins

rm -rf .git
```
- 프로젝트 생성 및 sample app push
```
 git init 
 git remote add origin http://192.168.150.191:18080/[본인 Group명]/jenkins
 git add .
 git commit -m "upload"
 > 안되는 경우 
  git config --global user.email "[계정]@data.kr"
  git config --global user.name "[계정]"
  git commit -m "upload"
  git push -u origin master
```
### 2. 프로젝트 생성 확인(Git UI)

## Jenkins 실습

### 1. Pipeline 생성(Jenkins UI)
- 새로운 item > Pipeline 이름 작성 > Pipeline 선택 > OK 버튼

### 2. Pipeline 작성(Jenkins UI)
- Git clone 
```
 stage('Preparation'){
  git branch: 'master', credentialsId: 'gitlab220721', url: 'http://gitlab01.ds.global:18080/[본인 Group명]]/jenkins.git'
 }
```

- sample app gradle build
```
 stage('build'){ 
  sh '/opt/gradle/gradle-6.3/bin/gradle clean build'
 }
```


- sample app 배포
(** manifest.yml 설정의 jar명 변경 **)
```
 stage('cfpush'){
  sh 'cf login -a https://api.sys.data.kr -u [계정]] -p [패스워드]] -o nhedu -s [본인 space명] --skip-ssl-validation'     
  sh 'cf push'
 }
```

- dir 삭제
```
 stage('deletedir'){
  deleteDir()
 }
```



