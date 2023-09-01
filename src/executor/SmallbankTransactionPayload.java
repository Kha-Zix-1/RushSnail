package executor;

import java.util.HashMap;
import java.util.Map;

public class SmallbankTransactionPayload {
	static Map<Integer,Account> accountList = new HashMap<Integer, Account>();
	
	// The CreateAccountTransaction creates an account
	public boolean createAccountTransactionData(int customer_id,  String customer_name,  int savings_balance, int checking_balance) {
		if (accountList.get(customer_id) != null) {
			return false;
		}
		
		accountList.put(customer_id, new Account(customer_id, customer_name, savings_balance, checking_balance));
		
		return true;
	}
	
	// The DepositCheckingTransction adds an amount to the customer's
    // checking account.
	public boolean depositCheckingTransactionData(int customer_id, int amount) {
		Account targetAccount = accountList.get(customer_id);
		
		if (targetAccount == null) {
			return false;
		}
		
		targetAccount.set_checking_balance(targetAccount.get_checking_balance() + amount);
		return true;
	}
	
	// The WriteCheckTransaction removes an amount from the customer's
    // checking account
	public boolean writeCheckTransactionData(int customer_id, int amount) {
		Account targetAccount = accountList.get(customer_id);
		
		if (targetAccount == null) {
			return false;
		}
		
		if (targetAccount.get_checking_balance() - amount < 0) {
			return false;
		}
		
		targetAccount.set_checking_balance(targetAccount.get_checking_balance() - amount);
		return true;
	}
	
	// The TransactSavingsTransaction adds an amount to the customer's
    // savings account. Amount may be a negative number.
	public boolean transactSavingsTransactionData(int customer_id, int amount) {
		Account targetAccount = accountList.get(customer_id);
		
		if (targetAccount == null) {
			return false;
		}
		
		if (targetAccount.get_savings_balance() + amount < 0) {
			return false;
		}
		
		targetAccount.set_savings_balance(targetAccount.get_savings_balance() + amount);
		return true;
	}
	
	// The SendPaymentTransaction transfers an amount from one customer's
    // checking account to another customer's checking account.
	public boolean sendPaymentTransactionData(int source_customer_id, int dest_customer_id, int amount) {
		Account sourceAccount = accountList.get(source_customer_id);
		Account destAccount = accountList.get(dest_customer_id);
		
		if (sourceAccount == null || destAccount == null) {
			return false;
		}
		
		if (sourceAccount.get_checking_balance() - amount < 0) {
			return false;
		}
		
		sourceAccount.set_checking_balance(sourceAccount.get_checking_balance() - amount);
		destAccount.set_checking_balance(destAccount.get_checking_balance() + amount);
		return true;
	}
	
	// The AmalgamateTransaction transfers the entire contents of one
    // customer's savings account into another customer's checking
    // account.
	public void amalgamateTransactionData(int source_customer_id, int dest_customer_id) {
		Account sourceAccount = accountList.get(source_customer_id);
		Account destAccount = accountList.get(dest_customer_id);
		
		destAccount.set_checking_balance(destAccount.get_checking_balance() + sourceAccount.get_savings_balance());
		sourceAccount.set_savings_balance(0);
	}
}
