package practiceTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;



public class TreeBuilder {
	private static final Random RANDOM = new Random(); 
	
	public static void buildTreeConstruct(){
		int resporitorySize = 100;
		Collection<Integer> resporitory = new HashSet<Integer>();
		for (int i = 0; i < resporitorySize; i++) {
			while(true){
				int tmp = RANDOM.nextInt(100);
				if(!resporitory.contains(tmp)){
					resporitory.add(tmp);
					break;
				}
			}
		}
		
		System.err.println("原始数据:");
		System.err.println(resporitory);
		System.err.println("==========================================================");		
		Tree tree = new Tree();
		for (int value : resporitory) {
			Node tmp = new Node();
			tmp.setValue(value);
			tree.addNode(tmp);
		}
		String orderStr = tree.treeOrder();
		String[] strArr = orderStr.split(",");
		if(strArr.length != resporitorySize){
			throw new RuntimeException("error!!");
		}
		
		int lastValue = 0;
		for (int i = 0; i < strArr.length; i++) {
			int nowValue = Integer.parseInt(strArr[i]);
			if(i == 0){
				lastValue = nowValue;
			}else{
				if(lastValue > nowValue){
					throw new RuntimeException("order error!");
				}else{
					lastValue = nowValue;
				}
			}
		}
		
		System.err.println("排序检查通过");
		System.err.println("==========================================================");	
		
		
		System.err.println("排序结果:");	
		System.err.println(orderStr);
		System.err.println("==========================================================");	
		System.err.println("树:");	
		tree.showTree();
	}
	
	public static void main(String[] args) {
		buildTreeConstruct();
		
		
	}
	
	static class Tree{
		Node root;

		public Tree() {
			
		}

		public Node getRoot() {
			return root;
		}

		public void setRoot(Node root) {
			this.root = root;
		}
		
		
		public void addNode(Node node){
			if(root == null){
				root = node;
				node.setBranch(-1);
				node.setRoot(true);
			}else{
				root.build(node);
				adjustTree();
			}
		}
		
		public void adjustTree(){
			if(root == null){
				return;
			}
			
			int leftDepth = root.getLeft() == null ? 0 : root.getLeft().getDepth();
			int rightDepth = root.getRight() == null ? 0 : root.getRight().getDepth();
			
			if(rightDepth - leftDepth >= 2){
				Node tmp = root;
				root = tmp.getRight();
				
				tmp.setParent(root);
				tmp.setRight(null);
				
				if(root.getLeft() != null){
					tmp.setRight(root.getLeft());
				}
				
				root.setLeft(tmp);
				root.getLeft().adjustBranch(0);
				root.setParent(null);
				root.setBranch(-1);
			}
			
			if(leftDepth - rightDepth >= 2){
				Node tmp = root;
				root = tmp.getLeft();
				
				tmp.setParent(root);
				tmp.setLeft(null);
				
				if(root.getRight() != null){
					tmp.setLeft(root.getRight());
				}
				
				root.setRight(tmp);
				root.getRight().adjustBranch(1);
				root.setParent(null);
				root.setBranch(-1);
			}
		}
		
		public void showTree(){
			Map<Integer, Map<Integer, List<Integer>>> showCollection = new HashMap<Integer, Map<Integer,List<Integer>>>();
			root.buildTreeInfo(showCollection, 1);
			
			
			StringBuilder messge = new StringBuilder();
			TreeSet<Integer> depthSet = new TreeSet<Integer>(showCollection.keySet());
			for (int depth : depthSet) {
				Map<Integer,List<Integer>> tmpList = showCollection.get(depth);
				if(tmpList.containsKey(-1)){
					messge.append(tmpList.get(-1).get(0)).append("\n");
				}else{
					List<Integer> leftNode = tmpList.get(0);
					if(leftNode == null){
						messge.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					}else{
						for (int left : leftNode) {
							messge.append(left).append("......");
						}
					}
					
				    messge.append("-------------------------------");
					List<Integer> rightNode = tmpList.get(1);
					if(rightNode == null){
						messge.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					}else{
						for (int right : rightNode) {
							messge.append(right).append("......");
						}
					}
					
					messge.append("\n");
				}
			}
			
			System.err.println(messge.toString());
			
		}
		
		public String treeOrder() {
			StringBuilder builder = new StringBuilder();
			root.buildInfo(builder);
			return builder.toString();
		}
	}
	
	static class Node{
		private int value;
		
		private Node parent;
		
		private Node left;
		
		private Node right;
		
