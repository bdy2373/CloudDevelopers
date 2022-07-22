# Using NFS volume mount to app.
this lab refers to the blog: https://www.cloudfoundry.org/blog/install-scale-wordpress-cloud-foundry-2018/

###  download wordpress app app
```
curl -L -o wordpress-latest.zip https://wordpress.org/latest.zip
```
### prepare mysql instance
```
cf create-service p-mysql 100mb wordpress-mysql-db
```

## create NFS service
```
cf create-service nfs Existing wordpress-files -c ‘{“share”: “nfs_server/path/to/nfs/files”}
```

## edit wordpress


## push

```
cf push --no-start

cf bind-service wordpress wordpress-files -c ‘{“uid”: “1001”, “gid”: “1001”, “mount”: “/home/vcap/app/files”}’`

cf start wordpress
```

*The End of this lab*

---