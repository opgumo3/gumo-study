# 한정적 와일드카드를 사용해 API 유연성을 높이라.
> ✔ 매개변수화 타입은 불공변이다.

String은 Object의 하위타입이지만, `List<String>`은 `List<Object>`은 하위 타입이 아니다. </br>
* `List<String>`에는 문자열만 넣을 수 있고, `List<Object>`에는 어떤 타입이든 넣을 수 있으니, `List<String>`은 `List<Object>`가 하는 일을 제대로 수행하지 못하이 하위 타입이 될 수 없음.

### 하지만 불공변 방식보다 유연한 무언가가 필요하다..
```java
public class Stack<E> {
    public void pushAll(Iterable<E> src) {
        for (E e : src) push(e);
    }
}
```
`Stack<Number>`를 생성한 경우에, pushAll에 `List<Integer>` 리스트를 넣으면?
* 매개변수화 타입은 불공변이기 때문에, Integer가 Number의 하위타입이도 동작하지 않는다.

```java
// 생산자 : src는 Stack이 사용할 E 인스턴스를 생산함.
public void pushAll(Iterable<? extends E> src) {
        for (E e : src) push(e);
    }
```
한정적 와일드카드 타입을 사용하여, E의 하위 타입의 Iterable로 지정할 수 있음.

<hr>

```java
public void popAll(Collection<E> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}
```
`Stack<Number>`의 원소를 `Collection<Object>`에 옮기려고 하지만 역시나 불공변으로 동작하지 않는다.

```java
// 소비자 : dst는 Stack으로부터 E 인스턴스를 소비함.
public void popAll(Collection<? super E> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}
```
한정적 와일드 카드 타입을 사용하여, E의 상위 타입의 Collection으로 지정할 수 있음.

> 👍 유연성을 극대화하려면 원소의 생상자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라.

### 타입을 정확히 지정해야하 하는 상황에서는 와일드카드 타입을 쓰지 말자.
* 입력 매개변수가 생산자와 소비자 역할을 동시에 하는 경우.

### 💡 PECS : Producer-Extends, Consumer-Super
매개변수화 타입 T가 생산자라면 `<? extends T>`, 소비자라면 `<? super T>` 를 사용하라.

* 반환 타입에는 한정적 와일드카드 타입을 사용하면, 클라이언트 코드에서도 와일드카드 타입을 써야하기 때문에 사용하지 말자.

> 자바 7까지는 명시적 타입 인수를 사용해야 한다. </br> 컴파일러가 올바른 타입을 추론하지 못하는 경우에는 명시적 타입 인수를 사용해서 타입을 알려줘라.

#### PECS 공식 적용
```java
// 전
public static <E extends Comparable<E>> E max(List<E> list) {
    if (c.isEmpty()) throw new IllegalArgumentException("컬렉션이 비어 있습니다.")'

    E result = null;
    for (E e : c) {
        if (result == null || e.compareTo(result) > 0) {
            result = Objects.requireNonNull(e);
        }
    }

    return result;
}

// 후
public static <E extends Comparable<? super E> E max(List<? extends E> list)>
``` 
입력 매개변수는, 내부에서 E 인스턴스를 생산하므로, 생산자 (extends)</br>
`Comparable<E>` 는 E 인스턴스를 소비하므로, 소비자 (super)

> `Comparable`은 언제나 소비자므로, `Comparable<E>`보다는 `Comparable<? super E>`를 사용하는 편이 낫다.

> 수정 전의 max로는 ScheduledFutuer를 처리할 수 없음. </br>`Comparable<ScheduledFuture`를 구현한 것이 아닌, `Comparable<Delayed>`를 확장했기 때문에.

### 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드 카드로 대체.
```java
// 1. 비한정적 타입 매개변수
public static <E> void swap(List<E> list, int i, int j);
// 2. 비한정적 와일드카드
public static void swap(List<?> list, int i, int j);
```
public API라면 간단한 두 번째 선언이 더 낫다. </br>
하지만 비한정적 와일드카드 타입을 사용하면, null 외에는 어떤 원소도 넣을 수 없음. </br>

```java
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i))); // 컴파일되지 않는다.
};
```

#### 와일드카드 타입의 실제 타입을 알려주는 메서드를 private 도우미 메서드로 작성
```java
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
};

private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i))); 
}
```
* swap 메서드를 호출하는 클라이언트는 swapHelper의 존재를 몰라도 되고, 와일드카드 기반의 선언을 유지할 수 있음.