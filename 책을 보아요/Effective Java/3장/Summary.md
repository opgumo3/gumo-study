# Item 10. equlas는 일반 규약을 지켜 재정의하라.
equals를 재정의하지 않는 경우에는 인스턴스를 자기 자신과 비교할 때만 true를 반환하게 된다.
#### 1. equals를 재정의 하지 말아야 하는 경우
* 각 인스턴스가 본질적으로 고유한 경우 ex) `Thread`클래스

* 인스턴스의 논리적 동치성을 검사하지 않는 경우

* 상위 클래스의 equals 메소드 하위 클래스에도 딱 들어맞는 경우

* 클래스가 private이거나 default이고 equals 메서드를 호출하지 않는 경우

#### 2. equals를 재정의 하지 않아도 되는 경우
* 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 경우 ex) Enum

#### 3. equals를 재정의해야 하는 경우
* 논리적 동치성을 비교해야하지만, 상위 클래스의 equals가 논리적 동치성을 비교하지 않는 경우 ex) 값 클래스인 Integer, String

## 👍 재정의 한다면 따라야 할 일반 규약
1. 반사성
* 객체는 자기 자신과 같아야 한다.
* x.equals(x) 는 true

2. 대칭성
* x.equals(y) = y.equals(y) 여야 한다.

3. 추이성
* x.equals(y)가 true이고, y.equals(z)도 true면 x.equals(z)도 true.
* 구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 없지만 `컴포지션`으로 우회할 수 있음.

4. 일관성
* x.equals(y)를 반복해서 호출하면 항상 ture나 false를 반환.
* equals는 항시 메모리에 존재하는 객체만을 사용한 결정적 계산을 수행해야 함.

5. null 아님
* x.equals(null)은 false


# Item11. equals를 재정의하려거든 hashCode도 재정의하라
* 같이 재정의하지 않으면, hashCode 일반 규약을 어겨, HashMap이나 HashSet 같은 컬렉션의 원소로 사용할 때 문제를 일으킨다.
>hashCode가 다르면 equals를 하기도 전에, 다른 객체라고 판단함.

* hashCode를 재정의 하지 않는다면, `equals(Object)가 true라면 두 객체의 hashCode는 같아야 한다.` 조항에 위배된다.
> 재정의 하지 않았을 때 사용되는 Object의 hashCode 메서드는 객체의 고유 주소 값을 바탕으로 반환하기 때문에 객체마다 다른 값을 반환한다. 

* hashCode를 재정의 했을 때, 위 조항을 위배하지 않는지 확인해라.

* 해시 충돌이 적은 방법을 써야 한다면 `구아바의 Hasing`을 참고하자.
* `Objects.hash`를 사용해서 해시코드 값을 얻을 수 있지만, 박싱과 언박싱 과정을 거칠 수 있기 때문에 속도가 느리다.
* hashCode를 구하는 비용이 크다면 캐싱을 고려하자.

# Item12. toString을 항상 재정의하라.
> toString의 규약은 `모든 하위 클래스에서 이 메서드를 재정의하라`고 한다.

toString을 잘 재정의하면 사용하기에 편하고, 디버깅하기 쉽다. </br>
그렇다면 어떻게 잘 재정의할까?

1. 객체가 가진 주요 정보를 모두 반환하는게 좋다.</br> 그게 안된다면, 요약 정보라도 담아야 한다.
2. 포맷을 명시할지 정해야 한다. </br>
포맷을 명시하기로 했다면, 명시한 포맷에 맞는 문자열과 객체를 상호 전환할 수 있는 메소드를 제공하면 좋다. </br>
3. 포맷과 상관없이 toString이 반환한 값에 포함된 정보를 얻어올 수 있는 API를 제공하자.


# Item13. clone 재정의는 주의해서 진행하라
* `Cloneable`은 복제해도 되는 클래스임을 명시하는 용도의 Mixin Interface
* `Cloneable`을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환.
* (문제점) 인터페이스는 클래스가 인터페이스에서 정의한 기능을 제공한다고 선언하는 행위지만, `Cloneable`은 protected clone의 동작 방식을 결정한다.

* 클래스가 가변 객체를 참조하면 지옥이 펼쳐진다. </br> 가변 객체의 참조가 같지 않도록 해야 한다.
```java
public class Tiger extends Cat implements Cloneable {

    private List<Person> fans = new ArrayList<>();
    
    @Override
    protected Tiger clone() throws CloneNotSupportedException {
        Tiger clone = (Tiger) super.clone();
        List<Person> newFans = new ArrayList<>();
        newFans.addAll(fans);
        clone.fans = newFans;

        return clone;
    }
}
```

* Cloneable을 구현한 스레드 세이프 클래스를 작성한다면, clone 메서드도 적절히 동기화해줘야 한다.

* Cloneable을 구현한 클래스를 확장하는 상황이 아니라면 `복사 생성자(변환 생성자)`와 `복사 팩터리(변환 팩터리)` 방식으로 객체 복사 방식을 제공할 수 있다.

```java
public Yum(Yum yum) {
    ...
}
```

```java
public static Yum newInstance(Yum yum) {
    ...
}
```

# Item14. Comparable을 구현할지 고려하라.
* `Comparable` 인터페이스의 `compareTo`는 단순 동치성 비교(equals)에 더해 순서까지 비교할 수 있고 제네릭하다.
* Comparable을 구현한 객체들의 배열은 `Arrays.sort(a)`로 손쉽게 정렬할 수 있다.

* compareTo 규약을 지키지 못하면 정렬된 컬렉션인 TreeSet이나 Treemap, 검색과 정렬 알고리즘을 활용하는 클래스인 Collections와 Arrays 사용 어렵.

* 자바 8에서는 `Comparator` 인터페이스와 비교자 생성 메서드의 메서드 연쇄 방식으로 비교자를 생성할 수 있게 되었다. 이걸로 `Comparable` 인터페이스의 compareTo 메소드를 구현할 수 있음. 간결하지만 약간의 성능 저하가 있다.
```java
// java.util.Comparator.comapringInt;
private static final Comparator<Type> COMPARATOR = comparingInt( (Type t) -> t.length)
        .thenComparingInt(t -> t.height); // 비교자 생성 메서드

public int compareTo(Type type) { // Comparable의 compareTo 오버라이드
    return COMPARATOR.compare(this, type);
}
``` 
