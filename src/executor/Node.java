package executor;

import java.util.ArrayList;

public class Node {
	// 第一维是账户ID，第二维是余额
	
	private ArrayList<Integer> cacheList = new ArrayList<>();
	
	// 更新节点的缓存，由于Map结构本身不允许重复，因此如果重复则为更新缓存，没有重复则为插入缓存
	public void addToNodeCache(int targetStateID) {
		cacheList.add(targetStateID);
	}
	
	// 查询一个account是否在节点缓存中
	public boolean isInCache(int targetStateID) {
		return cacheList.indexOf(targetStateID) != -1;
	}
}