		private int branch;//-1---root;0---左;---1是右
		
		private boolean isRoot = false;
		
		public Node() {

		}
		
		public int getDepth(){
			adjustSelf();
			int leftDepth = 0;
			int rightDepth = 0;
			
			if(left != null){
				leftDepth = left.getDepth();
			}
			
			if(right != null){
				rightDepth = right.getDepth();
			}
			
			return (leftDepth > rightDepth ? leftDepth : rightDepth) + 1;
		}
		
		public void adjustSelf(){
			int leftDepth = 0;
			int rightDepth = 0;
			if(parent == null){
				return;
			}
			
			if(left != null){
				leftDepth = left.getDepth();
			}
			
			if(right != null){
				rightDepth = right.getDepth();
			}
			
			
			if(rightDepth - leftDepth >= 2){
				boolean inLeft = parent.getLeft() == this ? true : false;
				Node oldRight = right;
				if(inLeft){
					parent.setLeft(oldRight);
				}else{
					parent.setRight(oldRight);
				}
				oldRight.setParent(parent);
				setParent(oldRight);
				setRight(oldRight.getLeft());
				oldRight.setLeft(this);
			}
			
			if(leftDepth - rightDepth >= 2){
				boolean inLeft = parent.getLeft() == this ? true : false;
				Node oldLeft = left;
				if(inLeft){
					parent.setLeft(oldLeft);
				}else{
					parent.setRight(oldLeft);
				}
				oldLeft.setParent(parent);
				setParent(oldLeft);
				setLeft(oldLeft.getRight());
				oldLeft.setRight(this);
			}
			
		}
		
		public void build(Node node){
			int nodeValue = node.getValue();
			if(nodeValue > value){
				if(right == null){
					right = node;
					node.setParent(this);
					if(isRoot){
						node.setBranch(1);
					}else{
						node.setBranch(getBranch());
					}
				}else{
					right.build(node);
				}
			}else if(nodeValue < value){
				if(left == null){
					left = node;
					node.setParent(this);
					if(isRoot){
						node.setBranch(0);
					}else{
						node.setBranch(getBranch());
					}
				}else{
					left.build(node);
				}
			}else{
				//TODO 相等时不做处理
			}
		}
		
		public void buildInfo(StringBuilder builder){
			if(builder == null){
				return;
			}
			
			if(left != null){
				left.buildInfo(builder);
			}
			if(isRoot){
				builder.append(value);
			}else{
				builder.append(",").append(value);
			}
			if(right != null){
				right.buildInfo(builder);
			}
			
		}
		
		public void buildTreeInfo(Map<Integer,Map<Integer,List<Integer>>> collections, int depth){
			if(collections == null){
				return;
			}
			
			if(depth == 1){
				List<Integer> tmplist = new ArrayList<Integer>();
				tmplist.add(getValue());
				Map<Integer,List<Integer>> tMap = new HashMap<Integer, List<Integer>>();
				tMap.put(-1, tmplist);
				collections.put(depth, tMap);
				
				if(left != null){
					left.buildTreeInfo(collections, depth + 1);
				}
				
				if(right != null){
					right.buildTreeInfo(collections, depth + 1); 
				}
			}else{
				Map<Integer,List<Integer>> tMap = collections.get(depth);
				if(tMap == null){
					tMap = new HashMap<Integer, List<Integer>>();
					collections.put(depth, tMap);
				}
				
				List<Integer> aimList = tMap.get(getBranch());
				if(aimList == null){
					aimList = new ArrayList<Integer>();
					tMap.put(getBranch(), aimList);
				}
				aimList.add(getValue());
				
				if(left != null){
					left.buildTreeInfo(collections, depth + 1);
				}
				
				if(right != null){
					right.buildTreeInfo(collections, depth + 1); 
				}
			}
		}
		
		public void adjustBranch(int branch){
			setBranch(branch);
			if(left != null){
				left.adjustBranch(branch);
			}
			
			if(right != null){
				right.adjustBranch(branch);
			}
		}
		
		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public Node getLeft() {
			return left;
		}

		public void setLeft(Node left) {
			this.left = left;
		}

		public Node getRight() {
			return right;
		}

		public void setRight(Node right) {
			this.right = right;
		}

		public int getBranch() {
			return branch;
		}

		public void setBranch(int branch) {
			this.branch = branch;
		}

		public boolean isRoot() {
			return isRoot;
		}

		public void setRoot(boolean isRoot) {
			this.isRoot = isRoot;
		}
		
		
	}
}
