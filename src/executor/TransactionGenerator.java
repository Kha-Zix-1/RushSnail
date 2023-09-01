package executor;

import java.util.ArrayList;
import java.util.Random;

public class TransactionGenerator {
	
	// 账户数量，交易数量
	private int accountNum;
	
	// zipfian分布的三个参数
	private double C, alpha; 
	
	//每个变量的选择概率list
	private ArrayList<Double> accountAccessProbabilityList = new ArrayList<>();
	
	//将变量的选择概率求和，在生成随机数的时候映射到对应变量
	private ArrayList<Double> accountAccessProbabilitySumList = new ArrayList<>();
	
	double accountAccessProbabilitySum;
	
	public TransactionGenerator(int accountNum, double C, double alpha) {
		this.accountNum = accountNum;
		this.C = C;
		this.alpha = alpha;
		
		for (int i = 0; i < accountNum; i++) {
			accountAccessProbabilityList.add(this.C/(Math.pow(i + 1,this.alpha)));
			if (i == 0) {
				accountAccessProbabilitySumList.add(accountAccessProbabilityList.get(i));
			}
			else {
				accountAccessProbabilitySumList.add(accountAccessProbabilityList.get(i) + accountAccessProbabilitySumList.get(i - 1));
			}
		}
		
		accountAccessProbabilitySum = accountAccessProbabilitySumList.get(accountNum - 1);
				
	}
	
	public int randomGenerateTransactionType() {
		Random random = new Random();
		int txType;
		
		txType = random.nextInt(5);
		
		return txType;
	}
	
	public Transaction randomGenerateSingleTransaction() {
		Transaction tx = new Transaction();
		
		int txType = this.randomGenerateTransactionType();
		tx.setType(txType);
		
		switch (txType) {
		case Transaction.DEPOSIT_CHECKING:
			tx = this.randomGenerateDepositCheckingTransaction();
			break;
			
		case Transaction.TRANSACT_SAVINGS:
			tx = this.randomGenerateTransactSavingsTransaction();
			break;
			
		case Transaction.SEND_PAYMENT:
			tx = this.randomGenerateSendPaymentTransaction();
			break;
			
		case Transaction.WRITE_CHECK:
			tx = this.randomGenerateWriteCheckTransaction();
			break;
			
		case Transaction.AMALGAMATE:
			tx = this.randomGenerateAmalgamateTransaction();
			break;
			
		default:
			break;
		}
				
		return tx;
	}
	
	public int randomGenerateTransactionAccessAccount() {
		Random random = new Random();
		int targetAccount = 0;
		double accessIndex;
		
		accessIndex = random.nextDouble() * accountAccessProbabilitySum;
		
		for (int i = 0; i < accountNum; i++) {
			if (accountAccessProbabilitySumList.get(i) > accessIndex) {
				targetAccount = i;
				break;
			}
		}
		
		return targetAccount;
	}
	
	public Transaction reformTransaction(Transaction tx) {
		Transaction targetTx = new Transaction();
		
		switch (tx.getType()) {
		case Transaction.WRITE_CHECK:
			targetTx = this.reformWriteCheckTransaction(tx);
			break;
			
		case Transaction.TRANSACT_SAVINGS:
			targetTx = this.reformTransactSavingsTransaction(tx);
			break;
			
		case Transaction.DEPOSIT_CHECKING:
			targetTx = this.reformDepositCheckingTransaction(tx);
			break;
			
		case Transaction.SEND_PAYMENT:
			targetTx = this.reformSendPaymentTransaction(tx);
			break;
			
		case Transaction.AMALGAMATE:
			targetTx = this.reformAmalgamateTransaction(tx);
			break;
			
		default:
			break;
		}
		
		return targetTx;
	}
	
	// The DepositCheckingTransction adds an amount to the customer's
    // checking account.
	// The customer's checking account should be in read set, write set
	// and delta write set.
	public Transaction randomGenerateDepositCheckingTransaction() {
		Transaction tx = new Transaction();
		int targetAccountID;
		
		tx.setType(Transaction.DEPOSIT_CHECKING);
		targetAccountID = randomGenerateTransactionAccessAccount();
		tx.setparamAccount1(targetAccountID);
		
		/*
		tx.addToReadSetList(Account.accountList.get(targetAccountID).get_checking_balance().hashCode());
		tx.addToWriteSetList(Account.accountList.get(targetAccountID).get_checking_balance().hashCode());
		tx.addToDeltaWriteSetList(Account.accountList.get(targetAccountID).get_checking_balance().hashCode());
		*/
		
		tx.addToReadSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToDeltaWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		
		return tx;
	}
	
