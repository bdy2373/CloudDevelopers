# Port forwarding from windows to TAS
If  lab environment has connectivity issues from Local PC to TAS go router, then `port forwarding` as following.

## Network topology
1. Local PC(port-forwarding): https://apps.sys.data.kr (127.0.0.2:443 => proxy server:8443)
2. ubuntu jumpbox(TCP bypassing with nginx): 127.0.0.1:8443 -> https://TAS-GOROUTER:443 -> tas apps manager.

## on windows PC
1. run notepad.exe with Admin permission. 
2. and open C:\Windows\System32\drivers\etc\hosts
```
127.0.0.2 api.sys.data.kr
127.0.0.2 apps.sys.data.kr
127.0.0.2 login.sys.data.kr
127.0.0.2 uaa.sys.data.kr
127.0.0.2 log-cache.sys.data.kr
127.0.0.2 ssh.sys.data.kr
127.0.0.2 cloud-native-spring.apps.data.kr
```
3. forward traffic to ubuntu jumpbox
```
netsh interface portproxy add v4tov4 listenport=443 listenaddress=127.0.0.2 connectport=8443 connectaddress=192.168.150.#seat
```
```
netsh interface portproxy show v4tov4
```
```
netsh interface portproxy delete v4tov4 listenport=443 listenaddress=127.0.0.2
```

## on ubuntu jumpbox
setup traffic forwarding with nginx streaming. 
- install nginx complied with steam libaray: https://github.com/myminseok/nginx-stream

```
sudo su
git clone https://github.com/myminseok/nginx-stream
cd nginx-stream

mkdir -p /etc/nginx
cp ./etc/nginx/nginx.conf /etc/nginx/
cp ./lib/systemd/system/nginx.service /lib/systemd/system/
cp ./usr/sbin/nginx /usr/sbin/
mkdir -p /var/log/nginx
systemctl daemon-reload
systemctl restart nginx
systemctl status nginx
```



## on windows PC
 test access https://apps.sys.data.kr and cli
```
cf login -a api.sys.data.kr --skip-ssl-validation
```
```
API endpoint: api.sys.data.kr
Email:
```

