const http = require("http");
const os = require("os");

// 현재 OS 정보 출력
console.log("=== OS Information ===");
console.log("Hostname:", os.hostname());
console.log("Platform:", os.platform());
console.log("Arch:", os.arch());
console.log("Release:", os.release());
console.log("Total Memory:", os.totalmem());
console.log("Free Memory:", os.freemem());
console.log("CPUs:", os.cpus().length);

// 80 포트에서 간단한 서버 실행
const PORT = 80;

const server = http.createServer((req, res) => {
  res.writeHead(200, { "Content-Type": "text/plain" });
  res.end("Hello from Node.js running inside Docker!\n");
});

server.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
