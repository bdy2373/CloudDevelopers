# Setup NFS Server

## 1. Enable NFS service on TAS
https://docs.pivotal.io/application-service/2-13/operating/enable-vol-services.html


## 2. Setting up NFS service for test
connect to jumpbox via command line tool or Putty.
```
connect 
$ ssh ubuntu@JUMPBOX_IP
```

install nfs server
```
$ sudo apt install nfs-kernel-server
```

create  nfs folder
```
for i in {1..11}; do  mkdir -p /home/ubuntu/nfsserver/edu$i; done
```
```
chown -R ubuntu:ubuntu /home/ubuntu/nfsserver
```

set nfs configuration
```
$ sudo vi /etc/exports

# /etc/exports: the access control list for filesystems which may be exported
#		to NFS clients.  See exports(5).
#
# Example for NFSv2 and NFSv3:
# /srv/homes       hostname1(rw,sync,no_subtree_check) hostname2(ro,sync,no_subtree_check)
#
# Example for NFSv4:
# /srv/nfs4        gss/krb5i(rw,sync,fsid=0,crossmnt,no_subtree_check)
# /srv/nfs4/homes  gss/krb5i(rw,sync,no_subtree_check)
#
/home/ubuntu/nfsserver/edu1 *(rw,sync,no_root_squash,subtree_check)
```
> `/home/ubuntu/nfsserver`

restart nfs server

```
sudo systemctl status nfs-kernel-server
```
```
sudo systemctl status nfs-kernel-server
```
```
● nfs-server.service - NFS server and services
     Loaded: loaded (/lib/systemd/system/nfs-server.service; enabled; vendor preset: enabled)
     Active: active (exited) since Tue 2022-07-12 13:53:34 KST; 1h 21min ago
    Process: 168391 ExecStartPre=/usr/sbin/exportfs -r (code=exited, status=0/SUCCESS)
    Process: 168392 ExecStart=/usr/sbin/rpc.nfsd $RPCNFSDARGS (code=exited, status=0/SUCCESS)
   Main PID: 168392 (code=exited, status=0/SUCCESS)

 7월 12 13:53:33 ubuntu-virtual-machine systemd[1]: Starting NFS server and services...
 7월 12 13:53:34 ubuntu-virtual-machine systemd[1]: Finished NFS server and services.

```
*The End of this lab*

---


