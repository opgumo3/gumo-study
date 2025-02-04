```java
public class IntCalculator extends RecursiveTask<Integer> {

    private final int[] ints;
    private int start = 0;
    private int end = 0;
    private final static int THRESHOLD = 5; // 서브태스크 크기

    public IntCalculator(int[] ints) {
        this(ints, 0, ints.length);
    }

    private IntCalculator(int[] ints, int start, int end) {
        this.ints = ints;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int length = end - start;

        if (length < THRESHOLD) {
            return getSum();
        }

        IntCalculator leftTask = new IntCalculator(this.ints, start, start + length / 2);
        leftTask.fork(); // ForkJoinPool 의 스레드로 태스크를 비동기로 실행.

        IntCalculator rightTask = new IntCalculator(this.ints, start + length / 2, end);
        Integer rightResult = rightTask.compute();
        Integer leftResult = leftTask.join(); // leftTask 의 결과가 없으면 기다리고, 있다면 읽음.

        return leftResult + rightResult;
    }

    private Integer getSum() {
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += ints[i];
        }
        System.out.printf(Thread.currentThread().getName() + " -- start %d, end %d, sum %d \n", start, end, sum);
        return sum;
    }
}
```

```
# 0~25 까지의 Intstream 에 대해 compute 했을 때.
main -- start 21, end 25, sum 90 
ForkJoinPool.commonPool-worker-4 -- start 6, end 9, sum 21 
ForkJoinPool.commonPool-worker-5 -- start 18, end 21, sum 57 
ForkJoinPool.commonPool-worker-2 -- start 15, end 18, sum 48 
ForkJoinPool.commonPool-worker-3 -- start 3, end 6, sum 12 
ForkJoinPool.commonPool-worker-1 -- start 9, end 12, sum 30 
ForkJoinPool.commonPool-worker-5 -- start 12, end 15, sum 39 
ForkJoinPool.commonPool-worker-4 -- start 0, end 3, sum 3 

---
ForkJoinPool.commonPool-worker-1 -- start 9, end 12, sum 30 
ForkJoinPool.commonPool-worker-5 -- start 6, end 9, sum 21 
ForkJoinPool.commonPool-worker-4 -- start 18, end 21, sum 57 
ForkJoinPool.commonPool-worker-2 -- start 15, end 18, sum 48 
ForkJoinPool.commonPool-worker-3 -- start 3, end 6, sum 12 
main -- start 21, end 25, sum 90 
ForkJoinPool.commonPool-worker-5 -- start 12, end 15, sum 39 
ForkJoinPool.commonPool-worker-1 -- start 0, end 3, sum 3 
```