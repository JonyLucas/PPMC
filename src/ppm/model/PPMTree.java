package ppm.model;

public class PPMTree {

    private int size, maxContext;
    private PPMNode root, currentNode;

    public PPMTree(int maxContext){
        this.maxContext = maxContext;
        this.root = new PPMNode();
        this.size = 0;
    }



}
