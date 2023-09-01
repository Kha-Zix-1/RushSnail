package executor;

import java.util.ArrayList;

public class Node {
	// ��һά���˻�ID���ڶ�ά�����
	
	private ArrayList<Integer> cacheList = new ArrayList<>();
	
	// ���½ڵ�Ļ��棬����Map�ṹ���������ظ����������ظ���Ϊ���»��棬û���ظ���Ϊ���뻺��
	public void addToNodeCache(int targetStateID) {
		cacheList.add(targetStateID);
	}
	
	// ��ѯһ��account�Ƿ��ڽڵ㻺����
	public boolean isInCache(int targetStateID) {
		return cacheList.indexOf(targetStateID) != -1;
	}
}
