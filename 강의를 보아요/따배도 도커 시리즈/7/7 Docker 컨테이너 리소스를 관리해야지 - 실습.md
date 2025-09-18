```
🔥 WARNING: Your kernel does not support OomKillDisable. OomKillDisable discarded.
```
# 컨테이너 리소스 제한과 모니터링
## 부하 테스트
- `stress` 를 이용하여 부하 테스트

```sh
# CPU 부하 테스트
stress --cpu 2

# Memory 부하 테스트
stress --vm 2 --vm-bytes <사용할 크기>
```

- 아래 명령어로 실습한 컨테이너 삭제
```sh
# 모든 컨테이너 삭제
docker rm -f $(docker ps -aq)
```

```
👉 cgroup
리눅스 커널에서 프로세스 집합에 대해 시스템 자원(CPU, 메모리, I/O 등)을 제한하고, 계측하며, 관리하는 기능
```

## 메모리 리소스 제한
```sh
# (1)

# 이미지의 기본 CMD 무시하고 stress --vm 1 ... 실행
# 1MB 메모리를 사용하는 VM 워커 1개를 5초 동안 실행
docker run -m 100m --memory-swap 200m stress stress --vm 1 --vm-bytes 1m -t 5s
# >> successful run completed in 5s


# (2)
docker run -m 100m --memory-swap 100m stress stress --vm 1 --vm-bytes 150m -t 5s
# >> failed run completed


# (3)
# swap 사이즈를 생략하면 메모리 최대 크기의 2배로 설정
docker run -m 100m stress stress --vm 1 --vm-bytes 150m -t 5s
# >> successful run completed in 5s
```

## OOM Killer 방지
```sh
docker run -d -m 100m --name m4 --oom-kill-disable=true nginx
# WARNING: Your kernel does not support OomKillDisable. OomKillDisable discarded.
docker inspect m4
```

## CPU 리소스 제한