
# Using an External Network File System (Volume Services)
[official guide](https://docs.pivotal.io/application-service/2-10/devguide/services/using-vol-services.html)

## Prerequisites
- [setup nfs server for dev](lab-nfs-setup-nfsserver-for-dev.md)

# Lab

### 1. Create nfs service instance
```
cf marketplace
service   plans      description
nfs       Existing   Service for connecting to NFS volumes

cf create-service nfs Existing SERVICE-INSTANCE-NAME -c '{"share":"SERVER/SHARE", "version":"NFS-PROTOCOL"}'

```
> SERVICE-INSTANCE-NAME is a name you provide for this NFS volume service instance.
> SERVER/SHARE is the NFS address of your server and share. Note: Ensure you omit the : that usually follows the server name in the address.
> (Optional) NFS-PROTOCOL is the NFS protocol you want to use. For example, if you want to use NFSv4, set the version to 4.1. Valid values are 3.0, 4.0, 4.1 or 4.2. If you do not specify a version, the protocol version used is negotiated between client and server at mount time. This usually results in the latest available version being used.

for example,
```
cf create-service nfs Existing nfs-edu1 -c '{"share":"192.168.160.201/home/ubuntu/nfsserver/edu1"}'
```
> Note: Ensure you omit the : that usually follows the server name in the address.
### 2. Push a sample app(pora)
```
git clone https://github.com/cloudfoundry/persi-acceptance-tests.git

cd ./persi-acceptance-tests/assets/pora
cf push pora --no-start
```

### 3. Bind to the App
```
cf bind-service YOUR-APP SERVICE-NAME -c '{"uid":"UID","gid":"GID","mount":"OPTIONAL-MOUNT-PATH","readonly":true, "cache:true}'
```
> YOUR-APP is the name of the app for which you want to use the volume service.
> SERVICE-NAME is the name of the volume service instance you created in the previous step.
> (Optional) UID and GID are the UID and GID to use when mounting the share to the app. The GID and UID must be positive integer values. Provide the UID and GID as a JSON string in-line or in a file. If you omit uid and gid, the driver skips mapfs mounting and performs just the normal kernel mount of the NFS file system without the overhead associated with FUSE mounts
> (Optional) cache: When you run the cf bind-service command, Volume Services mounts the remote file system with attribute caching disabled by default. You can enable attribute caching using default values by adding "cache":true to the bind configuration JSON string

for example, find the `uid` and `gid` from the NFS server
```
ubuntu@nfsserver:~/nfsserver$ id
uid=1000(ubuntu) gid=1000(ubuntu) groups=1000(ubuntu),4(adm),24(cdrom),27(sudo),30(dip),46(plugdev),116(lpadmin),126(sambashare)
```

and bind the service with the app.
```
cf bind-service pora nfs-edu1 -c '{"uid":"1000","gid":"1000","mount":"/opt/nfs"}'
```

### 4. Start your app
```
cf start pora
```

```
cf env pora
```

```
...
VCAP_SERVICES: {
  "nfs": [
    {
      "binding_guid": "acaa0f5e-330c-4c3b-aea6-31b929924734",
      "binding_name": null,
      "credentials": {},
      "instance_guid": "fae0b7ab-3e38-4f6f-a0b2-0b6c142d1631",
      "instance_name": "nfs-edu1",
      "label": "nfs",
      "name": "nfs-edu1",
      "plan": "Existing",
      "provider": null,
      "syslog_drain_url": null,
      "tags": [
        "nfs"
      ],
      "volume_mounts": [
        {
          "container_dir": "/opt/nfs",
          "device_type": "shared",
          "mode": "rw"
        }
      ]
    }
  ]
}


```

### 5. Test the Volume Service from Your App

The command writes a file to the share and then reads it back out again.
```
curl http://pora.YOUR-CF-DOMAIN.com/create
```
and check the files from the app and the nfs server
```
ubuntu@ubuntu18:~$ cf ssh pora

vcap@a389c83c-be5c-4c5e-468f-c8ec:/opt/nfs$ id
uid=2000(vcap) gid=2000(vcap) groups=2000(vcap)

vcap@a389c83c-be5c-4c5e-468f-c8ec:/opt/nfs$ ls -al
total 8
drwxr-xr-x 2 vcap vcap 4096 Jul 12 06:11 .
drwxr-xr-x 1 root root   17 Jul 12 06:02 ..
-rw-r--r-- 1 vcap vcap   24 Jul 12 06:09 poraQWcykINnsi

vcap@a389c83c-be5c-4c5e-468f-c8ec:/opt/nfs$ cat poraQWcykINnsi 
Hello Persistent World!


```
go to NFS server. and see the file created
```
ubuntu@nfsserver:~/nfsserver/edu1$ ls -al
total 12
drwxr-xr-x  2 ubuntu ubuntu 4096  7월 12 15:11 .
drwxr-xr-x 23 ubuntu ubuntu 4096  7월 12 13:55 ..
-rw-r--r--  1 ubuntu ubuntu   24  7월 12 15:09 poraQWcykINnsi

ubuntu@nfsserver:~/nfsserver/edu1$ cat poraQWcykINnsi 
Hello Persistent World!
```


*The End of this lab*

---


