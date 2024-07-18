package org.ssclab.vrp;

 final class Node {
  
	int numNode;
    double domanda;
    Route route;

    Node(int numNode, double domanda) {
        this.numNode = numNode;
        this.domanda = domanda;
        route=null;
    }
    Node(int numNode) {
        this.numNode = numNode;
        this.domanda = 0;
        route=null;
    }
    public int getNode() {
  		return numNode;
  	}
  	public double getDomand() {
  		return domanda;
  	}
}
