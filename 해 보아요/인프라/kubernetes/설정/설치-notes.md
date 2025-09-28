# container runtime
- ✅ 컨테이너를 실제로 실행하고 관리하는 소프트웨어
- 파드를 실행할 수 있도록 클러스터의 각 노드에는 컨테이너 런타임을 설치해야함.
- **Kubernetes 1.34** 버전부터는, **Container Runtime Interface(CRI)** 규격을 지킨 런타임만 쓸 수 있음

아래의 컨테이너 런타임을 사용할 수 있음.
- containerd
- CRI-O
- Docker Engine (cri-docrkd 어댑터 사용)
- Mirantis Container Runtime

## container runtime 비교
- 🤖 AI 검색 내용 기반
- https://kubernetes.io/blog/2020/12/02/dont-panic-kubernetes-and-docker/ 참고

containerd
- CNCF 프로젝트에서 관리, Docker에서 런타임 부분만 분리된 것.
	- **Docker**는 내부적으로는 여러 기능이 합쳐져 있음: 이미지 관리, 컨테이너 실행/중지, 네트워킹, 스토리지
	- 이 중에서 “컨테이너 실행/중지” 기능을 담당하는 부분이 바로 **런타임(runtime)**
- OCI(Open Container Initiative) 표준 준수.
- 경량, 안정성 높음.

CRI-O
- Red Hat 주도로 개발된 런타임.
- 단순하고 가볍고, SELinux, AppArmor 등 리눅스 보안 기능과 잘 통합됨
- Red Hat이 만든 Kubernetes 기반 PaaS인 OpenShift 의 기본 런타임.

Docker Engine (직접 지원 X)
- 도커는 쿠버네티스 내부에 포함(embedded)되도록 설계되지 않았음.
	- 도커는 CRI 를 준수하지 않아 Dockershim 이라는 다른 도구를 사용해야함.
    - ✅ K8S 에서 정의한 컨테이너 런타임과 통신하기 위한 표준 API. kubelet이 컨테이너 런타임에 명령을 보낼 때 쓰는 인터페이스
- Kubernetes 1.24부터 dockershim 이 제거되어 직접 사용 불가.
- Mirantis + Docker 커뮤니티에서 개발한 cri-docrkd 어댑터를 사용하여 사용할 수 있음.

# cgroup driver 설정
🤖 cgroup driver 를 설정한다는 것은?

👉 cgroup 이란?
- **control group**
- 리눅스 커널 기능으로 CPU, 메모리, IO 같은 자원을 프로세스 단위로 제한/측정/격리할 수 있음.
- cgroup 을 이용하여 K8S 에서 Pod 실행할 때 "CPU 2개, 메모리 1Gi" 이런 제한을 걸 수 있음.

👉 cgroup driver 란?
- kubelet이나 containerd 같은 프로그램이 cgroup 기능을 **어떤 방식으로 제어할지 정하는 인터페이스**.
- 대표적으로 두 가지 있음:

1. **cgroupfs**
    - 커널이 제공하는 cgroup 파일 시스템을 직접 접근
	    - container runtime이 직접 `/sys/fs/cgroup` 건드리기 → `cgroupfs`
    - kubelet 의 기본 cgroup 드라이버
    - 시스템의 init이 systemd일 경우 권장되지 않음.
        - → systemd는 시스템 전체에 단일 cgroup 관리자를 기대하기 때문.
    - cgroup v2를 사용할 경우, cgroupfs 대신 systemd 드라이버를 사용해야 함
2. **systemd**
    - systemd(리눅스 init 시스템)가 cgroup을 관리하도록 맡김
	    - systemd한테 “이 프로세스 자원 관리 좀 해줘” 부탁하기 → `systemd`
    - kubelet과 컨테이너 런타임이 systemd cgroup driver를 쓰도록 설정해야, 리소스 관리가 일관되고 안정적
    - KubeletConfiguration 에서 kubelet 의 driver 설정을 아래의 예시처럼 변경할 수 있으나, kubeadm v1.22 이상부터는 기본이 systemd
```
apiVersion: kubelet.config.k8s.io/v1beta1
kind: KubeletConfiguration
...
cgroupDriver: systemd
```

- K8S 1.34 부터는 KubeletCgroupDriverFromCRI라는 feature gate가 추가.
- 이 기능이 켜져 있고, 런타임이 RuntimeConfig CRI RPC 를 지원한다면,
    - kubelet이 직접 드라이버를 설정하지 않고, 컨테이너 런타임이 사용하는 cgroup driver를 자동 감지

