package com.mycompany.app;

import org.multiverse.api.StmUtils;
import org.multiverse.api.Txn;
import org.multiverse.api.callables.TxnCallable;
import org.multiverse.api.references.TxnInteger;
import org.multiverse.api.references.TxnLong;

public class Account {

  private final TxnLong lastUpdate;
  private final TxnInteger balance;

  public Account(final int balance) {
    this.lastUpdate = StmUtils.newTxnLong(System.currentTimeMillis());
    this.balance = StmUtils.newTxnInteger(balance);
  }

  public void adjustBy(final int amount) {
    adjustBy(amount, System.currentTimeMillis());
  }

  public void adjustBy(final int amount, final long date) {
    StmUtils.atomic(new Runnable() {
      @Override
      public void run() {
        balance.increment(amount);
        lastUpdate.set(date);

        if (balance.get() < 0) {
          throw new IllegalArgumentException("Not enough money");
        }
      }
    });
  }

  @Override
  public String toString() {
    return StmUtils.atomic(new TxnCallable<String>() {
      @Override
      public String call(final Txn txn) throws Exception {
        return String.format("%d (as of %tF %<tT)", balance.get(txn), lastUpdate.get(txn));
      }
    });
  }
}