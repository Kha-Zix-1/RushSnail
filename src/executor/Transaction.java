package executor;

import java.util.ArrayList;

public class Transaction {
	
	// 5�ֲ�ͬ�Ľ�������
	public static final int DEPOSIT_CHECKING = 0;
	public static final int WRITE_CHECK = 1;
	public static final int TRANSACT_SAVINGS = 2;
	public static final int SEND_PAYMENT = 3;
	public static final int AMALGAMATE = 4;
    
	// ���׵���Ϣ�Լ�4�ֶ���д��
	private int txType, paramAccount1, paramAccount2;
    private ArrayList<Integer> conditionReadSetList= new ArrayList<>();	// ���������жϵĶ�����
    private ArrayList<Integer> readSetList= new ArrayList<>();			// �����Ķ�����
    private ArrayList<Integer> writeSetList = new ArrayList<>();		// ������д����
    private ArrayList<Integer> deltaWriteSetList = new ArrayList<>();   // ��ĳ���������ϻ��ȥĳ��ֵ��д����
    
    private boolean hasSameElements(ArrayList<Integer> list1, ArrayList<Integer> list2) {
    	ArrayList<Integer> common = new ArrayList<Integer>(list1);
    	common.retainAll(list2);
    	
    	return common.size() > 0;
    }
    
    public boolean isConflictWithTransactionInTranditionalWay(Transaction tx) {
    	return hasSameElements(this.readSetList, tx.getWriteSetList());
    }
    
    
    public boolean isConflictWithTransactionInSmartWay(Transaction tx) {
    	return hasSameElements(this.readSetList, tx.getWriteSetList())
    			|| hasSameElements(this.readSetList, tx.getDeltaWriteSetList())
    			|| hasSameElements(this.conditionReadSetList,tx.getWriteSetList())
    			|| hasSameElements(this.conditionReadSetList,tx.getDeltaWriteSetList());
    }
    
    public void setparamAccount1(int account) {
    	this.paramAccount1 = account;
    }
    
    public int getparamAccount1() {
    	return this.paramAccount1;
    }
    
    public void setparamAccount2(int account) {
    	this.paramAccount2 = account;
    }
    
    public int getparamAccount2() {
    	return this.paramAccount2;
    }
    
    public void setType(int type) {
    	this.txType = type;
    }
    
    public int getType() {
    	return this.txType;
    }
    
    public void addToConditionReadSetList(int variable) {
    	this.conditionReadSetList.add(variable);
    }
    
    public ArrayList<Integer> getConditionReadSetList() {
    	return this.conditionReadSetList;
    }
    
    public void addToReadSetList(int variable) {
    	this.readSetList.add(variable);
    }
    
    public ArrayList<Integer> getReadSetList() {
    	return this.readSetList;
    }
    
    public void addToWriteSetList(int variable) {
    	this.writeSetList.add(variable);
    }
    
    public ArrayList<Integer> getWriteSetList() {
    	return this.writeSetList;
    }
    
    public void addToDeltaWriteSetList(int variable) {
    	this.deltaWriteSetList.add(variable);
    }
    
    public ArrayList<Integer> getDeltaWriteSetList() {
    	return this.deltaWriteSetList;
    }
}
