
# Apps Manager
[official doc](https://docs.pivotal.io/application-service/2-11/operating/console-login.html)

## Prerequisites
- apps manager URL: https://apps.SYSTEM-DOMAIN
  > Open a browser and navigate to apps.SYSTEM-DOMAIN, where SYSTEM-DOMAIN is the system domain you retrieved in the previous step. For example, if the system domain is system.example.com, then point your browser to apps.system.example.com
- username/password

## Managing Orgs and Spaces Using Apps Manager
https://docs.pivotal.io/application-service/2-11/console/manage-spaces.html

## Managing User Roles with Apps Manager
https://docs.pivotal.io/application-service/2-11/console/console-roles.html

# Lab


### 1. create user in TAS
* TAS admin permission is required for this command
```
cf create-user
```


### 2. add user to org/space
* TAS org `OrgManager` permission is required for this command
```
cf set-org-role
cf set-space-role
```


### 3. Invite New Users
create a user in TAS and set org/space role in Apps Manager UI.
* TAS org `OrgManager` permission is required for this command

## Permission
https://docs.pivotal.io/application-service/2-11/console/dev-console.html#permissions


*The End of this lab*

---