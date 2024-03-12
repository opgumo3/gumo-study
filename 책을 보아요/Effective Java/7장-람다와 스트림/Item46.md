# Item 46. 스트림에서는 부작용 없는 함수를 사용하라.
* 스트림은 함수형 프로그래밍에 기초한 패러다임이다
* 스트림 패러다임의 핵심은 계산을 일련의 변환으로 재구성하는 부분인데, 변화 단계는 `순수 함수`여야 한다.

### 순수 함수
- 입력만이 결과에 영향을 주는 함수
- 가변 상태를 참조하지 않는다.
- 함수 스스로도 다른 상태를 변경하지 않는다.
- 스트림 연산에 건네는 함수 객체는 모두 부작용이 없어야 한다.

</br>
<hr>
</br>
파일에서 단어의 빈도수를 구하는 코드를 작성해보자.

</br>

```java
// (1)
words.forEach(word -> {
    frequencyMap.merge(word.toLowerCase(), 1L, Long::sum);
})
```
- 위의 코드는 스트림을 사용했지만, 스트림답게 사용하지 못했음.
    - frequencyMap이라는 외부 상태를 수정하는 람다를 실행하기 떄문에.
    - forEach는 대놓고 반복적이라서 병렬화할 수 없다.

스트림답게 작성해본다면?
```java
// (2)
frequencyMap = words.collect(groupingBy(String::toLowerCase, counting()));
```
- (2)의 코드는 collector(수집기)를 사용하여, 스트림의 원소를 손쉽게 컬렉션으로 모았다.
- collector 인터페이스는 축소 전략을 캡슐화한 블랙박스 객체라고 생각하자.
    - 축소란 스트림의 원소들을 객체 하나에 취합한다는 뜻.

## collector
- 위에서 말했듯, collector를 사용하면 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다.
- `toList()`, `toSet()`, `toCollection(collectionFactory)` 를 사용하여 컬렉션을 반환받을 수 있음.

```java
// (3) collector 예시 코드
stringList.stream().collect(toList());
```

> (1) collect() 는 Stream 인터페이스에 정의되어 있으며, 인자로 Collector 인터페이스를 받는다.

> (2) Collectors 클래스에는 `toList()`, `toSet()` 등의 Collector 인터페이스 구현체를 반환하는 정적 메소드가 있음.

> (3) 예시코드에서는 사용하지 않았지만, comparing() 메소드는 비교자 생성 메서드다. (Comparator.comparing)

```java
// Collectors 클래스의 toList()
public static <T>Collector<T, ?, List<T>> toList() {
        return new CollectorImpl<>(
            (Supplier<List<T>>) ArrayList::new, 
            List::add,
            (left, right) -> { 
                left.addAll(right); return left; },
            CH_ID);
    }
```

### toMap()
- 스트림의 각 원소가 고유한 키에 매핑되어 있을 떄 적합함.
- 인수가 2개, 3개, 4개의 경우로 중복 정의되어 있음

1. 인수가 2개인 toMap()
```java
Collector<T, ?, Map<K,U>> toMap(
    Function<? super T, ? extends K> keyMapper,
    Function<? super T, ? extends U> valueMapper) {}

// 예시
Steram.of(values()).collect(
    toMap(Object::toString, e->e));
```

2. 인수가 3개인 toMap()
```java
Collector<T, ?, Map<K,U>> toMap(
    Function<? super T, ? extends K> keyMapper,
    Function<? super T, ? extends U> valueMapper,
    BinaryOperator<U> mergeFunction) {}

// 예시
albums.collect(
    toMap(Album::artis, a->a, maxBy(comparing(Album::sales))));
```
- 어떤 키와 그 키에 연관된 원소들 중 하나를 골라 연관 짓는 맵을 만들 때 유용하대요.
- 예시에서 mergeFunction에 들어간 `maxBy()`는 `BinaryOperator` 의 정적 메서드.
```java
@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T,T,T> {
    // minBy도 있어욤!

    public static <T> BinaryOperator<T> maxBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (a, b) -> comparator.compare(a, b) >= 0 ? a : b;
    }
}
```

3. 인수가 4개인 toMap()
```java
Collector<T, ?, M> toMap(
    Function<? super T, ? extends K> keyMapper,
    Function<? super T, ? extends U> valueMapper,
    BinaryOperator<U> mergeFunction,
    Supplier<M> mapFactory) {}
```
- 특정 맵 구현체를 직접 지정할 수 있음.

> toConcurrentMap은 병렬 실행된 후 결과로 ConcurrentHashMap 인스턴스를 생성한다.

### groupingBy()
- 입력으로 분류 함수를 받고, 출력으로는 원소들을 카테고리별로 모아 놓은 맵을 담은 collector를 반환한다.

```java
// 시그니처 생략된 부분 있음.
Collector groupingBy(
    Function classifier,
    Collector<? super T, A, D> downstream) {}
```
- groupingBy도 중복 정의 되어 있음.
- 반환하는 Collector가 리스트 외의 값을 갖는 맵을 생성하게 하려면, 다운스트림을 명시해야 함.
- 다운스트림에 counting()을 건네면, 해당 카테고리에 속하는 원소의 개수와 매핑한 맵을 얻음.
    - counting() 은 다운스트림 collector 전용이다.
```java
// groupingBy() 사용예시
words.collect(groupingBy(word -> alphabetize(Word)));

Map<String, Long> freq = words.collect(groupingBy(String::toLowerCase, counting()));
```

> groupingByConcurrent메서드는 ConcurrentHashMap 인스턴스를 만들어준다.

> partitionBy : 분류 함수 자리에 Predicate를 받고 키가 Boolean인 맵을 반환함.

> Collectors 에 외이 메서드들이 있으니, 클래스 내부를 보고 사용해보길.
