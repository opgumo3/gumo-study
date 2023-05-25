package org.example;

import org.example.annotation.ThisIsMain;
import org.example.annotation.ToDo;
import org.example.annotation.ToDos;

@ToDos({@ToDo("공부하기"), @ToDo("일하기")})
@ThisIsMain
public class Main {
    public static void main(String[] args) {

        // TODO 1-1  Object로 값을 받아보자. (p.671)
        ObjectClass objectClass = new ObjectClass();
        String objectString = (String) objectClass.setItem("Item is String");   // String을 넣더가도 형변환 해줘야 함.

        String o1 = (String) objectClass.setItem(3);    // ClassCastException(RuntimeException). 컴파일 시에는 모름



        // TODO 1-2  Generics를 사용해보자.
        GenericClass<String> differentType = new GenericClass<Integer>();

        GenericClass<String> genericClass = new GenericClass<>();
        genericClass.setItem(3);        // 이래서 타입 안정성?



        // TODO 1-3 이전의 코드와 호환을 위해..
        GenericClass otherClass = new GenericClass();
        Object o = otherClass.setItem(3);



        // TODO 1-4 etc .. 공변과 불공변 covariance contravariance
        Object[] testArray = new Long[2];
        testArray[0] = 1L;
        testArray[1] = "hi";    // 컴파일 때는 몰라요. ArrayStoreException(RuntimeException).

        GenericClass<?> differentType2 = new GenericClass<Integer>();   // ? 는 묵시적으로 extends Object 포함.



        // TODO 2-3 p.719 클래스에 적용된 애너테이션을 얻어보자.
        Class<Main> mainClass = Main.class;
        ThisIsMain annotation = mainClass.getAnnotation(ThisIsMain.class);
        String canonicalName = mainClass.getCanonicalName();    // org.example.Main
//        System.out.println(annotation.name() + canonicalName);
    }
}