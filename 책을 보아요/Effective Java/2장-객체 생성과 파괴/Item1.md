# 생성자 대신 정적 팩터리 메서드를 고려하라.
클래스의 인스턴스를 생성하는 방법으로 생성자 대신 정적 팩터리 메서드 방법을 제안한다. </br>
생성자와 비교했을 때 이점이 있으며, 단점 또한 존재한다.</br>

<hr>

```java
package java.lang;

public final class Boolean implements Serializable, Comparable<Boolean> {

    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

}
```
위 코드는 java.lang 패키지에 들어있는 boolean 타입의 박싱 클래스인 Boolean 클래스다. </br>
여기서 `valueOf(boolean)` 메소드는 Boolean 클래스의 인스턴스를 반환하는 정적 메서드이고, 이와 같은 메서드를 정적 팩터리 메서드를 말한다.

> ✔ 정적 팩터리 메서드 (static factory method) </br> 클래스의 인스턴스를 반환하는 정적 메서드

## 장점
