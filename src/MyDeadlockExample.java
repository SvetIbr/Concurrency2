import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class MyDeadlockExample {
    private static class MyResource {
        // Ресурсы
    }

    private final MyResource resourceA = new MyResource();
    private final MyResource resourceB = new MyResource();
    private final Lock lockA = new ReentrantLock();
    private final Lock lockB = new ReentrantLock();

    public void execute() {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                acquireResourcesAndWork(lockA, lockB, resourceA, resourceB, "Thread-1");
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                acquireResourcesAndWork(lockB, lockA, resourceB, resourceA, "Thread-2");
            }
        });

        thread1.start();
        thread2.start();
    }

    private void acquireResourcesAndWork(Lock firstLock, Lock secondLock,
                                         MyResource firstResource, MyResource secondResource, String threadName) {
        while (true) {
            boolean isLockedFirst = firstLock.tryLock();
            boolean isLockedSecond = false;

            if (isLockedFirst) {
                try {
                    System.out.println(threadName + " locked " + firstResource);

                    // Имитация работы с ресурсом
                    sleep(100);

                    isLockedSecond = secondLock.tryLock();

                    if (isLockedSecond) {
                        try {
                            System.out.println(threadName + " locked " + secondResource);

                            // Имитация работы с ресурсом
                            Thread.sleep(100);
                        } finally {
                            secondLock.unlock();
                            System.out.println(threadName + " unlocked " + secondResource);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    firstLock.unlock();
                    System.out.println(threadName + " unlocked " + firstResource);
                }

                if (isLockedFirst && isLockedSecond) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        MyDeadlockExample example = new MyDeadlockExample();
        example.execute();
    }

}
