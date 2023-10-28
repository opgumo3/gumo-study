# 1. 생성자 대신 정적 팩터리 메서드를 고려하라.
```java
package java.lang;

public final class Boolean implements Serializable, Comparable<Boolean> {

    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

}
```
Boolean 클래스의 `valueOf(boolean)` 메소드는 Boolean 인스턴스를 반환하는 정적 팩터리 메서드.

> ✔ 정적 팩터리 메서드 (static factory method) </br> 클래스의 인스턴스를 반환하는 정적 메서드

## 장점
### 1. 이름을 가질 수 있다.
* <strong>반환될 객체의 특성을 쉽게 묘사</strong>할 수 있다.

### 2. 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.
```java
package java.lang;

public final class Boolean implements Serializable, Comparable<Boolean> {
    // 인스턴스 재사용
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

}
```
* 객체가 자주 요청되는 상황이고, 특히 생성 비용이 큰 객체라면 이 방식으로 성능을 높일 수 있다.
* 플라이웨이트 패턴이 이와 비슷한 기법이다.

> ✔ 인스턴스를 통제 클래스 : 언제 어느 인스턴스를 살아 있게 할지 통제할 수 있음. 
</br> 1. 클래스를 싱글턴으로 만들 수 있다. 
</br> 2. 클래스를 인스턴스화 불가로 만들 수 있다.
</br> 3. 불변 값 클래스에서 동치인 인스턴스가 단 하나뿐임을 보장할 수 있다.

### 3. 반환 타입의 하위 타입 객체를 반환할 수 있다.
* 인터페이스를 정적 팩터리 메서드의 반환 타입으로 사용하는 인터페이스 기반 프레임워크를 만드는 핵심 기술.
* API를 만들 때, 구현 클래스를 공개하지 않고도 객체를 반환할 수 있어 API를 작게 유지할 수 있다.
* 자바 8 전에는 인터페이스에 정적 메서드를 선언할 수 없어서, 인스턴스화 불가인 동반 클래스(companion class)를 만들어 그 안에 정의해야 했다.</br>
java.util.Collections는 인스턴스화 불가 클래스로 45개의 유틸리티 구현체를 정적 팩터리 메서드로 제공.
> ✔ 인터페이스 from Java의 정석</br>
원래 인터페이스에 추상 메서드만 선언할 수 있었다. 그래서 인터페이스와 관련된 static 메서드는 별도의 클래스에 따로 두어야 했다. 그 예시가 java.util.Collection 인터페이스와 java.util.Collections 클래스. 
</br></br>
Collection 인터페이스와 관련된 static 메서드들이 인터페스에는 추상 메서드만 선언할 수 있다는 원칙때문에 별도의 Collections라는 클래스에 들어가게 되었다.

### 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
* 반환 타입의 하위 타입이기만 하면 어느 객체를 반환하든 상관없다.
```java
public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");

        if (universe.length <= 64)
            return new RegularEnumSet<>(elementType, universe);
        else
            return new JumboEnumSet<>(elementType, universe);
    }
```
* <strong>클라이언트는 팩터리가 건네주는 객체가 어느 클래스의 인스턴스인지 알 수도 없고 알 필요도 없다! 그저 반환형의 하위 클래스이기만 하면 된다. </strong>

### 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.
* 서비스 제공자 프레임워크를 만드는 근간이 된다. ex) JDBC
</br>제공자 : 서비스의 구현체
</br>프레임워크 : 구현제들을 클라이언트에게 제공하는 역할, 클라이언트를 구현체로부터 분리.
> ✔ 서비스 제공자 프레임워크의 핵심 컴포넌트 3가지
</br> 1. 서비스 인터페이스
</br> &emsp; 구현체의 동작을 정의
</br> 2. 제공자 등록 API 
</br> &emsp; 제공자가 구현체를 등록할 때 사용.
</br> 3. 서비스 접근 API
</br> &emsp; 클라이언트가 서비스의 인스턴스를 얻을 때 사용.
</br> &emsp; 클라이언트는 원하는 구현체의 조건을 명시할 수 있다.
</br> &emsp; 서비스 제공자 프레임워크의 근간인 `유연한 정적 팩터리의 실체`
</br> (4. 서비스 제공자 인터페이스. SPI)
</br> &emsp; 서비스 인터페이스의 인스턴스를 생성하는 팩터리 객체를 설명.
</br> &emsp; 이게 없다면 각 구현체를 인스턴스로 만들 때 리플렉션을 사용해야 한다.

JDBC에서는..
</br> 서비스 인터페이스 = Connection
</br> 제공자 등록 API = DriverManager.registerDriver
</br> 서비스 접근 API = DriverManager.getConnection
</br> 서비스 제공자 인터페이스 = Driver
```java
try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    // 서비스 접근 API로 서비스 인터페이스를 얻음.
    Connection connection = DriverManager.getConnection(url, user, password);
    
    Statement statement = connection.createStatement();
    boolean result = statement.execute(SQL);
    
} catch (ClassNotFoundException e) {
    e.printStackTrace();
} catch (SQLException e) {
    e.printStackTrace();
}

```
그렇다면 서비스 제공자 API는?
</br> Class.forName의 String 부분으로 ㄱㄱ

