# 제네릭과 가변인수를 함께 쓸 때는 신중하라.
* 가변인수(varargs) 메서드와 제네릭은 자바 5에 함께 추가되었지만 잘 어우러지지 않음.
* 가변인수 메서드를 호출하면, 가변 인수를 담기 위한 배열이 자동으로 하나 만들어지는데, 이 배열을 클라이언트에 노출하는 문제가 생김.
* 매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생함.

```java
static void dangerous(List<String>... stringList) {
    List<Integer> intList = List.of(42);
    Object[] objList = stringList; 

    objList[0] = intList; // 힙 오염 발생.
    String s = stringList[0].get(0); 
    // java.lang.Integer cannot be cast to class java.lang.String
}
```
* 인수를 건네는 부분에 컴파일러가 생성한 형변환이 숨어 있음.
* 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.

* 오류가 생길 수 있지만, 제네릭 varargs 매개변수를 받는 메서드를 선언할 수 있게 한 이유는?
    * 실무에서 유용하기 때문에.

* 자바 7부터 `@SafeVarargs`애너테이션이 추가되어, 제네릭 가변인수 메서드 작성자가 클라이언트 측에서 발생하는 경고를 숨길 수 있게 되었음.
    * `@SafeVarargs` : 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치

* 가변 인수 메서드를 호출할 때, varargs 매개변수를 담는 제네릭 배열이 만들어진다는 사실을 기억하자.
    * 메서드 내부에서 이 배열에 직접적으로 사용하지 않는다면, 타입 안전하다.

#### varargs 매개변수 배열에 아무것도 저장하지 않고도 타입 안전성을 깰 수 있다.
```java
static <T> T[] toArray(T... args) {
    return args;
}
```
* 메서드가 반환하는 배열의 타입은 이 멧드에 인수를 넘기는 컴파일타임에 결정되는데, 그 시점에 컴파일러에 충분한 정보가 주어지지 않아 타입을 잘못 판단할 수 있음.
* 힙 오염이 된 상태에서, 메서드를 호출한 쪽의 콜스택으로 전이될 수 있음.

<hr>
T 타입 인수 3개를 받아 그 중 2개를 무작위로 골라 담은 배열을 반환하는 예제.

```java
static <T> T[] toArray(T... args) {
    return args;
}

static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return toArray(a, b);
        case 1: return toArray(a, c);
        case 2: return toArray(b, c);
    }
}
```

* 컴파일러는 toArray에 넘길 T 인스턴스 2개를 담을 varargs 매개변수 배열을 만드는 코드를 생성.
* 이 코드가 만드는 배열의 타입은 Obejct[]
* 이 배열이 그대로 pickTwo를 호출한 클라이언트에게 전달됨.

```java
String[] strings = pickTwo("1", "2", "3");
```
위 코드처럼 사용할텐데, `Ljava.lang.Object; cannot be cast to class [Ljava.lang.String; ([Ljava.lang.Object; and [Ljava.lang.String`

Object[] 을 String[]로 형변환 하는 과정에서 형변환이 실패하는 것.</br>

> 제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다!

#### 예외
1. `@SafeVarargs`된 메서드에 넘기는 것은 안전하다.
2. 배열 내용의 일부 함수를 호출만 하는 일반 메서드에 넘기는 것은 안전하다.

> 안전하지 않은 varargs 메서드는 절대 작성해서는 안된다. </br> 그렇지 않다는 `@SafeVarargs`를 달아서 컴파일러 경고를 없애라.

#### 안전한 제네릭 varargs 메서드
* varargs 매개변수 배열에 아무것도 저장하지 않는다.
* 그 배열을 신뢰할 수 없는 코드에 노출하지 않는다.

#### varargs 매개변수를 List 매개변수로 바꿀 수 있다.
```java
// 전
static <T> List<T> flatten(List<? extends T>... lists) {

}

// 후
static <T> List<T> flatten(List<List<? extends T>> lists) {

}

flatten(List.of(list1, list2));
```
* 컴파일러가 이 메서드의 타입 안전성을 검증할 수 있음.
* 클라이언트 코드가 살짝 지저분해지고 속도가 조금 느려질 수 있음.

위 코드에서 toArray를 List.of로 사용하게 되면 클라이언트 쪽에서도 배열 없이 제네릭만 사용하므로 타입 안전함.
```java
List<String> attributes = pickTwo("1", "2", "3");
```