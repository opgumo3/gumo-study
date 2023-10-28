# 생성자 대신 정적 팩터리 메서드를 고려하라.
클래스의 인스턴스를 생성하는 방법으로 생성자 대신 `정적 팩터리 메서드`를 제안한다. </br>
생성자와 비교했을 때 이점이 있지만, 단점 또한 존재한다.</br>

<hr>

```java
package java.lang;

public final class Boolean implements Serializable, Comparable<Boolean> {

    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

}
```
위 코드는 java.lang 패키지에 들어있는 boolean 타입의 박싱 클래스인 Boolean 클래스다. </br>
여기서 `valueOf(boolean)` 메소드는 Boolean 클래스의 인스턴스를 반환하는 정적 메서드이고, 이와 같은 메서드를 정적 팩터리 메서드를 말한다.

> ✔ 정적 팩터리 메서드 (static factory method) </br> 클래스의 인스턴스를 반환하는 정적 메서드

## 장점
### 1. 이름을 가질 수 있다.
* <strong>반환될 객체의 특성을 쉽게 묘사</strong>할 수 있다.
* 시그니처가 같은 생성자가 여러 개 필요하다면, 매개변수의 순서를 다르게 하여 풀 수 있다. </br> 하지만 이 방법은 비추천! </br>이런 경우에는 정적 팩터리 메서드를 만들어 각각의 차이를 드러내느 이름을 지어주도록 하자.
> ✔ 메서드 시그니처 = 메서드 이름 + 매개변수 (반환형은 포함되지 않는다.)

### 2. 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.
Boolean 클래스를 다시 봐보자.
```java
package java.lang;

public final class Boolean implements Serializable, Comparable<Boolean> {
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

}
```

미리 만들어 놓은 인스턴스를 재사용한다는 것을 볼 수 있다.
* 객체가 자주 요청되는 상황이고, 특히 생성 비용이 큰 객체라면 이 방식으로 성능을 높일 수 있다.
* 플라이웨이트 패턴이 이와 비슷한 기법이다.
* 반복되는 요청에 같은 객체를 반환하는 식으로 언제 어느 인스턴스를 살아 있게 할지 통제할 수 있다. 이런 클래스를 `인스턴스 통제 클래스(instance-controlle class)` 라고 한다.
</br> ex) 플라이웨이트 패턴, 열거 타입.

> ✔ 인스턴스를 통제하는 이유? 
</br> 1. 클래스를 싱글턴으로 만들 수 있다. 
</br> 2. 클래스를 인스턴스화 불가로 만들 수 있다.
</br> 3. 불변 값 클래스에서 동치인 인스턴스가 단 하나뿐임을 보장할 수 있다.

### 3. 반환 타입의 하위 타입 객체를 반환할 수 있다.
* 반환할 객체의 클래스를 자유롭게 선택할 수 있다.
* 인터페이스를 정적 팩터리 메서드의 반환 타입으로 사용하는 인터페이스 기반 프레임워크를 만드는 핵심 기술.
* API를 만들 때, 구현 클래스를 공개하지 않고도 객체를 반환할 수 있어 API를 작게 유지할 수 있다.
* 자바 8 전에는 인터페이스에 정적 메서드를 선언할 수 없어서, 인스턴스화 불가인 동반 클래스(companion class)를 만들어 그 안에 정의해야 했다.</br>
java.util.Collections는 인스턴스화 불가 클래스로 45개의 유틸리티 구현체를 정적 팩터리 메서드로 제공한다.
> ✔ 인터페이스 from Java의 정석
</br> - 모든 멤버 변수는 public static final 이어야 하고, 생략할 수 있다.
</br> - 모든 메서드는 public abstract 이어야 하며, 생략할 수 있다. 
</br> (JDK1.8부터 static 메서드와 디폴트 메서드는 예외.)
</br></br>
원래 인터페이스에 추상 메서드만 선언할 수 있었다. 그래서 인터페이스와 관련된 static 메서드는 별도의 클래스에 따로 두어야 했다. 그 예시가 java.util.Collection 인터페이스와 java.util.Collections 클래스. 
</br></br>
Collection 인터페이스와 관련된 static 메서드들이 인터페스에는 추상 메서드만 선언할 수 있다는 원칙때문에 별도의 Collections라는 클래스에 들어가게 되었다.

* 정적 팩터리 메서드를 사용하는 클라이언트는 얻은 객체를 인터페이스만으로 다루게 된다.

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
위는 EnumSet 클래스에서 원소의 수에 따라, Enum의 하위 클래스 중 하나를 반환하는 코드다.
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

#### 정적 팩터리 메서드 명명 방식
1. from : 매개변수 하나 받아 해당 타입의 인스턴스 반환
</br>`Date d = Date.from(instant);`

2. of : 여러 매개변수 받아 적합한 타입의 인스턴스 반환
</br>`Set<Rank> faceCard = EnumSet.of(JACK, QUEEN, KING);`

3. valueOf : from과 of의 더 자세한 버전
</br>`BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);`

4. instance / getInstance : 매개변수로 명시한 인스턴스를 반환하지만, 같은 인슽언스임을 보장하지 않음
</br>`StackWalker luke = StackWalekr.getInstance(options);`

5. create / newInstacne : 매번 새로운 인스턴스를 생성해 반환.
</br>`Object newArrya = Array.newInstance(classObjec, arrayLen);`

6. getType : getInstnace와 같으나, 생성할 클래스가 아닌 다른 클래스에 정의.
</br>`FileStore fs = Files.getFileStore(path);`

7. newType : newInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 정의.
</br>`BufferedReader br = Files.newBufferedReader(path);`

8. type : getType과 newType의 간결한 버전
</br>`List<Complaint> litany = Collections.list(legacyLtany);`