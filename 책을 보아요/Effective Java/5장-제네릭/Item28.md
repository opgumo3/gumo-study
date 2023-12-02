# Item 28. 배열보다는 리스트를 사용하라.

## 배열은 공변, 제네릭은 불공변
* 배열은 공변이다. (covariant)
> ✔ 공변? </br>
Sub가 Super의 하위 타입이라면, 배열 Sub[]는 배열 Super[]의 하위 타입이 된다. 즉 함께 변한다는 뜻이다.

* 제네릭은 불공변이다. (invariant)
> ✔ 불공변? </br>
Type1과 Type2가 있을 때, `List<Type1>`은 `List<Type2>`의 하위 타입도 아니고 상위 타입도 아니다. 

### 문제가 있는 건 배열 쪽.
```java
Object[] objects = new Long[1];
objects[0] = "";
// 컴파일 오류가 나지 않음.
```
* 런타임에 ArrayStoreException이 난다.
* 컴파일할 때 문제를 알 수 있는 것이 낫다.

```java
List<Object> o = new ArrayList<Long>();
// 컴파일 오류.
```
```java
// ???
List<Object> o = Collections.singletonList(new ArrayList<Long>());
o.add(""); // UnsupportedOperationException
```

## 배열은 실체화 된다.
* 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다.
* 제네릭은 타입 정보가 런타임에 소거 되어, </br>
원소 타입을 컴파일타임에만 검사하며 런타임에는 알 수 없음.

> 제네릭의 소거는 제네릭이 지원되기 전의 코드와의 호환성을 지켜주는 메커니즘.

* 위의 이유로 배열과 제네릭은 잘 어우러지지 못함.
* 배열은 제네릭 타입, 매개변수화 타입, 타입 매개변수를 사용할 수 없다.
    * ex) `List<E>[]`, `List<String>[]`, `E[]`
    * 사용하면 제네릭 배열 생성 오류 발생.

* 제네릭 배열을 만들 수 있었다면?
```java
List<String>[] stringLists = new List<String>[1];
List<Integer> intList = List.of(42);

Object[] objects = stringList; // 배열은 공변이라 가능.
objects[0] = intList;

String s = stringLists[0].get(0); // 꺼낼 때, String으로 형 변환하려고 하니 ClassCastException이 발생.
```

## 실체화 불가 타입
* `E`, `List<E>`, `List<String>` 같은 타입을 실체화 불가 타입이라고 한다. (non-reifiable type)
* 실체화되지 않아 런타임에는 컴파일 타임보다 타입 정보를 적게 가진다.
* 매개변수화 타입 중 실체화될 수 있는 건 비한정적 와일드카트 타입뿐이다. (?)
* 제네릭 타입과 가변인수 메서드를 함께 쓸 때, </br>
호출 시 가변 인수 매개변수를 담은 배열이 만들어진다. </br>
그 때, 배열의 원소가 실체화 불가 타입이라면 경고가 발생하는 것. `@SafeVarargs`로 대처할 수 있음.

```java
// Java의 정석 - 12장

// java.util.Arrays
public static <T> List<T> asList(T... a) {
    return new ArrayList<T>(a);
}
```
* asList의 매개변수가 가변인자인 동시에 지네릭 타입.
* 메서드에 선언된 타입 T는 컴파일 과정에 Object로 바뀌어 Object[]가 됨.
* Object[] 에는 모든 타입의 객체가 들어있을 수 있다고 위험하다고 경고하는 것.
* 하지만 asList가 호출되는 부분에서는 T가 아닌 다른 타입은 들어가지 못하므로 @SafeVarargs를 붙여서 '이 메서드의 가변인자는 타입 안정성이 있다'라고 컴파일러에 알리는 것.