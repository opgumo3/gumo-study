# finalizer와 cleaner 사용을 피하라
Finalizer와 Cleaner는 자바의 객체 소멸자다. (java.lang.ref)

* Finalizer는 예측할 수 없고, 상황에 따라 위험할 수 있다.
* Java 9 부터는 Finalizer는 deprecated로 지정되었다.
* Finalizer의 대안인 Cleaner는 Finalizer보다는 덜 위험하지만 예측할 수 없고 느리다.

</br>

## 쓰지말라는데는 이유가 있다.

* 즉시 수행된다는 보장이 없다.
</br> 제때 실행되어야 한다면 Finalizer, Cleaner는 비추천.


* Finalizer 스레드는 다른 어플리케이션 스레드보다 우선 순위가 낮아서 작업이 계속 밀릴 수 있다.
* Cleaner는 자신을 수행할 스레드를 제어할 수 있지만, 결국 GC 통제하에 있어 즉시 수행될 보장은 없다.

> ✔ 상태를 영구적으로 수정하는 작업에서는 절대 Finalizer나 Cleaner에 의존해서는 안된다.
</br> ex) 파일 닫기..

* System.gc, System.runFinalization 메서드는 앞의 객체 소멸자보다 실행 가능성을 높여줄 수 있지만 보장하지 않는다.

* Finalizer 동작 중 발생한 예외는 무시되며, 처리할 작업이 남았더라고 그 순간 종료될 수 있다. 스택 추적 내역 출력조차 안한다. Cleaner는 자신의 스레드를 통제해서 경고는 출력해준다.

* 성능이 좋지 않다. AutoCloseable과 비교했을 때, 성능 차이가 심하다.

* Finalizer를 사용한 클래스는 Finalizer 공격에 노출되어 심각한 보안 문제를 일으킬 수 있다.

## AutoCloseable을 구현하세요.
* 인스턴스를 다 사용하면 close 메서드를 호출하면 된다.
* 인스턴스가 닫혔는지 추적하기에 좋다.

## 존재의 이유
* AutoCloseable의 close 메소드의 안전망 역할로 Cleaner와 Finalizer를 사용할 수 있다.
* 네이티브 객체를 회수 할 때. (GC는 자바 객체만 회수한다.)