package ppm.model;

import java.util.ArrayList;

public class PPMNode {

    private int frequency, symbol;
    private PPMNode parent, vinePointer;
    private ArrayList<PPMNode> children = new ArrayList<PPMNode>();

    public PPMNode(PPMNode parent, int symbol){
        this.parent = parent;
        this.symbol = symbol;
        frequency = 0;
    }

    public PPMNode(){
        this(null, -1);
    }

    public int getSymbol(){ return this.symbol; }
    public int getFrequency(){ return this.frequency; }
    public ArrayList<PPMNode> getChildren() { return this.children; }


}
