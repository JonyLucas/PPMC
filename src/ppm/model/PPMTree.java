package ppm.model;

public class PPMTree {

    private int MAX_SIZE = 10, maxContext;
    private int[] alphabet;
    private PPMNode root;

    public PPMTree(int maxContext, int[] alphabet) throws Exception {
        if (maxContext < 0 || maxContext > MAX_SIZE)
            throw new Exception("Valor do contexto inválido, este valor deve estar entre 0 e 10");

        this.alphabet = alphabet.clone();
        this.maxContext = maxContext;
        this.root = new PPMNode();
    }

    /**
     * Recebe uma substring da arquivo original, com o tamanho máximo do contexto (N), em seguida realiza inicialmente
     * a busca pelo maior comprimento e, posteriormente, decrementa o comprimento da mensagem, eliminando o caractere
     * inicial, e realiza-se uma nova busca, até que o comprimento da mensagem seja igual à 0.
     * A ideia é iniciar a pesquisa pelo maior contexto, caso exista determinado símbolo no contexto da busca, incrementa
     * o seu número de frequência, caso contrário, cria este símbolo neste contexto, iniciando sua frequência com 1.
     * Após a realização desta busca, descrementa o comprimento da string e repete a busca no contexto N-1.
     * @param contextMessage
     */
    public void findByContext(int[] contextMessage){
        int msgSize = contextMessage.length; // O comprimento da mensagem.
        String code = "";

        for(int i = 0; i < msgSize; i++){
            searchAndAdd(contextMessage);
            contextMessage = decrementMessage(contextMessage);
        }

    }

    /**
     * Realiza a busca do simbolo lido a partir da raiz da árvore
     * @param symbols
     * @return
     */
    private void searchAndAdd(int[] symbols){
        int searchLevel = 0; // Indica o nível de busca (contexto) na árvore.
        int maxLevel = symbols.length; // Corresponde ao contexto máximo (O tamanho da substring)
        PPMNode searchNode = this.root; // Inicia a busca pela raiz.
        PPMNode auxNode; // Nó auxiliar para realizar a verificação se o nó atual possui um nó filho com determinado símbolo

        while (searchLevel < maxLevel){
            if (symbols[searchLevel] == -1)
                break;

            auxNode = searchNode.findChild(symbols[searchLevel]);
            if(auxNode != null){
                getInterval(searchNode, symbols[searchLevel]);
                searchNode = auxNode;
                /**
                 * Caso o símbolo encontrado esteja no final da string e foi encontrado na árvore
                 * (o que indica que ele já foi encontrado antes dentro deste contexto),
                 * então incrementa a sua frequência.
                 **/
                if (searchLevel == maxLevel-1)
                    searchNode.incrementFrequency();
            }else{
                remainingSymbols(searchNode);
                searchNode.addChild(symbols[searchLevel]);
                searchNode = searchNode.findChild(symbols[searchLevel]);
            }

            searchLevel++;

        }

    }

    /**
     * Calcula a probabilidade do símbolo lido dentro do contexto do nó atual.
     *
     * @param currentNode
     * @param symbol
     * @return
     */
    private double getInterval(PPMNode currentNode, int symbol){
        int frequency = 0, total = 0, numChildren = currentNode.getChildren().size();
        double probability;

        for (PPMNode child : currentNode.getChildren()){
            total += child.getFrequency();
            frequency = (child.getSymbol() == symbol) ? child.getFrequency() : frequency;
        }

        if(numChildren == alphabet.length) // Indica que o nó atual possui todos os símbolos possíveis dentro do seu contexto (Desconsidera o rô)
            probability = frequency / ((double) total);
        else
            probability = frequency / ((double) total + (double) numChildren); // Considera-se o rô (que possui a frequência numChildren)

        System.out.println(probability); // Realizar o cálculo do codificador Aqui!
        return probability;
    }

    /**
     * Realiza o calculo da probabilidade dos símbolos que não estão presentes em determinado
     * contexto (corresponde ao calculo do Rô (P) da tabela do contexto). Esse método só é chamado
     * quando o símbolo lido não está presente no contexto atual.
     * OBS: Não realiza o critério de exclusão para o contexto à um nível acima na hierarquia da árvore.
     *
     * @param currentNode
     * @return
     */
    private double remainingSymbols(PPMNode currentNode){
        int frequencies = 0, numChildren = currentNode.getChildren().size();
        double probability;

        if (numChildren == 0)
            return 1;

        for (PPMNode child : currentNode.getChildren()){
            frequencies += child.getFrequency();
        }

        probability = numChildren / ((double) numChildren + (double) frequencies);
        System.out.println(probability); // Realizar o cálculo do codificador Aqui!
        return probability;
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

        PPMNode node = this.root;
        System.out.println(root.printNode(-1));

    }

}
