# 불필요한 객체 생성을 피하라.
* 똑같은 기능의 객체를 매번 생성하기보다 객체 하나를 재사용하는 편이 나을 때가 많다!

## String
```java
String s = new String(""); // 실행될 때마다 새로운 인스턴스를 만든다.
String s = ""; // String pool. 하나의 String 인스턴스를 사용.
```

## 캐싱

```java
String s = "";
s.mathes(String regex)
```
이 경우에 matches 함수 내부에서는 regex를 Pattern 객체로 만들고 Pattern의 matcher() 함수를 호출하는데,
</br> Pattern은 매개변수로 받은 정규표현식에 해당하는 유한 상태 머신을 만들기 때문에 인스턴스 생성 비용이 높다.

이 경우에는 Pattern 인스턴스를 캐싱해서 사용하자.
```java
public class A {
    private static final Pattern pattern = Pattern.compile("...");

    // pattern.matcher()...
}
```
* 메서드가 처음 호출될 때 필드를 초기화하는 '지연초기화'는 코드가 복잡해지고, 성능은 크게 개선되지 않을 때가 많다.


## 오토박싱
* 박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자.
* 성능면으로 좋지 않다.

## 자체 객체 풀
* 코드를 헷갈리게 만들고 메모리 사용량을 늘리고 성능을 떨어뜨릴 수 있어서 자체 객체 풀은 만들지 말자. (아주 무거운 객체가 아닌 이상..)
* 데이터베이스 연결의 경우는 생성 비용이 워낙 비싸서 재사용하는 편이 낫다.