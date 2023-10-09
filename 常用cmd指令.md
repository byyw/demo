# 请求链路

tracert www.baidu.com

# 端口映射

netsh interface portproxy show v4tov4
netsh interface portproxy add v4tov4 listenaddress=192.168.0.131 listenport=3306 connectaddress=192.168.0.128 connectport=3306
netsh interface portproxy delete v4tov4 listenaddress=192.168.0.131 listenport=3306

netsh interface portproxy add v4tov4 listenport=7612 connectaddress=47.114.165.221 connectport=7000
netsh interface portproxy delete v4tov4 listenport=7612

# 端口占用查询

netstat -ano | findstr xxxx
tasklist|findstr "9088"
taskkill /T /F /PID 9088

# 回环路由

route add 192.168.1.77 mask 255.255.255.255 192.168.1.1
route delete 192.168.1.77 mask 255.255.255.255 192.168.1.1
