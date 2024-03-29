# equals를 재정의하려거든 hashCode도 재정의하라
같이 재정의하지 않으면, hashCode 일반 규약을 어겨, HashMap이나 HashSet 같은 컬렉션의 원소로 사용할 때 문제를 일으킬 것이다.

![hash](https://github.com/opgumo3/gumo-study/assets/38172794/58d94bb7-3d24-4c77-a6ac-3509f50272c7)
</br>refer : Tecoble </br>
>hashCode가 다르면 equals를 하기도 전에, 다른 객체라고 판단함.

아래는 Object 명세에 있는 규약이다.
* equals 비교에 사용되는 정보가 변경되지 않았다면, 어플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 항상 같은 값을 반환해야 한다.
* equals(Object)가 true라면 두 객체의 hashCode는 같아야 한다.
* equals(Object)가 false라도 두 객체의 hashCode가 다른 값일 필요는 없다. 하지만 다른 값을 반환해야 해시테이블의 성능이 좋아진다.

hashCode를 재정의 하지 않는다면, 두 번째 조항에 위배된다.
> 재정의 하지 않았을 때 사용되는 Object의 hashCode 메서드는 객체의 고유 주소 값을 바탕으로 반환하기 때문에 객체마다 다른 값을 반환한다. 

## hashCode 작성 요령.
이상적인 해시 함수는 주어인 인스턴스들을 32비트 정수 범위에 균일하게 분배해야 한다.

다음은 hashCode를 작성하는 간단한 요령이다.

1. int result = c; (핵심 필드를 2.a 방식으로 계산한 해시 코드)
> 핵심 필드 : equals 비교에 사용되는 필드
</br> <strong>핵심 필드 외의 필드는 계산에 반드시 제외해야 한다.</strong>

2. 핵심 필드 각각에 대해 아래의 해시코드 c를 계산하는 과정을 수행한다.
    1. 기본 타입 필드라면, Type.hashCode() 수행. </br> ex) Integer.hashCode()
    2. 참조 타입 필드라면, 참조 타입 클래스의 hashCode를 재귀적으로 호출하거나, null 이라면 (전통적으로) 0을 사용한다.
    3. 필드가 배열이라면, 배열의 핵심 원소 각각을 별도 필드처럼 다룬다. </br> 모든 원소가 핵심 원소라면 `Arrays.hashCode`를 사용하고, 배열에 핵심 원소가 없다면 상수(0을 추천)를 사용한다.
* 위에서 계산한 해시코드 c로 result를 갱신한다.
</br> result = 31 * result + c;

3. result 반환.

> <strong>작성한 hashCode가 두번째 규약을 지키는가?</strong>

* 계산식은 필드를 곱하는 순서에 따라 result 값이 달라진다. </br> String이라면 글자마다 계산을 하게되어, 구성하는 철자가 같지만 그 순서가 달라고 해시코드의 값이 달라진다.
* 계산식에서 31을 곱하는 이유는 홀수이면서 소수이기 때문이다. </br> 31을 `<<5 - i`로 최적화 할 수 있다.
* 해시 충돌이 적은 방법을 써야 한다면 `구아바의 Hasing`을 참고하자.
* `Objects.hash`를 사용하면 임의의 개수의 객체로 해시코드를 계산해준다. </br> 코드를 한 줄로 작성할 수 있지만 입력에 기본타입이 있다면 박싱과 언박싱을 거쳐야 하기 때문에 속도가 느리다.
* hashCode를 구하는 비용이 크다면 캐싱을 고려하자.
* HashCode가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말아라. 그래야 추후에 계산 방식을 바꿀 수 있다.