```java
public class Driver implements java.sql.Driver {
    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException E) {
            throw new RuntimeException("Can't register driver!");
        }
    }

    // Construct a new driver and register it with DriverManager.
    public Driver() throws SQLException {
        // Required for Class.forName().newInstance().
    }
}

```
Driver 클래스에서 static 블록을 보면 된다.
</br> 클래스로더에 의해서 로드가 되면 DriverManager 클래스에 등록이 되는데,
</br> Class.forName(String name) 메서드에 의해서 작동한다고 되어있다.

> ✔ Java에서는 Driver와 Connection 인터페이스를 각 벤더에서 구현하도록 하고 DriverManager 클래스를 통해 사용자가 사용.

* Java 6부터 ServiceLoader라는 범용 서비스 제공자 프레임워크가 제공되어 프레임워크를 직접 만들 필요가 거의 없어졌다.

## 단점
### 1. 하위 클래스를 만들 수 없다.
불변 타입으로 만들려면 이 제약은 어쩌면 장점일 수도 있다.

### 2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.
생성자처럼 명확히 드러나지 않기 때문이다.
</br> 메서드 이름을 널리 알려진 규약에 따라 지어서 이 문제를 완화하자.

# 2. 생성자에 매개변수가 많다면 빌더를 고려하라.
클래스에 선택적인 매개변수가 많을 때는 어떻게 해야할까?

## 1. 점층적 생성자 패턴
```java
public class Person {
    private String name; // 필수
    private int age; // 필수
    private int tongueLength; // 선택
    private int footSize; // 선택
    private int noseHeight; // 선택

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    // 생략
    public Person(String name, int age, int tongueLength, int footSize, int noseHeight) {
        this.name = name;
        this.age = age;
        this.tongueLength = tongueLength;
        this.footSize = footSize;
        this.noseHeight = noseHeight;
    }
}
```
대충 사람 정보 담는 클래스가 있고, 선택 사항 필드까지 넣어서 만들어야 한다면 아래처럼 생성해야 할 것이다. 
</br>읽기 쉬운가? 필드가 더 늘어난다면? 혹은 int 매개변수간에 순서가 바뀐다면?
```java
Person person = new Person("Gumo", 24, 6, 245, 5);
```

## 2. 자바 빈즈 패턴
매개변수가 없는 생성자로 객체를 만든 후, Setter를 호출해 원하는 매개변수의 값을 설정하는 방식.

```java
Person person = new Person();
person.setName("Gumo");
person.setAge(24);
person.setTongueLength(6);
person.setFootSize(245);
person.setNoseHeight(5);
```
* 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다.


## 3. 빌더 패턴
점층적 생성자보다 코드를 읽고 쓰기가 한결하고, 자바빈즈 패턴보다 안전한 빌더 패턴을 봐보자.

```java
public class Person {
    private final String name;
    private final int age;
    private final int tongueLength;
    private final int footSize;
    private final int noseHeight;

    public static class Builder {
        private final String name;
        private final int age;
        // 선택 사항은 기본값으로 초기화.
        private int tongueLength = 0;
        private int footSize = 0;
        private int noseHeight = 0;

        public Builder(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Builder tongueLength(int tongueLength) {
            this.tongueLength = tongueLength;
            return this;
        }

        public Builder footSize(int footSize) {
            this.footSize = footSize;
            return this;
        }

        public Builder noseHeight(int noseHeight) {
            this.noseHeight = noseHeight;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }

    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.tongueLength = builder.tongueLength;
        this.footSize = builder.footSize;
        this.noseHeight = builder.noseHeight;
    }
}
```
빌더 패턴은 위처럼 작성하면되고, 사용은 아래처럼 하면 된다.
```java
Person gumo = new Person.Builder("gumo", 24)
                .noseHeight(5)
                .build();
```

### 빌더 패턴의 특징
* 클래스는 불변이다.
* 빌더를 연쇄적으로 호출할 수 있다.
* 코드 쓰기 쉽고, 읽기도 쉽다.것.
* 유효성 검사를 위해서, 빌더의 메서드에서 입력 매개변수를 검사하고, build 메서드가 호출하는 생성자에서 여러 매개변수에 걸친 불변식을 검사.
    * 빌더로부터 매개변수를 복사한 후, 해당 객체 필드들을 검사한다.

> ✔ 불변 (Immutable, Immutability) <-> mutable
</br> 어떠한 변경도 허용하지 않는다는 뜻. 
</br> ✔ 불변식 (Invariant)
</br> 프로그램이 실행되는 동안 혹은 정해진 기간 동안 반드시 만족해야하는 조건.
</br> ex) 리스트의 크기는 반드시 0 이상이어야 한다. 음수 값이 된다면 불변식이 깨지는 것.

> ✔ API는 시간이 지날수록 매개변수가 많아지는 경향이 있다는 것을 명심하자.

# 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라.
## 싱글턴 (Singleton)
* 인스턴스를 오직 하나만 생성할 수 있는 클래스.
* 싱글턴 인스턴스를 가짜 구현으로 대체할 수 없어 테스트하기 어려움.

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