	// After reforming, just keep the delta write set for DepositCheckingTransaction
	public Transaction reformDepositCheckingTransaction(Transaction tx) {
		assert(tx.getType() == Transaction.DEPOSIT_CHECKING);
		
		Transaction targetTx = new Transaction();
		ArrayList<Integer> targetDeltaWriteSetList = new ArrayList<>(tx.getDeltaWriteSetList());
		int targetDeltaWriteSetListSize = targetDeltaWriteSetList.size();
		
		targetTx.setType(Transaction.DEPOSIT_CHECKING);
		
		for (int i = 0; i < targetDeltaWriteSetListSize; i++) {
			targetTx.addToDeltaWriteSetList(targetDeltaWriteSetList.get(i));
		}
		
		return targetTx;
	}
	
	// The WriteCheckTransaction removes an amount from the customer's
    // checking account.
	// The customer's checking account should be in read set, condition read set, 
	// write set and delta write set.
	public Transaction randomGenerateWriteCheckTransaction() {
		Transaction tx = new Transaction();
		int targetAccountID;
		
		tx.setType(Transaction.WRITE_CHECK);
		targetAccountID = randomGenerateTransactionAccessAccount();
		tx.setparamAccount1(targetAccountID);
		
		tx.addToConditionReadSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToReadSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToDeltaWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		
		return tx;
	}
	
	// After reforming, just keep the delta write set and condition read set for WriteCheckTransaction
	public Transaction reformWriteCheckTransaction(Transaction tx) {
		assert(tx.getType() == Transaction.WRITE_CHECK);
		
		Transaction targetTx = new Transaction();
		
		ArrayList<Integer> targetDeltaWriteSetList = new ArrayList<>(tx.getDeltaWriteSetList());
		ArrayList<Integer> targetConditionReadSetList = new ArrayList<>(tx.getConditionReadSetList());
		int targetDeltaWriteSetListSize = targetDeltaWriteSetList.size();
		int targetConditionReadSetListSize = targetConditionReadSetList.size();
		
		targetTx.setType(Transaction.WRITE_CHECK);
		
		for (int i = 0; i < targetDeltaWriteSetListSize; i++) {
			targetTx.addToDeltaWriteSetList(targetDeltaWriteSetList.get(i));
		}
		
		for (int i = 0; i < targetConditionReadSetListSize; i++) {
			targetTx.addToConditionReadSetList(targetConditionReadSetList.get(i));
		}
		
		return targetTx;
	}
	
	// The TransactSavingsTransaction adds an amount to the customer's
    // savings account. Amount may be a negative number.
	// The customer's savings account should be in read set, condition read set, 
	// write set and delta write set.
	public Transaction randomGenerateTransactSavingsTransaction() {
		Transaction tx = new Transaction();
		int targetAccountID;
		
		tx.setType(Transaction.TRANSACT_SAVINGS);
		targetAccountID = randomGenerateTransactionAccessAccount();
		tx.setparamAccount1(targetAccountID);
		
		tx.addToConditionReadSetList(targetAccountID * 10 + Account.savings_balance_Sym);
		tx.addToReadSetList(targetAccountID * 10 + Account.savings_balance_Sym);
		tx.addToWriteSetList(targetAccountID * 10 + Account.savings_balance_Sym);
		tx.addToDeltaWriteSetList(targetAccountID * 10 + Account.savings_balance_Sym);
		
		return tx;
	}
	
	// After reforming, just keep the delta write set and condition read set for TransactSavingsTransaction
	public Transaction reformTransactSavingsTransaction(Transaction tx) {
		assert(tx.getType() == Transaction.TRANSACT_SAVINGS);
		
		Transaction targetTx = new Transaction();
		
		ArrayList<Integer> targetDeltaWriteSetList = new ArrayList<>(tx.getDeltaWriteSetList());
		ArrayList<Integer> targetConditionReadSetList = new ArrayList<>(tx.getConditionReadSetList());
		int targetDeltaWriteSetListSize = targetDeltaWriteSetList.size();
		int targetConditionReadSetListSize = targetConditionReadSetList.size();
		
		targetTx.setType(Transaction.TRANSACT_SAVINGS);
		
		for (int i = 0; i < targetDeltaWriteSetListSize; i++) {
			targetTx.addToDeltaWriteSetList(targetDeltaWriteSetList.get(i));
		}
		
		for (int i = 0; i < targetConditionReadSetListSize; i++) {
			targetTx.addToConditionReadSetList(targetConditionReadSetList.get(i));
		}
		
		return targetTx;
	}
	
