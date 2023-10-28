# 인스턴스화를 막으려거든 private 생성자를 사용하라
Math, Arrays, Collections 같은 유틸리티 클래스는 인스턴스로 만들어 쓰려고 설계한 것이 아니다. </br>
그렇다면 클래스의 인스턴스화를 어떻게 막을 수 있을까?</br>

### 클래스를 추상 클래스로 만들면 인스턴스화를 막을 수 있을까?
NO!
</br> 추상 클래스의 하위 클래스를 만들어 인스턴스화할 수 있다.


## private 생성자를 추가하면 인스턴스화를 막을 수 있다.
코드에 생성자를 명시하지 않아도, 컴파일러가 자동으로 기본 생성자를 생성한다.
</br> 그러니 생성자를 private으로 명시해라.
```java
public class A {
    // 인스턴스화 방지용
    private A() {
        // 클래스 내에서 생성자를 호출하지 않도록.
        throw new AssertionError();
    }
}
```
* 상속 불가능.
</br> 모든 생성자는 명시적이든 묵시적이든 상위 클래스의 생성자를 호출하기 때문에.
</br> 하위 클래스에서..
`There is no default constructor available in '[상위클래스]'`

아래는 Collections, Math 클래스의 private 생성자다.
```java
public class Collections {
    // Suppresses default constructor, ensuring non-instantiability.
    private Collections() {
    }
}
```

```java
public final class Math {
    /**
     * Don't let anyone instantiate this class.
     */
    private Math() {}
}
```