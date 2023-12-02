# Item 27. 비검사 경고를 제거하라.
> javac 명령줄 인수에 `-Xlint`를 붙여서 컴파일러 경고의 자세한 내용을 알 수 있음.

```java
Set<Lark> exaltation = new HashSet();
// 비검사 형변환 경고를 낸다.

Set<Lark> exaltation = new HashSet<>();
// 자바 7부터 지원하는 다이아몬드 연산자를 사용하면
// 컴파일러가 올바른 실제 타입 매개변수를 추론해준다.
```

> 👍 타입 안전성을 위해 할 수 있는 한 모든 비검사 경고를 제거해라.</br>
모든 비검사 경고는 런타임에 ClassCastException을 일으킬 수 있는 잠재적 가능성을 뜻한다.

### @SupressWarnings("unchecked")
* 경고를 제거할 수는 없지만, 타입이 안전하다고 확신할 수 있다면 `@SupressWarnings("unchecked")` 애너테이션을 달아 경고를 숨기고, </br>
경고를 무시해도 안전한 이유를 주석으로 남기자.
* 항상 가능한 한 좁은 범위에 적용하자. </br>
절대로 클래스 전체에 적용해서는 안된다.
    * 메서드나 생성자에 달려있다면, 지역 변수 선언 쪽으로 옮기자.

> @SupressWarnings("unchecked")는 지네릭스 타입을 지정하지 않았을 때 발생하는 경고를 나타나지 않게 한다.

