package ppm.decoder.model;

import java.util.ArrayList;

public class PPMNode {

    private int frequency, symbol, contextLevel;
    private ArrayList<PPMNode> children = new ArrayList<PPMNode>();

    public PPMNode(int symbol, int contextLevel){
        this.symbol = symbol;
        this.contextLevel = contextLevel;
        frequency = 1;
    }

    /**
     * Construtor utilizado na instanciação da raiz da árvore.
     */
    public PPMNode(){
        this(-1, -1);
    }

    public int getSymbol(){ return this.symbol; }

    public int getFrequency(){ return this.frequency; }

    public int getContextLevel() { return this.contextLevel; }

    public ArrayList<PPMNode> getChildren() { return this.children; }

    public void incrementFrequency(){ this.frequency++; }

    public void addChild(int symbol) {
        children.add(new PPMNode(symbol, this.contextLevel+1));
    }

    /**
     * Realiza a busca (em largura) do filho do nó corrente que possua o símbolo correspondente
     * ao símbolo passado como argumento.
     * @param symbol
     * @return
     */
    public PPMNode findChild(int symbol){
        for(PPMNode child : this.children){
            if(child.getSymbol() == symbol)
                return child;
        }

        return null;
    }

    /**
     * Realiza uma exibição formata do nó em questão e dos seus filhos
     * @param context
     * @return
     */
    public String printNode(int context){
        String description = "";
        for (int i = 0; i < context; i++)
            description += "\t"; // Realiza uma indentação por contexto

        description += "context: " + context + " - Symbol: [" + this.symbol + "] - Frequency: " + this.frequency + "\n";

        for (PPMNode child : children)
            description += child.printNode(context+1);

        return description;
    }

}
