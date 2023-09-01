package executor;

import java.util.HashMap;
import java.util.Map;

public class Account {
	private Integer customer_id;

    // Customer Name
	private String customer_name;

    // Savings Balance (in cents to avoid float)
	private Integer savings_balance;

    // Checking Balance (in cents to avoid float)
	private Integer checking_balance;
	
	public static final int savings_balance_Sym = 1;
	public static final int checking_balance_Sym = 2;
    
    public static Map<Integer,Account> accountList = new HashMap<Integer, Account>();
    
    Account(int customer_id, String customer_name, int savings_balance, int checking_balance) {
    	this.customer_id = customer_id;
    	this.customer_name = customer_name;
    	this.savings_balance = savings_balance;
    	this.checking_balance = checking_balance;
    	accountList.put(this.customer_id, this);
    }
    
    public void set_customer_id(Integer customer_id) {
    	this.customer_id = customer_id;
    }
    
    public Integer get_customer_id() {
    	return this.customer_id;
    }
    
    public void set_customer_name(String customer_name) {
    	this.customer_name = customer_name;
    }
    
    public String get_customer_name() {
    	return this.customer_name;
    }
    
    public void set_savings_balance(Integer savings_balance) {
    	this.savings_balance = savings_balance;
    }
    
    public Integer get_savings_balance() {
    	return this.savings_balance;
    }
    
    public void set_checking_balance(Integer checking_balance) {
    	this.checking_balance = checking_balance;
    }
    
    public Integer get_checking_balance() {
    	return this.checking_balance;
    }
}
