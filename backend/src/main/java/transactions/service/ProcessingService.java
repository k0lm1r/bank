package transactions.service;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import transactions.db.*;
import transactions.models.*;

public class ProcessingService {
    private BlockingQueue<Transaction> transactionsQueue = new LinkedBlockingQueue<>();
    private AtomicBoolean isProcessingRunning = new AtomicBoolean(false);

    public ProcessingService () {
        if (AccountDB.createTable()) {
            for (int i = 0; i < 5; ++i)
                AccountDB.insertAccount(new Account(BigDecimal.valueOf(10000)));
        }

        TransactionDB.createTable();
    }

    public synchronized void addToQueue(Transaction newTransaction) {
        transactionsQueue.add(newTransaction);

        if (!isProcessingRunning.get()) {
            isProcessingRunning.set(true);
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
        isProcessingRunning.set(false);
    }

    private void updateAllBalances() {
        long count = AccountDB.getCount();

        for (long i = 1; i <= count; ++i)
            AccountDB.updateBalance(i, TransactionDB.takeDelta(i));
    }

    public void stop() {
        while (isProcessingRunning.get() || !transactionsQueue.isEmpty());
        updateAllBalances();
        Database.close();
    }
}