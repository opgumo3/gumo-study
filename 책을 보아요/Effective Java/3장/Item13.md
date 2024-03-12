# clone 재정의는 주의해서 진행하라
clone 메서드를 잘 동작하게끔 해주는 구현 방법과 언제 그렇게 해야하는지, 그리고 가능한 다른 선택지에 관한 내용을 서술한다.
<hr>

* `Cloneable`은 복제해도 되는 클래스임을 명시하는 용도의 Mixin Interface지만 의도한 목적이 제대로 이루어지지 못했다.
* `clone` 메서드가 `Cloneable` 인터페이스가 아닌 Object에 protected로 선언되어있어서, </br> 인터페이스를 구현하더라고 clone메서드를 호출할 수 없다.

그렇다면 Cloneable 인터페이스가 하는 일은?
* Object의 protected 메서드인 clone의 동작 방식을 결정한다.

Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환하고, 그렇지 않다면 `CloneNotSupportedException`을 던진다.

* 인터페이스는 클래스가 인터페이스에서 정의한 기능을 제공한다고 선언하는 행위지만, `Cloneable`은 protected clone의 동작 방식을 결정한다.

아래는 Object 명세에서 가져온 clone 메서드의 일반 규약이다.
> (1) x.clone() != x </br>
(2) x.clone().getClass = x.getClass() </br>
(3) (Option) x.clone().equals(x) </br>
관례상, 이 메서드가 반환하는 객체는 super.clone을 호출해 얻어야 한다. </br>
관례상, 반환된 객체와 원본 객체는 독립적이어야 한다. 이를 만족하기 위해 super.clone으로 얻은 객체의 필드 중 하나 이상을 반환 전에 수정해야 할 수도 있다.

## 간단한 사용
```java
public class Cat {
    private String name;

    public Cat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

```java
public class Tiger extends Cat implements Cloneable {

    private int rank;

    public Tiger(String name, int rank) {
        super(name);
        this.rank = rank;
    }

    @Override
    protected Tiger clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
```

```java
public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {
        Tiger tiger = new Tiger("호랑이", 1);
        Tiger clone = tiger.clone();

        System.out.println(tiger == clone); // false
        System.out.println(tiger.getName() == clone.getName()); // true
    }
}
```

> 하지만 클래스가 가변 객체를 참조하는 순간...

```java
public class Tiger extends Cat implements Cloneable {

    private List<Person> fans = new ArrayList<>();
    private int rank;

    public Tiger(String name, int rank) {
        super(name);
        this.rank = rank;
    }

    public void follow(Person fan) {
        fans.add(fan);
    }

    public List<Person> getFans() {
        return fans;
    }

    @Override
    protected Tiger clone() throws CloneNotSupportedException {
        return (Tiger) super.clone();
    }
}
```
호랑이의 팬 리스트를 만들었다. Main으로 가보자.

```java
public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {
        Person person = new Person("1호팬");
        Tiger tiger = new Tiger("호랑이", 1);

        tiger.follow(person);

        Tiger clone = tiger.clone();

        System.out.println(tiger.getFans() == clone.getFans()); // true
    }
}
```
원본과 클론의 fans가 같은 참조를 가지면 안된다. 
</br>
그렇게 하려면, 리스트의 내부 정보를 복사해야 한다. 

```java
@Override
    protected Tiger clone() throws CloneNotSupportedException {
        Tiger clone = (Tiger) super.clone();
        List<Person> newFans = new ArrayList<>();
        newFans.addAll(fans);
        clone.fans = newFans;

        return clone;
    }
```
다시 정의하니 false값이 나온다.

* 배열을 복제할 때는, 배열.clone을 사용해라.

정리하자면.. 가변 객체를 복제한다면, clone을 호출하여 얻은 객체의 모든 필드를 초기 상태로 설정한 후, 원본 객체의 상태를 다시 생성하는 메서드들을 호출한다.

* 생성자에서는 재정의될 수 있는 메서드를 호출하지 않아야 하는데, clone에서도 마찬가지.

* Cloneable을 구현한 스레드 세이프 클래스를 작성한다면, clone 메서드도 적절히 동기화해줘야 한다.

* Cloneable을 구현한 클래스를 확장하는 상황이 아니라면 `복사 생성자(변환 생성자)`와 `복사 팩터리(변환 팩터리)` 방식으로 객체 복사 방식을 제공할 수 있다.

```java
public Yum(Yum yum) {
    ...
}
```

```java
public static Yum newInstance(Yum yum) {
    ...
}
```