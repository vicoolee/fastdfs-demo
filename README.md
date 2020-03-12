# fastdfs-demo  create by liwk-b

# maven com.github.tobato  
参考 https://github.com/tobato/FastDFS_Client 

#单机fastdfs 安装
1.安装docker
参考文档 
> https://www.runoob.com/docker/centos-docker-install.html
> https://www.cnblogs.com/hellxz/p/11044012.html

docker开机启动 systemctl start docker
2.拉取镜像
拉取docker镜像
docker pull delron/fastdfs    

构建tracker容器
docker run -d --network=host --name tracker -v /docker/fastdfs/tracker:/var/fdfs delron/fastdfs tracker    

构建storage容器，这里需要修改TRACKER_SERVER=192.168.1.56:22122中的IP地址为你的服务器本机对应ip地址
docker run -d --network=host --name storage -e TRACKER_SERVER=192.168.1.56:22122 -v /docker/fastdfs/storage:/var/fdfs -e GROUP_NAME=group1 delron/fastdfs storage 

3.开启防护墙端口   需开放端口 22122（tracker端口）和23000（storage端口）和8888（nginx端口）

查看所有打开的端口： firewall-cmd --zone=public --list-ports
增加端口访问 ：           firewall-cmd --permanent --zone=public --add-port=8888/tcp   
更新防火墙规则：        firewall-cmd --reload
安装firewalld   yum install firewalld firewall-config



#待完善 fastdfs 集群部署
1.集群部署

2.横向扩容 增加 storage group