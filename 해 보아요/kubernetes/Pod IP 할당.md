```
Network provider : Flannel
Container Runtime : containerd
```
Pod 에는 고유한 IP 주소가 할당됨.

### 다른 호스트의 컨테이너간 통신
- 패킷이 vxlan 을 통해 다른 호스트의 컨테이너로 이동하고, UDP 패킷으로 캡슐화됨.

### Reference
- https://ronaknathani.com/blog/2020/08/how-a-kubernetes-pod-gets-an-ip-address/#disqus_thread