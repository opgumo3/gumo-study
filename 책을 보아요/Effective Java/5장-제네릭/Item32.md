# 제네릭과 가변인수를 함께 쓸 때는 신중하라.
* 가변인수(varargs) 메서드와 제네릭은 자바 5에 함께 추가되었지만 잘 어우러지지 않음.
* 가변인수 메서드를 호출하면, 가변 인수를 담기 위한 배열이 자동으로 하나 만들어지는데, 이 배열을 클라이언트에 노출하는 문제가 생김.
* 매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생함.

```java
static void dangerous(List<String>... stringList) {
    List<Integer> intList = List.of(42);
    Object obj = intList; // 힙 오염 발생.

    List<Double> doubleList = (List<Double>) obj; 
    Double d = doubleList.get(0); // ClassCastException    
}
```
* 인수를 건네는 부분에 컴파일러가 생성한 형변환이 숨어 있음.
* 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.