# Deployment Methods
쿠버네티스 클러스터 자체를 설치·구성하는 도구

1. kubeadm
    - Kubernetes 공식 제공 도구.
    - 단일 클러스터를 빠르게 설치/구성하는 데 초점.

2. Cluster API (CAPI)
    - Kubernetes의 하위 프로젝트
    - 선언적 API와 도구를 제공해 여러 Kubernetes 클러스터를 쉽게 프로비저닝(생성), 업그레이드, 운영할 수 있도록 함.

3. kOps
    - 자동화된 클러스터 프로비저닝 도구.

4. Kubespray:
    - Ansible 플레이북, 인벤토리, 프로비저닝 도구, 그리고 범용 OS/Kubernetes 클러스터 구성 관리 작업에 필요한 도메인 지식을 모아둔 구성체. 
    - 내부적으로 kubeadm을 사용하지만, 복잡한 과정을 자동화

## kubeadm
### 시스템 요구사항
- 호환되는 Linux 호스트
    - Debian 및 Red Hat 계열의 Linux 배포판을 지원.
    - 패키지 매니저가 없는 배포판을 위한 지침도 제공됨
- 메모리 (RAM)
    - 머신(노드)당 최소 **2GB 이상**이 필요. 이보다 적으면 애플리케이션을 실행할 공간이 부족
- CPU
    - **컨트롤 플레인(Control Plane) 노드**는 **2개 이상의 CPU**가 필요
- 네트워크 연결
    - 클러스터의 모든 머신(노드) 간에 완전한 통신이 가능해야 함. 
    - 공용 또는 사설 네트워크 모두 사용 가능
- 고유 식별자
    - 모든 노드는 각기 다른 고유한 **호스트 이름(hostname), MAC 주소, product_uuid**를 가져야 함
	    - Mac 주소는 `iplinkg` or `ipconfig -a` 로 확인
	    - uuid 는 `sudo cat /sys/class/dmi/id/product_uuid`
- 포트(Port) 개방
    - 쿠버네티스가 사용하는 특정 포트들이 각 머신에서 개방되어 있어야 함.
    - 포트는 아래 `Control Plane 포트`, `Worker Node 포트` 확인.
    - 커맨드 예시 : `nc 127.0.0.1 10250 -zv -w 2` 
        - `connection refused` 결과여야 함. (대상에 도달했지만, 해당 포트에 서비스가 안 떠 있어서 닫힘)
- swap 설정
	- kubelet 은 노드에 swap memory가 켜져 있으면 시작하지 않고 실패
	- swap을 끄거나, kubelet이 swap을 허용하도록 설정해야 함.
```
✅ swap
- 메모리가 부족할 때, 디스크 일부 공간을 메모리 처럼 사용하는 것/영역.
- 실제 메모리보다 속도 느림.
- K8S 에서 정확한 메모리 사용을 감지하지 못할 수 있음.
```
- glibc 확인
    - 동적 링크(dynamic linking) 을 이용하여 설치되기 때문에 필요.
    - `ldd --version` 으로 확인 가능

### Control Plane 포트
| Protocol | Direction | Port Range | Purpose | Used By |
| --- | --- | --- | --- | --- |
| TCP | Inbound   | 6443       | Kubernetes API server   | All                  |
| TCP | Inbound   | 2379-2380  | etcd server client API  | kube-apiserver, etcd |
| TCP | Inbound   | 10250      | Kubelet API             | Self, Control plane  |
| TCP | Inbound   | 10259      | kube-scheduler          | Self                 |
| TCP | Inbound   | 10257      | kube-controller-manager | Self  |

### Worker Node 포트

| Protocol | Direction | Port Range  | Purpose | Used By |
| ---| --- | --- | --- | --- |
| TCP | Inbound   | 10250       | Kubelet API        | Self, Control plane  |
| TCP | Inbound   | 10256       | kube-proxy         | Self, Load balancers |
| TCP | Inbound   | 30000-32767 | NodePort Services† | All                  |
| UDP | Inbound   | 30000-32767 | NodePort Services† | All                  |

### 설치 항목
- **kubeadm** : 클러스트를 bootstrap 하는 명령어.
- **kubelet** : 클러스트의 모든 머신에서 실행되고, 파드나 컨테이너 시작과 같은 작업을 수행하는 컴포넌트.
- **kubectl** : 클러스너에 명령하기 위한 명령어. CLI