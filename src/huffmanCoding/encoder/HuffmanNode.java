package huffmanCoding.encoder;

import java.util.ArrayList;

public class HuffmanNode implements Comparable<HuffmanNode>{

    private int frequencySum = 0;
    private boolean isSheet;
    private HuffmanNode leftSon, rightSon;
    private ArrayList<Integer> symbols = new ArrayList<>();


    public HuffmanNode(HuffmanNode rightSon, HuffmanNode leftSon){
        this.leftSon = leftSon;
        this.rightSon = rightSon;
        isSheet = false;
        frequencySum = leftSon.getFrequency() + rightSon.getFrequency();
        symbols.addAll(rightSon.getSymbols());
        symbols.addAll(leftSon.getSymbols());
    }

    public HuffmanNode(int symbol, int frequency) {
        symbols.add(symbol);
        frequencySum = frequency;
        isSheet = true;
    }

    public int getFrequency(){ return this.frequencySum; }

    public ArrayList<Integer> getSymbols() { return symbols; }

    public HuffmanNode getLeftSon() { return leftSon; }

    public HuffmanNode getRightSon() { return rightSon; }

    public boolean hasSymbol(int symbol){ return symbols.contains(symbol); }

    @Override
    public int compareTo(HuffmanNode node) {
        if(this.getFrequency() > node.getFrequency())
            return 1;
        else if(this.getFrequency() < node.getFrequency())
            return -1;
        else
            return 0;
    }
}
