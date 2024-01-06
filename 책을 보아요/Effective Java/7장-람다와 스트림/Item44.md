# Item 44. 표준 함수형 인터페이스를 사용하라.
자바가 람다를 지원하면서 API 작성 모범 사례도 바뀜.
* 함수 객체를 매개변수로 받는 생성자와 메서드

### LinkedHashMap의 예시
`removeEldestEntry`메서드를 함수형 인터페이스로 선언한다면
```java
@FunctionalInterface
interface EldestEntryRemovalFunction<K,V> {
    boolean remove(Map<K,V> map, Map.Entry<K,V> eldest);
    // 
}
```
하지만 이 인터페이스는 java.util.function 패키지에 이미 있음. (`BiPredicate<Map<K,V>, Map.Entry<K,V>>`)
* 필요한 용도에 맞는게 있다면, 직접 구현하지 말고 표준 함수형 인터페이스를 써라.

## 표준 함수형 인터페이스
java.tuil.function 패키지에 43개의 인터페이스가 있고, 기본 인터페이스 6개만 기억하면 나머지는 EZ할 것

### 기본 인터페이스와 변형
|인터페이스|시그니처|예시|설명
|------|------|------|------|
|`UnaryOperator<T>`|T apply(T t)|String::toLowerCase|인수가 1개이고 반환값과 인수의 타입이 같음.
|`BinaryOperator<T>`|T apply(T t1, T t2)|BigInteger::add|인수가 2개이고 반환값과 인수의 타입이 같음.
|`Predicate<T>`|boolean test(T t)| Collection::isEmpty|인수 하나를 받아 boolean 반환
|`Function<T,R>`|R apply(T t)| Arrays::asList|인수와 반환타입이 다른 함수
|`Supplier<T>`|T get()|Instant::now| 인수를 받지 않고 값을 반환하는 함수
|`Consumer<T>`|void accept(T t)|System.out::println|인수를 하나 받고 반환값은 없는(소비하는) 함수
* 기본 인터페이스에서 int, long, double용으로 각 3개씩 변형이 생김. (IntPredicate, LongBinaryOperator)
* Function의 변형만 매개변수화됨. (`LongFunction<int[]>`)
* Function 인터페이스의 변형
    * 입력과 결과 타입이 모두 기본 타입이면 SrcToResult를 접두어로. (LongToIntFunction)
    * 나머지는 입력은 객체 참조, 결과는 int,long,double로 ToReuslt를 접두어로. (ToLongFunction)
* 인수를 두개 받는 변형
    * `BiPredicate<T,U>`, `BiFunction<T,U,R>`, `BiConsumer<T,U>`
    * BiFunction은 기본 타입을 반환하는 변형 3개가 존재.
* Consumer
    * ObjDoubleConsumer, ObjIntConsumer, ObjLongConsumer 가 변형.
* `BooleanSupplier` 인터페이스는 boolean을 반환하도록 한 Supplier의 변형

### 알아둘 것
1. 표준 함수형 인터페이스 대부분은 기본 타입만 지원한다. <br> 인터페이스에 박싱된 기본 타입을 넣어 사용하지는 말자. 계산량이 많아질 때 성능이 안좋아질 것이다.
2. 되도록 표준 함수형 인터페이스를 사용하고, 없다면 직접 작성해라.
3. 구조적으로 같지만, 직접 작성하는 경우가 있다.
<br/> `Comparator<T>`의 경우 ToIntBiFunction과 동일하지만, 이름이 용도를 잘 설명하고, 구현하는 쪽에서 지켜야 할 규약을 담고있으며, 비교자들을 변환하고 조합하는 유용한 디폴트 메서드들을 가지고 있음.

### @FunctionalInterface
직접 만든 함수형 인터페이스에는 항상 이 애너테이션을 사용해라.
이 애너테이션을 사용하는 이유는 @`Override`를 사용하는 이유와 비슷하다.
* 프로그래머의 의도를 명시
* 인터페이스가 람다용으로 설계된 것임을 알려줌
* 해당 인터페이스가 추상 메서드를 오직 하나만 가지고 있어야 컴파일되게 해줌.
* 유지보수 과정에서 누군가 실수로 메서드를 추가하지 못하게 막아줌.

### 함수형 인터페이스를 API에서 사용할 때 주의점
서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중 정의해서는 안됨.
* 클라이언트에게 모호함만 안겨줌.
* Item 52 / ExecutorService의 submit 메서드
```java
<T> Future<T> submit(Callable<T> task);
<T> Future<T> submit(Runnable task, T result);
```