# private 생성자나 열거 타입으로 싱글턴임을 보증하라.
## 싱글턴 (Singleton)
인스턴스를 오직 하나만 생성할 수 있는 클래스.
</br> ex) 함수 같은 무상태 객체나, 설계상 유일해야 하는 시스템 컴포넌트

* 타입을 인터페이스로 정의하고, 그 인터페이스를 구현해서 만든 싱글턴이 아니라면
</br> 싱글턴 인스턴스를 가짜 구현으로 대체할 수 없어 테스트하기 어려움.

## 싱글턴 구현 방식 2가지
두 방식 모두 생성자는 private, 인스턴스에 접근할 수 있는 수단으로 public static 멤버를 제공한다.

### 1. public static 멤버가 final 필드인 방식
```java
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() {}
}
```
* 리플렉션 API인 `AccessibleObject.setAccessible`을 사용해 private 생성자를 호출할 수 있다.
    * 생성자를 수정하여 두 번째 객체가 생성되려 할 때 예외를 던지게 하면 됨.
* 해당 클래스가 싱글턴임이 명백히 드러난다.
* 간결함.

### 2. 정적 팩터리 메서드를 public static 멤버로 제공
```java
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();
    private Elvis() {}

    public static Elvis getInstance() { return INSTANCE; }
}
```
* 정적 팩터리 메서드를 바꿔서 싱글턴이 아니게 변경할 수 있음.
* 정적 팩터리 메서드를 제네릭 싱글턴 팩터리로 만들 수 있음. (item 30)
```java
public class Test {
    private static List<Object> list = new ArrayList<>();

    public static final <T> List<T> emptySet() {
        return (List<T>) list;
    }
}
```

```java
List<String> objects = Person.emptySet();
objects.add("hi");

List<Integer> objects1 = Person.emptySet();
objects1.add(1);
```
* 정적 팩터리의 메서드 참조를 공급자로 사용할 수 있음.
* 2번의 장점이 굳이 필요하지 않다면 1번 방법을 추천.

### 직렬화
싱글턴 클래스를 직렬화하려면, 클래스에 Serializable을 구현한다고 선언하는 것만으로는 부족하다.
</br> `transient` 선언하고 `readResolve` 메서드를 제공해야한다.
</br> 안그러면 직렬화된 인스턴스를 역직렬화할 때마다 새로운 인스턴스가 만들어진다.

> ✔ transient
</br> 직렬화 과정에서 제외하고 싶은 경우 사용하는 키워드

```java
private Object readResolve() {
    // 역직렬화 과정에서 만들어진 인스턴스 대신에 기존에 생성된 Elvis INSTACNE를 반환.
    return INSTACNE;
}
```

### 3. 원소가 하나인 열거 타입을 선언
```java
public enum Elvis {
    INSTACNE;
}
```
```java
class Elvis {
    // enum은 이런 느낌으로 생각하면 된다.
    public final static Elvis INSTACNE = new Elvis();
}
```
* 간결하고, 추가 노력 없이 직렬화할 수 있고, 복잡한 직렬화 상황이나 리플렉션 공격에서도 싱글턴을 유지할 수 있다. (JVM이 보장)
* 추천!
* 하지만 만들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 사용할 수 없음. (인터페이스 구현은 가능)

