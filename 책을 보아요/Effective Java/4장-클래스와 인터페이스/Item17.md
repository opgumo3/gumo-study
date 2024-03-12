# 변경 가능성을 최소화하라.
> 불변 클래스 : 인스턴스 내부 값을 수정할 수 없는 클래스
자바에서는 String, 기본 타입의 박싱 클래스들, BingInteger, BigDecimal이 있음.

불변 클래스로 설계한 이유?

가변 클래스보다 설계하고 구현하고 사용하기 쉽고, 오류가 생길 여지도 적어 훨씬 안전함
또한 불변 객체는 근본적으로 스레드 세이프하여 따로 동기화할 필요없다. 여러 스레드가 동시에 사용해도 절대 훼손 되지 않는다.

불변 객체에 대해 스레드 간에 영향을 줄 수 없으니 불변 객체는 안심하고 공유할 수 있다.

그래서 불변 클래스의 인스턴스는 최대한 재활용하기를 권한다.

가장 쉬운 재활용 방법은 상수로 제공하는 것. `public static final`


## 클래스를 불변으로 만들기 위한 다섯 가지 규칙
### 1. 객체의 상태를 변경하는 메서드(변경자, setter)를 제공하지 않는다

### 2. 클래스를 확장할 수 없도록 한다.
하위에서 객체의 상태를 변하게 만드는 상황을 막는다. 클래스를 final로 선언하면 되지만 다른 방법도 뒤에 있따.

#### 1. 생성자를 private, default로 만들고 public 정적 팩터리를 제공한다.

### 3. 모든 필드를 final로 선언한다.

### 4. 모든 필드를 private으로 선언한다.
필드가 참조하는 가변 객체를 직접 접근해 수정하는 일을 막아줌.

### 5. 자신 외에 내부의 가변 컴포넌트에 접근할 수 없도록 한다.
가변 컴포넌트 참조로 변경이 일어날 수 있기 때문에, 접근자 메서드가 가변 컴포넌트를 그대로 반환해서는 안된다.


주로 자주 사용되는 인스턴스를 캐싱해서 같은 인스턴스를 중복 생성하기 않게 해주는 정적 팩터리를 제공할 수 있다.
박싱된 기본 타입 클래스 전부와 BigInteger가 여기에 속한다고 함.
```java
@HotSpotIntrinsicCandidate
    public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }


private static class IntegerCache {
        static final int low = -128;
        static final int high;
        static final Integer cache[];

        static {
            // high value may be configured by property
            int h = 127;
            String integerCacheHighPropValue =
                VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
            if (integerCacheHighPropValue != null) {
                try {
                    int i = parseInt(integerCacheHighPropValue);
                    i = Math.max(i, 127);
                    // Maximum array size is Integer.MAX_VALUE
                    h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
                } catch( NumberFormatException nfe) {
                    // If the property cannot be parsed into an int, ignore it.
                }
            }
            high = h;

            cache = new Integer[(high - low) + 1];
            int j = low;
            for(int k = 0; k < cache.length; k++)
                cache[k] = new Integer(j++);

            // range [-128, 127] must be interned (JLS7 5.1.7)
            assert IntegerCache.high >= 127;
        }

        private IntegerCache() {}
    }
```

이렇게 하면 메모리 사용량과 GC 비용이 줄어들겠죠

불변 객체를 사용하면 방어적 복사도 필요 없다는 결론으로 자연스럽게 이어진다.
불변 클래스는 clone 메서드나 복사 생성자는 제공하지 않는게 종다.

암튼 불변 객체끼리는 내부 데이터를 공유할 수 있다. 자유ㅂ게 공유할 수있다는 것의 연장선으로 

불변 객체는 맵의 키오 ㅏ집합의 원소를 쓰기에 안성 맞춤이다.
맵이나 집ㅇ합은 안에 담긴 값이 바뀌면 불변식이 허물어지는데 불변 객체를 사용하면 그런 걱정은 하지 않아도 된다.

불변 객체는 그 자체로 싪래 원자성(메서드에서 예외가 발생한 후에도 극 객체는 호출 전과 같은 상태여야한다는 성질.  ) 을 제공한다.

불변 객체의 단점으로는 값이 달면 반드시 독립된 객체로 만들어야 하는 것.
값의 가짓수가 많다면 이들을 모두 만들어야 함.

백만 비트에서 하나의 비트만 바꾸는데 새로운 인스턴스가 필요하는 것.
BigInteger, BitSet


다단계 연산? 을 예측 하여 기본 기능으로 제공. BigInter는 모듈러 지수 같은 다단계 연산 속도를 높여주는 가변 동반 클래스가 있음.
String의 경우에는 StringBuilder

> 합당한 이유가 없다면 모든 필드느 private final이어야 한다.