# Item43. 람다보다는 메서드 참조를 사용하라.
메서드 참조로 더 간결하게 작성할 수 있다.
### 예시
```java
public enum Operation {
    PLUS ("+", Double::sum) // 메서드 참조

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
} 
```
* 메서드 참조를 사용하면 같은 결과를 더 보기 좋게 얻을 수 있음.
* 하지만 람다에서의 매개변수 명이 코드적으로 좋은 가이드가 될 수 있어서, 항상 메서드 참조가 좋은 것은 아님.
* 대신 메서드 참조는 이름을 지어줄 수 있고, 문서를 남길 수 있음.


## 메서드 참조 유형 5가지
|유형|세부|예시|설명|람다
|------|------|------|------|------|
|정석 메서드를 가르킴| |`Integer::parseInt`||`str -> Integer.parseInt(str)`|
|인스턴스 메서드를 참조|한정적|`Instance.now()::isAfter`|수신 객체(참조 대상 인스턴스)를 특정함.<br/>함수 객체가 받는 인수와 참조되는 메서드가 받는 인수가 같음. | `Instant then = Instant.now();` </br> `t -> then.isAfter(t)`
|인스턴스 메서드를 참조|비한정적|`String::toLowerCase`|수신 객체를 특정하지 않음.</br>함수 객체를 적용하는 시점에 수신 객체를 알려준다.</br> 주로 스트림 파이프라인에서의 매핑과 필터 함수에 사용.| `str -> str.toLowerCase()`
|클래스 생성자를 가르킴||`TreeMap<K,V>::new`||`() -> new TreeMap<K,V>()`|
|배열 생성자를 가르킴||`int[]::new`||`len -> new int[len]`|

* 람다로 할 수 없는 일은 메서드 참조로도 `거의` 할 수 없다.
    * 제네릭 함수 타입 구현은 람다에서는 할 수 없지만, 메서드 참조로는 가능하다. 💥