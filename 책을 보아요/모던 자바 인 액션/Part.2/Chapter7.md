# Chapter 7. 병렬 데이터 처리와 성능

## 병렬 스트림


## JMV

### dependency
```xml
<dependencies>
    <!-- https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-core -->
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-core</artifactId>
        <version>1.37</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-generator-annprocess -->
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-generator-annprocess</artifactId>
        <version>1.37</version>
    </dependency>
</dependencies>
```

### code
```java
@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(
    value = 2,
    jvmArgs = {"-Xms2G", "-Xmx2G"}
)
public class BenchMarkTest {
    
    @Benchmark
    public int testParallelStream() {
        return this.parallelStream();
    }
}
```

- `@TearDown annotation is placed within a class not having @State annotation. This is prohibited because it would have no effect.`