package transactions;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import transactions.models.Transaction;
import transactions.service.ProcessingService;

public class Main {
    public static void main(String[] args) {
        ProcessingService service = new ProcessingService();
        ExecutorService clientsPool = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(10);
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; ++i) {
            clientsPool.execute(() -> {
                service.addToQueue(new Transaction(BigDecimal.valueOf(new Random().nextDouble(1000)),
                        new Random().nextInt(5) + 1, new Random().nextInt(5) + 1));
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage() + "in clients");
        }

        service.stop();
        clientsPool.shutdown();

        long end = System.currentTimeMillis();
        System.out.println("complite " + (end - start));
    }
}
