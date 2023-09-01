package executor;

import java.util.ArrayList;

// https://github.com/hellogcc/circuit-finding-algorithm/blob/master/CircuitFinder.h

public class CircuitFinder {
	ArrayList<ArrayList<Integer>> AK = new ArrayList<>();
	ArrayList<Integer> Stack = new ArrayList<>();
	ArrayList<Boolean> Blocked = new ArrayList<>();
	ArrayList<ArrayList<Integer>> BlockMap = new ArrayList<>();
	ArrayList<ArrayList<Integer>> circuitList = new ArrayList<>();
	int S, transactionNum;
	
	public CircuitFinder(int transactionNum, int[][] conflictMap) {
		this.transactionNum = transactionNum;
		
		for (int i = 0; i < transactionNum; i++) {
			AK.add(new ArrayList<>());
			Blocked.add(false);
			BlockMap.add(new ArrayList<>());
			for (int j = 0; j < transactionNum; j++) {
				if (conflictMap[i][j] != 0) {
					AK.get(i).add(j + 1);
				}
			}
		}
	}
	
	public void unblock(int U) {
		Blocked.set(U - 1, false);
		
		while (!BlockMap.get(U - 1).isEmpty()) {
			int W = BlockMap.get(U - 1).get(0);
			BlockMap.get(U - 1).remove(0);
			
			if (Blocked.get(W - 1)) {
				unblock(W);
			}
		}
	}
	
	public boolean circuit(int V) {
		boolean F = false;
		Stack.add(V);
		Blocked.set(V - 1, true);
		
		for (int W : AK.get(V - 1)) {
			if (W == S) {
				output();
				F = true;
			}
			else if (W > S && !Blocked.get(W - 1)) {
				F = circuit(W);
			}
		}
		
		if (F) {
			unblock(V);
		}
		else {
			for (int W: AK.get(V - 1)) {
				int IT = BlockMap.get(W - 1).indexOf(V);
				if (IT == -1) {
					BlockMap.get(W - 1).add(V);
				}
			}
		}
		
		Stack.remove(Stack.size() - 1);
		
		return F;
	}
	
	public void output() {
		ArrayList<Integer> newCircuit = new ArrayList<>();
		
		System.out.print("circuit: "); 
		
		for (int i : Stack) {
			System.out.print(i - 1 + "->");
			newCircuit.add(i - 1);
		}
		
		System.out.println(Stack.get(0) - 1);
		newCircuit.add(Stack.get(0) - 1);
		
		this.circuitList.add(newCircuit);
	}
	
	public ArrayList<ArrayList<Integer>> getCircuitList() {
		return this.circuitList;
	}
	
	public void run() {
		Stack.clear();
		S = 1;
		
		while (S < this.transactionNum) {
			for (int i = S; i <= this.transactionNum; i++) {
				Blocked.set(i - 1, false);
				BlockMap.get(i - 1).clear();
			}
			circuit(S);
			S++;
		}
	}
	
}
