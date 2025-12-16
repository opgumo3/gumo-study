# K8S 클러스터 설정

진행 시 입력했던 명령어를 위주로 정리한 내용입니다.

## 클러스터 정보
- 클러스터는 Control Plane 1, Worker 2로 총 3대의 인스턴스 사용하여 구성.
    - OS : Ubuntu 22.04
    - CPU 아키텍처 : amd64
- 컨테이너 런타임으로 containerd 사용.
    - containerd : v2.1.4
    - runc : 1.3.1
    - CNI Plugin : 1.8.0
- Deployment Tool 로 kubeadm 사용.
    - Client Version: v1.34.1
    - Kustomize Version: v5.7.1
- Network Add-on
    - Weave Net

## 설치 과정
1. 컨테이너 런타임 설치
2. IPv4 패킷 포워딩 허용
3. kubeadm 설치
4. Pod Network add-on 설치

# Commands
- 명령어는 기본적으로 모든 노드에서 실행하고 별도로 표시된 경우에는 해당 노드에서만 실행.

## 1. 컨테이너 런타임 설치

### (1) containerd 설치
```sh
# 1. 아키텍처에 맞는 containerd 파일 다운로드
$ wget https://github.com/containerd/containerd/releases/download/v2.1.5/containerd-2.1.5-linux-amd64.tar.gz -O ~/containerd-2.1.5-linux-amd64.tar.gz

# 2. 다운로드 한 파일을 /usr/local 에 압축 해제
$ tar Cxzvf /usr/local ~/containerd-2.1.5-linux-amd64.tar.gz

# 3. containerd 를 systemd 로 실행하기 위해 설정
# 3-1. .service 정의 파일 위한 디렉토리 생성
$ mkdir -p /usr/local/lib/systemd/system/

# 3-2. containerd.service 다운로드
$ wget https://raw.githubusercontent.com/containerd/containerd/main/containerd.service -O /usr/local/lib/systemd/system/containerd.service

# 3-3 서비스 갱신
$ systemctl daemon-reload

# 3-4. containerd 부팅 시 시작할 수 있도록 설정
$ systemctl enable --now containerd
```

### (2) runc 설치
```sh
# 1. 아키텍처에 맞는 runc 파일 다운로드
$ wget https://github.com/opencontainers/runc/releases/download/v1.3.1/runc.amd64 -O ~/runc.amd64

# 2. 파일을 /usr/local/sbin/runc 로 복사하고, 실행 권한(755)을 설정
$ install -m 755 runc.amd64 /usr/local/sbin/runc
```

### (3) CNI Plugin 설치
```sh
# 1. 디렉토리 생성
$ mkdir -p /opt/cni/bin

# 2. cni plugin 다운로드
$ wget https://github.com/containernetworking/plugins/releases/download/v1.8.0/cni-plugins-linux-amd64-v1.8.0.tgz

# 3. 2번의 파일을 1번에 압축 해제
$ tar Cxzvf /opt/cni/bin cni-plugins-linux-amd64-v1.8.0.tgz
```

### (4) cgroup 드라이버 설정
```sh
# kubelet 과 컨테이너 런타임이 같은 cgroup 드라이버 사용하도록 설정하기 위해
# containerd 의 cgroup 드라이버 설정.

# 1.
$ mkdir -p /etc/containerd

# 2.
$ containerd config default > /etc/containerd/config.toml

# 3.
$ vi /etc/containerd/config.toml

# 4. 옵션 변경
# 4-1. [ESC] 입력 후 : 부터 입력하여 수정해야하는 곳 검색
    # n : 해당 방향으로 다음 결과 조회
    # N : 역 방향으로 다음 결과 조회
:/plugins.'io.containerd.cri.v1.runtime'.containerd.runtimes.runc.options

# 4-2. 아래 설정 추가 혹은 변경
SystemdCgroup = true 

# 4-3. [ESC] > [:wq] > [ENTER] 입력하여 설정 저장

# 5. 재실행
$ systemctl restart containerd
```

## 2. IPv4 패킷 포워딩 허용
```sh
# 리눅스 커널은 기본적으로 ipv4 패킷을 인터페이스간 라우팅은 할 수 없음.
# 이를 가능하도록 설정 변경

# Kubernetes 설정에 필요한 sysctl 파라미터를 설정하고,
# 이 설정이 재부팅 이후에도 유지되도록 /etc/sysctl.d/k8s.conf 파일에 저장합니다.
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.ipv4.ip_forward = 1
EOF

# 시스템을 재부팅하지 않고 지금 바로 sysctl 설정을 적용합니다.
sudo sysctl --system

# 1로 설정되었는지 확인
sysctl net.ipv4.ip_forward
```

## 3. kubeadm 설치
### (1) swap off
두 개 중 하나의 방법으로 진행
```sh
# (추가) 아래 명령어로 현재 시스템에서 활성화된 swap 영역을 보여줌. 출력이 없다면 swap 없음.
# 나는 swap 이 없어서 아래 과정들은 진행하지 않았음.
$ swapon --show

# 방법 1
# 1-1. swap 끔. 재부팅 전까지 유지
$ sudo swapoff -a

# 1-2. 영구 적용
$ sudo vi /etc/fstab

# 1-3. 아래의 내용 라인을 # 으로 주석처리
# /swap.img    none    swap    sw    0    0
# UUID=xxxx-xxxx   none   swap   sw   0  0

# 1-4. 저장, 나가기
$ :wq

# 방법 2
$ swapoff -a && sed -i '/swap/s/^/#/' /etc/fstab
```

### (2) kubeadm, kubelet, kubectl 설치
```sh
sudo apt-get update

sudo mkdir -p -m 755 /etc/apt/keyrings

curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.34/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg

echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.34/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl

sudo systemctl enable --now kubelet
```

### (3) Control Plane 설정
- Control Plane 노드에서 실행합니다.
```sh
kubeadm init --control-plane-endpoint "k8s-api.local:6443"
```
- `--control-plane-endpoint` : API 서버에 접근하는 통로를 하나로 통일. 
    - IP 나 DNS name 값을 사용할 수 있음.
    - kubeadm 에서는 이 옵션 없이 생성된 단일 Control Plane 클러스터는 이후에 HA 클러스터로 전환하는 것이 지원되지 않음.
    - 🤖 또한 이후 endpoint 설정/변경은 인증서 재발행과 관련되어서 어렵다고 함.
    - 현재는 DNS 이름 사용하여 설정. 
    - Control Plane 과 Worker Node 의 /etc/hosts 에 아래처럼 등록
```
# /etc/hosts
[Control Plane IP] k8s-api.local
```

### (4) Worker Node 설정
- Control Plane 에서 `kubeadm init` 해서 출력된 join 문을 실행함.
```sh
kubeadm join k8s-api.local:6443 --token ... --discovery-token-ca-cert-hash ...
```

#### 👉 token 이 만료되었을 때
- Control Plane 에서 실행.
```sh
# 아래 명령어로 발급된 토큰과 만료를 확인할 수 있음.
kubeadm token list

# 토큰 새로 생성
kubeadm token create --print-join-command
```

### (5) 유저가 kubectl 사용 가능하도록.
```sh
# root 가 아닌 사용자가 kubectl 사용할 수 있도록 설정.
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

## 4. Pod Network add-on 설치 
- Control Plane 에서 실행
- Weave Net 사용.
```sh
kubectl apply -f https://reweave.azurewebsites.net/k8s/v1.34/net.yaml
```

#### 👉 `error validating ...` 오류 발생 시
- 실행한 사용자에 대해 kubectl 을 사용할 수 있도록 설정되어있어야함.