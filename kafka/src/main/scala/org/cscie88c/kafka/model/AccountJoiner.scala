package org.cscie88c.kafka.model

import org.apache.kafka.streams.kstream.ValueJoiner

case class TransactionWithAccount(txn: Transaction, acct: String)
class AccountJoiner extends ValueJoiner[Transaction,String,TransactionWithAccount]{

  override def apply(txn: Transaction, acct: String): TransactionWithAccount = {
    TransactionWithAccount(txn, acct) 
  }
}
