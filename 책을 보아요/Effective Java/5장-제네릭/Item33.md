# 타입 안전 이종 컨테이너를 고려하라.
* `ThreadLocal<T>`, `AtomicReference<T>` 등의 단일원소 컨테이너에서 제네릭은 흔히 쓰인다.
    * 이런 쓰임에서 매개변수화되는 대상은 컨테이너 자신.

> 💥 컨테이너?

데이터베이스의 행, 열의 열을 타입 안전하게 이용하는 방법?
* 컨테이너 대신 키를 매개변수화하고, 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 제공.
* 값의 타입이 키와 같음을 보장할 것이다.
* 이를 **타입 안전 이종 컨테이너 패턴**이라고 한다.

```java
public class Favorites {
    // 키 자체가 와일드 카드가 아니므로, 값을 넣을 수 있다.
    private Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }
    
    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}
```
* 각 타입의 Class 객체를 키 역할로, 인스턴스를 저장한다.
> String.class의 타입은 `Class<String>`, Integer.class의 타입은 `Class<Integer>`.

> **컴파일타임 타입 정보와 런타임 타입 정보를 알아내기 위해 메서드들이 주고받는 class 리터럴을 타입 토큰이라고 한다.** </br>
타입 안전 이종 컨테이너에서 쓰이는 Class 객체를 타입 토큰이라고 한다.

* 위처럼 코드를 작성하면, favorites 맵은 키와 값 사이의 타입 관계는 보장하지 않는다.
    * 값이 그 키 타입의 인스턴스라는 정보가 사라짐.
    * 하지만, getFavorite 메서드에서 이 관계를 되살릴 수 있음.
* type.cast는 `class Class<T>`의 Class 객체의 타입 매개변수를 반환하여, Favorites를 타입 안전하게 만든다.

#### 제약
1. 클라이언트가 Class 객체를 로타입으로 넘긴다면?
    * 컴파일 때, 비검사 경고가 뜰 것. (getFavorite 시에 ClassCastException 난다고 함.)
    * 만약 Favorites가 타입 불변식을 어기지 않도록 보장하려면, putFavorite 메서드에서 동적 형변환을 쓰면 된다.
```java
public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }
```

2. 실체화 불가 타입에는 사용할 수 없다.
    * String, String[] 은 저장할 수 있지만, `List<String>`은 저장할 수 없음.
    * `List<String>`용 Class 객체를 얻을 수없기 때문에.
    * 완벽히 만족스러운 우회로는 없다만 궁금하다면 슈퍼 타입 토근에 대해 알아보세요.
    
* 지금은 어떤 Class 객체든 받아들이는데, 제한을 하려면 한정적 타입 토근을 활용하면 된다.
    * 한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입을 제한하는 타입 토큰.
    * 애너테이션 API는 한정적 타입 토큰을 적극적으로 사용한다.

```java
public interface AnnotatedElement {
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);
}
```

* annotationType 인수는 애너테이션 타입을 뜻하는 한정적 타입 토큰으로, 저 타입의 애너테이션이 대상 요소에 달려있다면 그 애너테이션을 반환하고 없다면 null 반환.

* 만약 Class<?> 타입의 객체가 있고, 이를 한정적 타입 토큰을 받는 메서드에 넘기려면?
     * 객체를 `Class<? extends Annotation>` 으로 형변환 할 수 있지만, 형변환이 비검사이므로 컴파일 과정에서 경고가 뜰 것임.
     asSubclass 메서드는 호출된 인스턴스 자신의 Class 객체를 인수가 명시한 클래스로 형변환한다.
```java
static Annotation getAnnotation(AnnotatedElement element, String annotationTypeName) {
    Class<?> annotationType = Class.forNmae(annotationTypeNmae);
    try {
        annotationType = Class.forName(annotationTypeName);
    } catch (Exception ex) {
        //
    }

    return element.getAnnotation(annotationType.asSubclass(Annotation.class));
}
```