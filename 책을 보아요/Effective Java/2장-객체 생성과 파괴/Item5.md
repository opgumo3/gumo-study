# 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라.
```java
public class SpellChecker {

    private static final Dictionary dictionary = new KoreanDictionary();

    // 객체 생성 방지
    private SpellChecker() {}

    public static boolean isRight(String word) {...}
}
```
맞춤법 검사기는 사전에 의존을 해야하는데, 위 코드처럼 단 하나의 사전을 사용한다고 가정하는 건 그리 좋지 않은 생각이다!

맞춤법 검사기가 여러 사전을 사용할 수 있도록 만들어보자.

### 1. Setter로 사전을 교체하는 방법
이 방법은 멀티스레드 환경에서 사용할 수 없다.
> ✔ 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.

### 2. 인스턴스를 생성할 때, 생성자에 필요한 자원을 넘겨주는 방법
```java
public class SpellChecker {
     private static final Dictionary dictionary = new KoreanDictionary();

    public SpellChecker(Dictionary dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isRight(String word) {...}
}
```
* 의존 객체 주입 패턴이라고 불린다.
* 생성자에 자원이 몇 개든 의존 관계가 어떻든 잘 작동하고, 불변을 보장하여 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있다.
* 생성자에 팩터리를 넘겨줄 수 있다.
> ✔ 팩터리 : 호출할 때마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체.


* 유연성과 테스트 용이성을 개선해주지만, 의존성이 많으면 코드를 어지럽게 만든다.
    * 대거, 주스, 스프링같은 의존 객체 주입 프레임워크를 사용하면 해소할 수 있음.

</br>

클라이언트가 제공한 팩터리가 생성한 타일들로 구성된 모자이크를 만드는 메서드..
```java
public abstract class Tile {
    abstract void process();
}
```

```java
public class RedTile extends Tile{
    @Override
    void process() {
        System.out.println("RedTile");
    }
}
```

```java
public abstract class TileFactory implements Supplier<Tile> {
    @Override
    public Tile get() {
        return createTile();
    }

    abstract Tile createTile();
}
```

```java
public class RedTileFactory extends TileFactory{
    @Override
    Tile createTile() {
        return new RedTile();
    }
}
```

```java
public class Mosaic {
    public static void createMosaic(Supplier<? extends Tile> tileFactory) {
        Tile tile = tileFactory.get();
        tile.process();
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Mosaic.createMosaic(new RedTileFactory());
    }
}
```