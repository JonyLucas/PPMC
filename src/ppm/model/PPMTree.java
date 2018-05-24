package ppm.model;

public class PPMTree {

    private int size, maxContext;
    private PPMNode root;

    public PPMTree(int maxContext){
        this.maxContext = maxContext;
        this.root = new PPMNode();
        this.size = 0;
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
        PPMNode currentNode;

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
                searchNode = auxNode;
                /**
                 * Caso o símbolo encontrado esteja no final da string e foi encontrado na árvore
                 * (o que indica que ele já foi encontrado antes dentro deste contexto),
                 * então incrementa a sua frequência.
                 **/
                if (searchLevel == maxLevel-1)
                    searchNode.incrementFrequency();
            }else{
                searchNode.addChild(symbols[searchLevel]);
                searchNode = searchNode.findChild(symbols[searchLevel]);
            }

            searchLevel++;

        }

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
