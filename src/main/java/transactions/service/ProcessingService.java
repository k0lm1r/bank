package transactions.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import transactions.db.*;
import transactions.models.Transaction;

public class ProcessingService {
    private BlockingQueue<Transaction> transactionsQueue = new LinkedBlockingQueue<>();

    public ProcessingService () {
        new TransacitonDB().createTable();

        if (new AccountDB().createTable()) {

        }
    }

    public synchronized boolean addToQueue(Transaction newTransaction) {
        return transactionsQueue.add(newTransaction);
    }

    public void processQueue() {
        ExecutorService pool = Executors.newWorkStealingPool(4);
        
        
    }
}
