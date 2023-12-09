# 이왕이면 제네릭 메서드로 만들라.
> 💡 제네릭 메서드 </br>
메서드 선언부에 제네릭 타입이 선언된 메서드.</br>
제네릭 클래스에 정의된 타입 매개변수와 제네릭 메서드에 정의된 타입 매개변수는 별개의 것.</br>
메서드에 선언된 제네릭 타입은 지역 변수를 선언한 것과 같다고 생각하면 이해하기 쉽다.</br>
= Java의 정석 12장 =

```java
public class Collections {
// 메서드의 제한자와 반한 타입 사이에 타입 매개변수 목록을 둔다.
public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) {
        if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
            return Collections.indexedBinarySearch(list, key);
        else
            return Collections.iteratorBinarySearch(list, key);
    }
}
```

#### Tip. 타입 매개변수 명명 규칙
| 이름 | 의미 |
|---|---|
|E| Element|
|K| Key |
|N| Number |
|T| Type |
|V|Value |
|S,U,V etc. | 2nd, 3rd, 4th types |

> https://docs.oracle.com/javase/tutorial/java/generics/types.html


```java
public class Collections {

@SuppressWarnings("rawtypes")
public static final Set EMPTY_SET = new EmptySet<>();

@SuppressWarnings("unchecked")
    public static final <T> Set<T> emptySet() {
        return (Set<T>) EMPTY_SET;
    }
}
```
불변 객체를 여러 타입으로 활용할 수 있게 만들어야 할 때가 있음. </br>
요청한 타입 매개 변수에 맞게, 객체의 타입을 바꿔주는 정적 팩터리가 필요하게 되는데, </br>
이 패턴을 제네릭 싱글턴 팩터리라고 한다.

### 재귀적 타입 한정 (Recursive Type Bound)
* 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정하는 것.
* 타입의 자연적 순서를 정하는 Comparable 인터페이스와 함께 쓰인다.

```java
public interface Comparable<T> {
    int compareTo(T o); // T는 비교할 원소의 타입.
}
```
비교를 한다면, 자신과 같은 타입의 원소와 비교를 한다. </br>
String은 `Comparable<String>`을 구현하고, Integer는 `Comparable<Integer>`을 구현.

<hr>

Comparable을 구현한 원소의 컬렉션을 이용하는 메서드들은 주로 정렬/검색/min/max 구하는데, </br>
이 기능을 수행하려면 컬렉션에 담긴 모든 원소가 Comparable을 구현을 해야하는 제약이 있음. </br>
이 제약을 코드로 표현하면 아래와 같음.
```java
public static <E extends Comparable<E>> E max(Collections<E> c);
// 타입 E는 자기 자신인 E와 비교할 수 있다.
```


```java
public class Collections {
// List의 타입 파라미터에 대해 제약.
// List의 원소 타입은 자기 자신과 비교 할 수 있음.
public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) {
        if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
            return Collections.indexedBinarySearch(list, key);
        else
            return Collections.iteratorBinarySearch(list, key);
    }
}
```

> == Java의 정석 12장 == </br>
`<? extends T>` T와 그 자손들만 가능 </br>
`<? super T>` T와 그 조상들만 가능