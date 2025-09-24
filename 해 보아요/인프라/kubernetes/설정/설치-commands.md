Server
- Main Node : 1대
- Worker Node : 2대

Conatainer Runtime
- containerd : v2.1.4
- runc : 1.3.1
- cni plugin : 1.8.0

K8S Deployment Tool
- kubeadm

## ✔️ containerd

### (1) containerd 설치
```sh
$ wget https://github.com/containerd/containerd/releases/download/v2.1.4/containerd-2.1.4-linux-amd64.tar.gz -O containerd-2.1.4-linux-amd64.tar.gz

$ tar Cxzvf /usr/local/bin containerd-2.1.4-linux-amd64.tar.gz
>> bin/
>> bin/containerd-stress
>> bin/ctr
>> bin/containerd
>> bin/containerd-shim-runc-v2

# systemd 로 실행하기 위해 containerd.service 다운로드
$ wget https://raw.githubusercontent.com/containerd/containerd/main/containerd.service -o /usr/local/lib/systemd/system/containerd.service
$ systemctl daemon-reload
$ systemctl enable --now containerd # 부팅 시 시작
```

### (2) runc 설치
```sh
$ wget https://github.com/opencontainers/runc/releases/download/v1.3.1/runc.arm64
$ install -m 755 runc.amd64 /usr/local/sbin/runc
```

### (3) CNI Plugin 설치

```sh
$ mkdir -p /opt/cni/bin
$ wget https://github.com/containernetworking/plugins/releases/download/v1.8.0/cni-plugins-linux-arm64-v1.8.0.tgz
$ tar Cxzvf /opt/cni/bin cni-plugins-linux-arm64-v1.8.0.tgz
```


### (4) systemd cgroup driver 설정
```sh
$ mkdir -p /etc/containerd
$ containerd config default > /etc/containerd/config.toml

$ vi /etc/containerd/config.toml
:/plugins.'io.containerd.cri.v1.runtime'.containerd.runtimes.runc.options

# SystemCgroup = true 로 변경

$ systemctl restart containerd
```
`kubelet` 과 컨테이너 런타임이 같은 cgroup 드라이버를 사용하도록 하자.


## ✔️ IPv4 패킷 포워딩 허용
리눅스 커널은 기본적으로 ipv4 패킷을 인터페이스간 라우팅은 할 수 없음.

```
# sysctl params required by setup, params persist across reboots
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.ipv4.ip_forward = 1
EOF

# Apply sysctl params without reboot
sudo sysctl --system

# 1로 설정되었는지 확인
sysctl net.ipv4.ip_forward
```