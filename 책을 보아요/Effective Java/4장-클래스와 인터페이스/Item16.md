# public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라.
```java
class Point {
    public double x;
    public double y;
}
```
위의 코드는 데이터 필드에 직접 접근할 수 있어, 캡슐화의 이점을 제공할 수 없다.
API를 수정하지 않고는 내부 표현을 바꿀 수 없고, 불변식을 보장할 수 없다.
또한 외부에서 필드에 접근할 때 부수 작업을 수행할 수 없다. (?)

```java
class Point {
    private double x;
    private double y;

    // constructor

    // getter, setter : 접근자와 변경자
}
```
접근자와 변경자를 제공하자.
패키지 바깥에서 접근할 수 있는 클래스라면 접근자를 제공하자.
이렇게 하면 클래스 내부 표현 방식을 언제든 바꿀 수 있다.

package-private 클래스나 private 중첩 클래스라면 데이터 필드를 노출해도 문제가 없다.
