# Comparable을 구현할지 고려하라.
`Comparable` 인터페이스의 `compareTo`는 단순 동치성 비교(equals)에 더해 순서까지 비교할 수 있고 제네릭하다.

Comparable을 구현한 객체들의 배열은 `Arrays.sort(a)`로 손쉽게 정렬할 수 있다.

아래는 CompareTo 메서드의 일반 규약이다.
* 객체가 주어진 객체보다 작으면 음수(ex. -1), 같으면 -, 크면 양수 (ex. 1)을 반환
* Comparable을 구현한 클래스는 모든 x, y에 대해 x.compareTo(y) == -y.compareTo(x)
* x.compareTo(y) > 0 이고 y.compareTo(z) 라면 x.compareTo(z) > 0 이다.
* x.compareTo(y) = 0 이면, z.compareTo(x)와 z.compareTo(y) 는 같은 값을 가진다.
* x.compareTo(y) = 0 이면 x.equals(y) 는 true 여야 한다. (권고)


compareTo 규약을 지키지 못하면 정렬된 컬렉션인 TreeSet이나 Treemap, 검색과 정렬 알고리즘을 활용하는 클래스인 Collections와 Arrays와 어울리지 못한다.

* `Comparable`을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 한다면 `Comparator`를 대신 사용한다.

* 원래 compareTo에서 정수 기본 타입은 관계 연산자인 `>`, `<`를 사용하라고 했었지만, 자바 7부터 박싱된 기본 타입 클래스들에 정적 메서드인 `compare`가 추가되어 이를 사용하는 것을 추천한다. </br> 관계연산자를 사용하는 방식은 거추장스럽고 오류를 유발할 수 있다.
* 자바 8에서는 `Comparator` 인터페이스가 비교자 생성 메서드와 팀을 꾸려 메서드 연쇄 방식으로 비교자를 생성할 수 있게 되었다. 간결하지만 약간의 성능 저하가 있다.
```java
private static final Comparator<Type> COMPARATOR = comparingInt( (Type t) -> t.length)
        .thenComparingInt(t -> t.height); // 비교자 생성 메서드

public int compareTo(Type type) {
    return COMPARATOR.compare(this, type);
}
``` 