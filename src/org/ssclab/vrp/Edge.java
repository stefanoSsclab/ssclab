package org.ssclab.vrp;


 final class Edge {
    int i, j;
    double guadagno;

    Edge(int i, int j, double guadagno) {
        this.i = i;
        this.j = j;
        this.guadagno = guadagno;
    }
    @Override
    public String toString() {
    	return "("+i+","+j+")";
    }
}
