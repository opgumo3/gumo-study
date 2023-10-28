# try-finally보다는 try-with-resources를 사용하라
* Java 7 부터의 `try-with-resources`를 사용해라.
* 이 구조를 사용하려면 해당 자원이 `AutoCloseable` 인터페이스를 구현해야한다.
* 읽기 수월하고, 오류 추적에도 좋다.

try-finally로 작성한다면?
```java
public class Main {
    public static void main(String[] args) throws IOException {
        FileInputStream fin = null;

        try {
            fin = new FileInputStream("ddd");
            fin.read();
        } finally {
            fin.close();
        }
    }
}
```
없는 파일이라서 FileInputStream 생성 중에, `FileNotFoundException`이 발생해야 한다.
</br> 하지만 코드를 실행했을 때, 발생하는 예외는 NPE다. 
</br> 두 번째 예외가, 첫 번째 예외를 집어삼켜버린다.

try-with-resources 문으로 작성해보자.
```java
public class Main {
    public static void main(String[] args) throws IOException {
        try (FileInputStream fin = new FileInputStream("ddd")) {
            fin.read();
        }
    }
}
```
FileNotFoundException이 발생하면서 제대로된 원인을 파악할 수 있다.

```java
try (FileInputStream fin = new FileInputStream("ddd")) {
    // logic
} catch (FileNotFoundException e) {
    throw new RuntimeException(e);
} catch (IOException e) {
    throw new RuntimeException(e);        
```
위 처럼 catch 문과 함께 사용하자.