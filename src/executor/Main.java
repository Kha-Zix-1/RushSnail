package executor;
import java.util.ArrayList;
import java.util.Collections;

import executor.Transaction;

public class Main {
	
	static private ArrayList<Integer> stateHasBeenWritten = new ArrayList<>();
	static private ArrayList<Transaction> traditionalTransactionList = new ArrayList<>();
	static private ArrayList<Transaction> smartTransactionList = new ArrayList<>();
	static private ArrayList<Integer> accountAccessList = new ArrayList<>();
	static private ArrayList<ArrayList<Integer>> traditionalTransactionCycles = new ArrayList<>();
	static private ArrayList<ArrayList<Integer>> smartTransactionCycles = new ArrayList<>();
	static private ArrayList<Integer> traditionalTransactionAbortList = new ArrayList<>();
	static private ArrayList<Integer> smartTransactionAbortList = new ArrayList<>();
	static private ArrayList<Integer> fabricTransactionAbortList = new ArrayList<>();
	
	// 账户数量，交易数量
	static int accountNum = 100000;
	static int transactionNum = 100;
	
	// 交易冲突图，Java默认数组初始化为0
	static int [][] traditionalTransactionConflictMap = new int [transactionNum][transactionNum];
	static int [][] smartTransactionConflictMap = new int [transactionNum][transactionNum];
	
	//zipfian分布的两个参数
	static double
	C = 2, alpha = 1.1;
	static TransactionGenerator generator = new TransactionGenerator(accountNum, C, alpha);
	
	public static void main(String[] args) {
		initAccounts();
		randomGenerateTransactions();
		
		fabricTransactionAbortList = fabricMVCCAbortTransactions();
		
		generateConflictGraph();
		
		traditionalTransactionCycles = getAllCycles(transactionNum, traditionalTransactionConflictMap);
		smartTransactionCycles = getAllCycles(transactionNum, smartTransactionConflictMap);
		
		traditionalTransactionAbortList = abortTransactions(traditionalTransactionCycles);
		smartTransactionAbortList = abortTransactions(smartTransactionCycles);
		
		System.out.println(fabricTransactionAbortList.size());
		System.out.println(traditionalTransactionAbortList.size());
		System.out.println(smartTransactionAbortList.size());
	}
	
	public static boolean isInWriteList(ArrayList<Integer> readList) {
		for (int i : readList) {
			if (stateHasBeenWritten.indexOf(i) != -1) {
				return true;
			}
		}
		
		return false;
	}
	
	public static void addToWriteList(ArrayList<Integer> writeList) {
		for (int i : writeList) {
			stateHasBeenWritten.add(i);
		}
	}
	
	public static ArrayList<Integer> fabricMVCCAbortTransactions() {
		ArrayList<Integer> abortedTransactions = new ArrayList<>();
		ArrayList<Integer> readList = new ArrayList<>();
		ArrayList<Integer> writeList = new ArrayList<>();
		
		for (int i = 0 ;i < transactionNum; i++) {
			readList = traditionalTransactionList.get(i).getReadSetList();
			
			if (isInWriteList(readList)) {
				abortedTransactions.add(i);
				continue;
			}
			
			writeList = traditionalTransactionList.get(i).getWriteSetList();
			addToWriteList(writeList);		
		}
		
		return abortedTransactions;
	}
	
	public static ArrayList<ArrayList<Integer>> getAllCycles(int transactionNum, int [][]conflictMap) {
		CircuitFinder CF = new CircuitFinder(transactionNum, conflictMap);
		CF.run();
		
		return CF.getCircuitList();
	}
	
	public static ArrayList<Integer> abortTransactions(ArrayList<ArrayList<Integer>> allCycles) {
		ArrayList<Integer> abortedTransactions = new ArrayList<>();
		
		while (!allCycles.isEmpty()) {
			int targetTransactionID = findBaddestTransaction(allCycles);
			abortedTransactions.add(targetTransactionID);
			removeCycleContainingSpecificTransaction(targetTransactionID, allCycles);
		}
		
		return abortedTransactions;
	}
	
	// 找到参与构成环次数最多的那一笔交易
	public static int findBaddestTransaction(ArrayList<ArrayList<Integer>> allCycles) {
		int [] transactionAppearNumList = new int [transactionNum];
		int cycleNum = allCycles.size();
		
		for (int i = 0; i < cycleNum; i++) {
			for (int j = 0; j < allCycles.get(i).size() - 1; j++) {
				transactionAppearNumList[allCycles.get(i).get(j)]++;
			}
		}
		
		int maxAppearNum = 0, baddestTransaction = 0;
		for (int i = 0; i < transactionNum; i++) {
			if (transactionAppearNumList[i] > maxAppearNum) {
				maxAppearNum = transactionAppearNumList[i];
				baddestTransaction = i;
			}
		}
		
		System.out.println("Abort transaction" + baddestTransaction);
		return baddestTransaction;
	}
	
	public static void removeCycleContainingSpecificTransaction(int transactionID, ArrayList<ArrayList<Integer>> cycles) {
		int cycleNum = cycles.size();
		
		for (int i = 0; i < cycleNum; i++) {
			if (cycles.get(i).indexOf(transactionID) != -1) {
				cycles.remove(i);
				i--;
				cycleNum--;
			}
		}
	}
	
	public static void traditionalProcessTransaction() {
		randomGenerateTransactions();
	}
	
	public static void initAccounts() {
		for (int i = 0; i < accountNum; i++) {
			Account account = new Account(i, "aoligei", 0, 0);
		}
	}
	
	public static void generateConflictGraph () {
		for (int i = 0; i < transactionNum; i++) {
			for (int j = 0; j < transactionNum; j++)
				if (j != i) {
					if (traditionalTransactionList.get(i).isConflictWithTransactionInTranditionalWay(traditionalTransactionList.get(j))) {
						traditionalTransactionConflictMap[i][j] = 1;
					}
					if (smartTransactionList.get(i).isConflictWithTransactionInSmartWay(smartTransactionList.get(j))) {
						smartTransactionConflictMap[i][j] = 1;
					}
				}
			
		}
	}
	
	public static void randomGenerateTransactions() {
		Transaction singleTraditionalTransaction = new Transaction();
		Transaction singleSmartTransaction = new Transaction();
		
		for (int i = 0; i < transactionNum; i++) {
			singleTraditionalTransaction = generator.randomGenerateSingleTransaction();
			
			if (singleTraditionalTransaction.getType() == Transaction.DEPOSIT_CHECKING
					|| singleTraditionalTransaction.getType() == Transaction.WRITE_CHECK
					|| singleTraditionalTransaction.getType() == Transaction.TRANSACT_SAVINGS) {
				accountAccessList.add(singleTraditionalTransaction.getparamAccount1());
								
			}
			else {
				accountAccessList.add(singleTraditionalTransaction.getparamAccount1());
				accountAccessList.add(singleTraditionalTransaction.getparamAccount2());
			}
			
			
			traditionalTransactionList.add(singleTraditionalTransaction);
			singleSmartTransaction = generator.reformTransaction(singleTraditionalTransaction);
			smartTransactionList.add(singleSmartTransaction);
		}
	}
	
}