	// The SendPaymentTransaction transfers an amount from one customer's
    // checking account to another customer's checking account.
	// The customer1's and customer2's checking accounts should be in read set 
	// write set and delta write set.The customer1's checking account should 
	// be also in condition read set.
	public Transaction randomGenerateSendPaymentTransaction() {
		Transaction tx = new Transaction();
		int targetAccountID;
		
		tx.setType(Transaction.SEND_PAYMENT);
		targetAccountID = randomGenerateTransactionAccessAccount();
		tx.setparamAccount1(targetAccountID);
		tx.addToConditionReadSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToReadSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToDeltaWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		
		targetAccountID = randomGenerateTransactionAccessAccount();
		tx.setparamAccount2(targetAccountID);
		tx.addToReadSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToDeltaWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		
		return tx;
	}
	
	// After reforming, just keep the delta write set and condition read set for SendPaymentTransaction
	public Transaction reformSendPaymentTransaction(Transaction tx) {
		assert(tx.getType() == Transaction.SEND_PAYMENT);
		
		Transaction targetTx = new Transaction();
		
		ArrayList<Integer> targetDeltaWriteSetList = new ArrayList<>(tx.getDeltaWriteSetList());
		ArrayList<Integer> targetConditionReadSetList = new ArrayList<>(tx.getConditionReadSetList());
		int targetDeltaWriteSetListSize = targetDeltaWriteSetList.size();
		int targetConditionReadSetListSize = targetConditionReadSetList.size();
		
		targetTx.setType(Transaction.SEND_PAYMENT);
		
		for (int i = 0; i < targetDeltaWriteSetListSize; i++) {
			targetTx.addToDeltaWriteSetList(targetDeltaWriteSetList.get(i));
		}
		
		for (int i = 0; i < targetConditionReadSetListSize; i++) {
			targetTx.addToConditionReadSetList(targetConditionReadSetList.get(i));
		}
		
		return targetTx;
	}
	
	// The AmalgamateTransaction transfers the entire contents of one
    // customer's savings account into another customer's checking
    // account.
	// The customer1's savings account should be in write set and read set, and customer2's
	// checking account should be in read set, write set and delta write set.
	public Transaction randomGenerateAmalgamateTransaction() {
		Transaction tx = new Transaction();
		int targetAccountID;
		
		tx.setType(Transaction.AMALGAMATE);
		targetAccountID = randomGenerateTransactionAccessAccount();
		tx.setparamAccount1(targetAccountID);
		tx.addToReadSetList(targetAccountID * 10 + Account.savings_balance_Sym);
		tx.addToWriteSetList(targetAccountID * 10 + Account.savings_balance_Sym);
		
		targetAccountID = randomGenerateTransactionAccessAccount();
		tx.setparamAccount2(targetAccountID);
		tx.addToReadSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		tx.addToDeltaWriteSetList(targetAccountID * 10 + Account.checking_balance_Sym);
		
		return tx;
	}
	
	// After reforming, just keep the delta write set , read set and write set for AmalgamateTransaction
	public Transaction reformAmalgamateTransaction(Transaction tx) {
		assert(tx.getType() == Transaction.AMALGAMATE);

		Transaction targetTx = new Transaction();
		
		ArrayList<Integer> targetDeltaWriteSetList = new ArrayList<>(tx.getDeltaWriteSetList());
		ArrayList<Integer> targetReadSetList = new ArrayList<>(tx.getReadSetList());
		ArrayList<Integer> targetWriteSetList = new ArrayList<>(tx.getWriteSetList());
		int targetDeltaWriteSetListSize = targetDeltaWriteSetList.size();
		int targetReadSetListSize = targetReadSetList.size();
		int targetWriteSetListSize = targetWriteSetList.size();
		
		targetTx.setType(Transaction.AMALGAMATE);
		
		for (int i = 0; i < targetDeltaWriteSetListSize; i++) {
			targetTx.addToDeltaWriteSetList(targetDeltaWriteSetList.get(i));
		}
		
		for (int i = 0; i < targetReadSetListSize; i++) {
			targetTx.addToReadSetList(targetReadSetList.get(i));
		}
		
		for (int i = 0; i < targetWriteSetListSize; i++) {
			targetTx.addToWriteSetList(targetWriteSetList.get(i));
		}
		
		return targetTx;
	}
}
