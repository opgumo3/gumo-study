
# Item 47. 반환 타입으로는 스트림보다 컬렉션이 낫다.
### 반환을 스트림으로 한다면
- 스트림은 Iterable 처럼 반복을 지원하지 않는다.
    - 그렇기 떄문에, API를 작성할 때, 스트림만 반환하도록 작성한다면, for-each 로 반복하기 어렵다.

> Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함하고, Iterable 인터페이스가 정의한 방식대로 동작하지만, Iterable을 extends 하지 않아서 for-each로 반복할 수 없다.

- 스트림을 for-each로 순회하기 위해서는 아래 코드처럼 작성해야한다. 
```java
Stream<String> stream = strings.stream();
Iterable<String> iterator = stream::iterator;
    for (String str : iterator) {
      System.out.println(str);
    }
```
- 난잡하고 직관성이 떨어져서, 변환하는 어댑터 메소드를 만들 수 있다.

```java
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
    return stream::iterator;
}

// iterableOf를 사용한다면
for(String str : iterableOf(stream)) {
    // 로직
}
```

### 반환을 Iterable로 한다면
- 스트림 파이프라인을 사용하기 어렵다.
- 이 경우 또한 어댑터를 만들 수 있다.
```java
public static <E> Stream<E> streamOf(Iterable<E> iterable) {
    return StreamSupport.sream(iterable.spliterator(), false);
}
```

> 공개 API를 작성할 때는, 스트림 파이프라인 사용자와 반복문 사용자 모두 배려해야한다.

### 반환을 컬렉션으로 한다면
- Collection 인터페이스는 Iterable의 하위 타입이고, stream 메서드도 제공하니 반복과 스트림을 동시에 지원한다!
- 원소 시퀀스를 반환하는 공개 API 경우에는 Collection 이나 그 하위 타입을 쓰는 게 일반적으로 최선이다.
- 하지만, 컬렉션 구현체의 덩치 큰 시퀀스를 메모리에 올려서는 안된다.
    - 이 경우에는 AbstractList를 이용해 Collection 구현체를 작성하자.

### 결론
어댑터를 사용하면 클라이언트 코드를 어수선하게 하고, 직접 구현한 전용 Collection은 코드는 지저분하지만 빠를 수도 있다.
