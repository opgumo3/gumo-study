Server
- Main Node : 1대
- Worker Node : 2대

OS
- Ubuntu 22.04

Architecture
- amd64

Conatainer Runtime
- containerd : v2.1.4
- runc : 1.3.1
- cni plugin : 1.8.0

K8S Deployment Tool
- kubeadm

K8S Version
1.34

## ✔️ containerd

### (1) containerd 설치
```sh
$ wget https://github.com/containerd/containerd/releases/download/v2.1.4/containerd-2.1.4-linux-amd64.tar.gz -O ~/containerd-2.1.4-linux-amd64.tar.gz

$ tar Cxzvf /usr/local ~/containerd-2.1.4-linux-amd64.tar.gz
>> bin/
>> bin/containerd-stress
>> bin/ctr
>> bin/containerd
>> bin/containerd-shim-runc-v2

# systemd 로 실행하기 위해 containerd.service 다운로드
$ mkdir -p /usr/local/lib/systemd/system/

$ wget https://raw.githubusercontent.com/containerd/containerd/main/containerd.service -O /usr/local/lib/systemd/system/containerd.service

$ systemctl daemon-reload

# 부팅 시 시작
$ systemctl enable --now containerd
```

### (2) runc 설치
```sh
$ wget https://github.com/opencontainers/runc/releases/download/v1.3.1/runc.amd64 -O ~/runc.amd64
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
# 수정해야하는 옵션 아래 방향으로 검색
:/plugins.'io.containerd.cri.v1.runtime'.containerd.runtimes.runc.options
# n : 해당 방향으로 다음 결과 조회
# N : 역 방향으로 다음 결과 조회

# 아래 설정 추가 혹은 변경
SystemCgroup = true 

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

## ✔️ kubeadm 설치
### swap off
```sh
# 차근차근 버전
# swap 끔. 재부팅 전까지 유지
$ sudo swapoff -a

# 영구 적용
$ sudo vi /etc/fstab

# 아래와 같은 라인을 # 으로 주석처리
# /swap.img    none    swap    sw    0    0
# UUID=xxxx-xxxx   none   swap   sw   0  0

# 저장, 나가기
$ :wq

# ------ 해당 내용 없음 ------
# 아래 명령어로 현재 시스템에서 활성화된 swap 영역을 보여줌. 출력이 없다면 swap 없음.
$ swapon --show


# 바로바로 버전
$ swapoff -a && sed -i '/swap/s/^/#/' /etc/fstab
```

### 설치
```sh
# kubelet, kubeadm, kubectl 설치

sudo apt-get update

sudo mkdir -p -m 755 /etc/apt/keyrings

curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.34/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg

echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.34/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl

sudo systemctl enable --now kubelet
```

### Control Plane
```sh
kubeadm init --control-plane-endpoint "k8s-api.local:6443"
```
- `--control-plane-endpoint` : API 서버에 접근하는 통로를 하나로 통일. IP 나 DNS 를 사용할 수 있음.
    - kubeadm 에서는 이 옵션 없이 생성된 단일 Control Plane 클러스터는 이후에 HA 클러스터로 전환하는 것이 지원되지 않음.
    - 🤖 이후 endpoint 설정이나 변경은 인증서 재발행과 관련되어서 어렵다고 함.
    - 현재는 DNS 이름 사용하여 설정. Control Plane 과 Worker Node 의 /etc/hosts 에 아래처럼 등록
```
# /etc/hosts
[Control Plane IP] k8s-api.local
```

```sh
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```
- root 가 아닌 사용자가 kubectl 사용할 수 있도록 위 커맨드 실행

#### 👉 token 이 만료되었을 때
```sh
# 아래 명령어로 발급된 토큰과 만료를 확인할 수 있음.
kubeadm token list

# 토큰 새로 생성
kubeadm token create --print-join-command
```