package org.ssclab.vrp;

 public final class Node {
  
	public final int numNode;
    @Override
	public String toString() {
		return String.valueOf(numNode) ;
	}
	public final double demand;
    Route route;

    Node(int numNode, double domanda) {
        this.numNode = numNode;
        this.demand = domanda;
        route=null;
    }
    Node(int numNode) {
        this.numNode = numNode;
        this.demand = 0;
        route=null;
    }
    public int getNode() {
  		return numNode;
  	}
  	public double getDomand() {
  		return demand;
  	}
}
