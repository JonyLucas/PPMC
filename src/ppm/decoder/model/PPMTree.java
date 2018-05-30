package ppm.decoder.model;

import arithmeticCoding.decoder.ArithmeticDecoder;
import arithmeticCoding.tables.SimpleFrequencyTable;

import java.io.IOException;
import java.util.Arrays;

public class PPMTree {

    private int MAX_SIZE = 10, maxContext, contextLevel = 0;
    private int[] equivalentProbabilitySymbols, subString;
    private PPMNode root, currentNode;
    private ArithmeticDecoder decoder;

    /**
     * Construtor da árvore de decodificação do PPMC
     * @param decoder
     * @param maxContext
     * @throws Exception
     */
    public PPMTree(ArithmeticDecoder decoder, int maxContext) throws Exception {
        if (maxContext < 0 || maxContext > MAX_SIZE)
            throw new Exception("Valor do contexto inválido, este valor deve estar entre 0 e 10");

        this.maxContext = maxContext;
        this.root = new PPMNode();
        this.decoder = decoder;

        currentNode = root;

        // Substring que contêm os últimos K símbolos decodificados
        subString = new int[maxContext];
        Arrays.fill(subString, -1);

        equivalentProbabilitySymbols = new int[256];
        for (int i = 0; i < 256; i++)
            equivalentProbabilitySymbols[i] = i;

    }

    public void searchAndDecode(int[] symbols) throws IOException {
        int maxLevel = maxContext, numChildren = currentNode.getChildren().size();
        if(numChildren == 0 && currentNode.equals(root)){ // verifica se é o início da decodificação (Nó atual é a raiz, que ainda não possui filhos)
            findEquiProbContext();
        }

    }

    public PPMNode getInLowerLevel(){
        this.contextLevel--;
        if(contextLevel == -1){

        }
//        int[] lowerMessage()

        return null;
    }

    private int decode(){

        return 0;

    }

    public void constructAndAdd(){
        int[] contextMessage = subString.clone();
        int msgSize = contextMessage.length;

        for(int i = 0; i < msgSize; i++){
//            searchAndAdd(contextMessage);
            contextMessage = decrementMessage(contextMessage);
        }
    }

    private void searchAndAdd(int[] symbols) throws IOException {
        int searchLevel = 0; // Indica o nível de busca (contexto) na árvore.
        int maxLevel = symbols.length; // Corresponde ao contexto máximo (O tamanho da substring)
        PPMNode searchNode = this.root; // Inicia a busca pela raiz.
        PPMNode auxNode; // Nó auxiliar para realizar a verificação se o nó atual possui um nó filho com determinado símbolo

        while (searchLevel < maxLevel){
            if (symbols[searchLevel] == -1)
                break;

            auxNode = searchNode.findChild(symbols[searchLevel]);
            if(auxNode != null){
                /**
                 * Caso o símbolo encontrado esteja no final da string e foi encontrado na árvore
                 * (o que indica que ele já foi encontrado antes dentro deste contexto),
                 * então incrementa a sua frequência.
                 **/
                if (searchLevel == maxLevel-1){
//                    calculateInterval(searchNode, symbols[searchLevel]);
                    System.out.println("Increment Symbol: " + symbols[searchLevel] + " in context: " + searchNode.getSymbol());
                    searchNode = auxNode;
                    searchNode.incrementFrequency();
                }else {
                    searchNode = auxNode;
                }
            }else{
                remainingSymbols(searchNode, symbols[searchLevel]);
                System.out.println("Add Symbol: " + symbols[searchLevel] + " in context: " + searchNode.getSymbol() + "\n");
                searchNode.addChild(symbols[searchLevel]);
                searchNode = searchNode.findChild(symbols[searchLevel]);
            }

            searchLevel++;

        }

    }

    private void remainingSymbols(PPMNode currentNode, int symbol) throws IOException {
        int i = 0, total = 0, numChildren = currentNode.getChildren().size();
        int frequencies[] = new int[numChildren+1]; // Inclui as frequências dos símbolos dos nós filhos e do rô (numChildren)
        String tableDescription = "";

        System.out.println("Current context: " + currentNode.getSymbol());

        if (numChildren == 0){ // Em caso do contexto atual não possuir símbolos, não computa o intervalo no codificador aritmético
            if(currentNode.equals(this.root))
//                findEquiProbContext(symbol); // No caso do nó atual ser a raiz, faz-se a busca do símbolo no contexto de equiprobabilidade (K = -1)
            return;
        }

        for (PPMNode child : currentNode.getChildren()){
            tableDescription += i + ": " + child.getSymbol() + ", ";
            total += child.getFrequency();
            frequencies[i++] = child.getFrequency();
        }

        frequencies[i] = numChildren; // Inclui a frequencia do Rô (igual ao número de filhos do nó)
        tableDescription += i + ": " + "Ro";

        System.out.println(tableDescription);

        SimpleFrequencyTable frequencyTable = new SimpleFrequencyTable(frequencies);
        System.out.println(frequencyTable);
//        encoder.write(frequencyTable, i); // Escreve o intervalo do rô (P), por meio codificador aritmético, no arquivo

//        if(currentNode.equals(this.root))
//            findEquiProbContext(symbol); // No caso do nó atual ser a raiz, faz-se a busca do símbolo no contexto de equiprobabilidade (K = -1)


    }

    private int findEquiProbContext() throws IOException {
        int j = 0, symbol = 0, index = 0, size = equivalentProbabilitySymbols.length;
        int [] frequencies = new int[size], remainingSymbols = new int[size-1];

        System.out.println("Equivalent probable context (K = -1)");
        Arrays.fill(frequencies, 1);

        index = decoder.read(new SimpleFrequencyTable(frequencies));
        symbol = equivalentProbabilitySymbols[index];

        for (int i = 0; i < size; i++){
            if (equivalentProbabilitySymbols[i] != symbol)
                remainingSymbols[j++] = equivalentProbabilitySymbols[i];
        }

        System.out.println("Simbolo removido: " + symbol);

        this.equivalentProbabilitySymbols = remainingSymbols;

        return symbol;

    }

    /**
     * Cria uma sub-mensagem da mensagem passada como argumento, com comprimento reduzido em um elemento:
     * Comprimento(novaMsg) = Comprimento(msgOriginal) - 1.
     * E eliminando o primeiro caractere da mensagem original.
     * @param message
     * @return
     */
    private int[] decrementMessage(int[] message){
        int newSize = message.length - 1;
        int[] newMessage = new int[newSize]; //Cria um array com o comprimento (n) da mensagem inicial - 1

        for (int i = 0; i < newSize; i++){
            newMessage[i] = message[i+1];
        }

        return newMessage;
    }

    public void showTree(){
        System.out.println(root.printNode(-1));
    }


}
