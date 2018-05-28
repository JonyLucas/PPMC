package ppm.model;

import arithmeticCoding.encoder.ArithmeticEncoder;
import arithmeticCoding.tables.SimpleFrequencyTable;

import java.io.IOException;
import java.util.ArrayList;

public class PPMTree {

    private int MAX_SIZE = 10, maxContext;
    private int[] alphabet, equivalentProbabilityContext;
    private PPMNode root;
    private ArithmeticEncoder encoder;

    public PPMTree(int maxContext, int[] alphabet) throws Exception {
        if (maxContext < 0 || maxContext > MAX_SIZE)
            throw new Exception("Valor do contexto inválido, este valor deve estar entre 0 e 10");

        this.alphabet = alphabet.clone();
        this.equivalentProbabilityContext = alphabet.clone();
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
    public void findByContext(int[] contextMessage, ArithmeticEncoder encoder) throws IOException {
        this.encoder = encoder;
        int msgSize = contextMessage.length; // O comprimento da mensagem.

        for(int i = 0; i < msgSize; i++){
            searchAndAdd(contextMessage);
            contextMessage = decrementMessage(contextMessage);
        }

    }

    /**
     * Realiza a busca do simbolo lido a partir da raiz da árvore e cria um novo nó,
     * caso o nó atual de busca não possua um nó filho com o símbolo lido, isto é,
     * o símbolo não está presente no contexto atual da busca. Este método também
     * incrementa a frequência do símbolo no contexto de atual, caso o símbolo exista
     * no contexto de busca corrente.
     *
     * @param symbols
     * @return
     */
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
                    calculateInterval(searchNode, symbols[searchLevel]);
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

    /**
     * Monta uma tabela de frequências de símbolos dentro do contexto do nó atual e a utiliza,
     * passando como argumento o codificador aritmético, para escrever o intervalo do símbolo
     * lido (passado como argumento) no arquivo de saída.
     *
     * @param currentNode
     * @param symbol
     * @return
     */
    private void calculateInterval(PPMNode currentNode, int symbol) throws IOException {
        int i = 0, index = 0;
        int numChildren = currentNode.getChildren().size();
        int [] frequencies = (numChildren == alphabet.length) ? new int[numChildren] : new int [numChildren+1];
        String tableDescription = "";

        System.out.println("Current context: " + currentNode.getSymbol());
        for (PPMNode child : currentNode.getChildren()){

            if(child.getSymbol() == symbol)
                index = i;

            tableDescription += i + ": " + child.getSymbol() + ", ";
            frequencies[i++] = child.getFrequency();
        }

        if(numChildren != alphabet.length) { // Indica que o nó atual não possui todos os símbolos possíveis dentro do seu contexto (há a existência do rô)
            tableDescription += i + ": " + "Ro";
            frequencies[i] = numChildren;
        }

        System.out.println(tableDescription);
        SimpleFrequencyTable frequencyTable = new SimpleFrequencyTable(frequencies);
        System.out.println(frequencyTable);
        encoder.write(frequencyTable, index); // Escreve o intervalo do codificador aritmético no arquivo

    }

    /**
     * Realiza a construção da tabela dos símbolos que não estão presentes em determinado
     * contexto (corresponde ao calculo do Rô (P) da tabela do contexto). Esse método só é chamado
     * quando o símbolo lido não está presente no contexto atual.
     * OBS: Não realiza o critério de exclusão para o contexto à um nível acima na hierarquia da árvore.
     *
     * @param currentNode
     * @return
     */
    private void remainingSymbols(PPMNode currentNode, int symbol) throws IOException {
        int i = 0, total = 0, numChildren = currentNode.getChildren().size();
        int frequencies[] = new int[numChildren+1]; // Inclui as frequências dos símbolos dos nós filhos e do rô (numChildren)
        String tableDescription = "";

        System.out.println("Current context: " + currentNode.getSymbol());

        if (numChildren == 0){ // Em caso do contexto atual não possuir símbolos, não computa o intervalo no codificador aritmético
            if(currentNode.equals(this.root))
                findEquiProbContext(symbol); // No caso do nó atual ser a raiz, faz-se a busca do símbolo no contexto de equiprobabilidade (K = -1)
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
        encoder.write(frequencyTable, i); // Escreve o intervalo do rô (P), por meio codificador aritmético, no arquivo

        if(currentNode.equals(this.root))
            findEquiProbContext(symbol); // No caso do nó atual ser a raiz, faz-se a busca do símbolo no contexto de equiprobabilidade (K = -1)


    }

    /**
     * Realiza uma busca dos símbolo no contexto onde os símbolos são equiprováveis (K = -1).
     * Após a busca do símbolo neste contexto, escreve o intervalo correspondente à probabilidade
     * deste símbolo (por meio do codificador aritmético) e, em seguida, é realizado decremento dos
     * símbolos neste contexto, eliminando este símbolo lido.
     * @param symbol
     * @throws IOException
     */
    private void findEquiProbContext(int symbol) throws IOException {
        int j = 0, index = 0, size = equivalentProbabilityContext.length;
        int [] frenquencies = new int[size], remainingSymbols = new int[size-1];
        String tableDescription = "";

        System.out.println("Equivalent probable context (K = -1)");
        for (int i  = 0; i < size; i++){
            frenquencies[i] = 1; // No contexto -1, todos os símbolos tem frequência 1
            tableDescription += i + ": " + equivalentProbabilityContext[i] + ", ";

            if (equivalentProbabilityContext[i] == symbol){ // Armazena o índice do símbolo que está sendo codificado
                index = i;
                continue;
            }

            remainingSymbols[j++] = equivalentProbabilityContext[i]; // Armazena os símbolos do contexto, removendo o símbolo que está sendo codificado

        }

        System.out.println(tableDescription);
        SimpleFrequencyTable frequencyTable = new SimpleFrequencyTable(frenquencies); // Tabela de frequência utilizada para escrita do codificador aritmético
        System.out.println(frequencyTable);
        encoder.write(frequencyTable, index); // Escreve o intervalo do codificador aritmético no arquivo

        this.equivalentProbabilityContext = remainingSymbols;

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

    public void printAlphabet(){
        int size = alphabet.length;
        String alphabetDesc = "Alphabet: ";
        for (int i = 0; i < size-1; i++){
            alphabetDesc += alphabet[i] + " ";
        }
        System.out.println(alphabetDesc);
    }

}
