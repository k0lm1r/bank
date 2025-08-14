package transactions.service;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import transactions.db.*;
import transactions.models.*;

public class ProcessingService {
    private BlockingQueue<Transaction> transactionsQueue = new LinkedBlockingQueue<>();
    private boolean isProcessingRunning = false;

    public ProcessingService () {
        new TransactionDB().createTable();

        if (new AccountDB().createTable())
            for (int i = 0; i < 5; ++i)
                AccountDB.insertAccount(new Account(BigDecimal.valueOf(10000)));
    }

    public synchronized void addToQueue(Transaction newTransaction) {
        transactionsQueue.add(newTransaction);

        if (!isProcessingRunning) {
            isProcessingRunning = true;
            CompletableFuture.runAsync(() -> this.processQueue());
        }
    }

    public void processQueue() {
        ExecutorService pool = Executors.newWorkStealingPool(4);
        
        while (!transactionsQueue.isEmpty()) {
            pool.execute(() -> {
                try {
                    TransactionDB.insertTransaction(transactionsQueue.take());
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage() + " " + Thread.currentThread().getName());
                }
            });
        }

        try {
            pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            pool.shutdown();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        isProcessingRunning = false;
    }
}