FROM debian
RUN apt-get update && apt-get install stress -y
CMD ["/bin/sh", "-c", "stress -c 1"] # -c 옵션 : 뒤의 문자열을 명령어로 실행