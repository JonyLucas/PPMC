package ppm.decoder.model;

import arithmeticCoding.ArithmeticDecoder;
import arithmeticCoding.tables.SimpleFrequencyTable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PPMTree {

    private int MAX_SIZE = 10, maxContext;
    private int[] equivalentProbabilitySymbols, subString, lowerMessage;

    private DataOutputStream out;

    private PPMNode root, currentNode;
    private ArithmeticDecoder decoder;

    /**
     * Construtor da árvore de decodificação do PPMC
     *
     * @param decoder
     * @param maxContext
     * @throws Exception
     */
    public PPMTree(ArithmeticDecoder decoder, DataOutputStream out, int maxContext) throws Exception {
        if (maxContext < 0 || maxContext > MAX_SIZE)
            throw new Exception("Valor do contexto inválido, este valor deve estar entre 0 e 10");

        this.maxContext = maxContext;
        this.root = new PPMNode();
        this.decoder = decoder;
        this.out = out;

        currentNode = root;

        // Substring que contêm os últimos K símbolos decodificados
        subString = new int[maxContext];
        Arrays.fill(subString, -1);
        lowerMessage = subString.clone();

        equivalentProbabilitySymbols = new int[257];
        for (int i = 0; i < 257; i++)
            equivalentProbabilitySymbols[i] = i;

    }

    /**
     * Realiza a busca e decodificação a partir do nó corrente (currentNode), verificando se
     * o currentNode possui filhos, isto é, se o contexto do nó atual existe. Caso o nó corrente
     * não possua filhos e seja a raiz da árvore, realiza a decodificação do intervalo no contexto
     * da equiprobabilidade(k = -1). Caso o nó atual não possua filhos (o seu contexto ainda não
     * existe), é realizado a busca deste nó em um nível mais árvore da árvore (k - 1) e realiza-se
     * novamente a busca a partir deste nó. Caso o contexto deste nó exista, realiza-se a decodificação
     * do intervalo a partir do contexto do nó corrente, de acordo com a frequência dos seus nó filhos.
     *
     * @throws IOException
     */
    public void searchAndDecode() throws IOException {
        int numChildren = currentNode.getChildren().size();
        if (numChildren == 0 && currentNode.equals(root)) { // verifica se é o início da decodificação (Nó atual é a raiz, que ainda não possui filhos)
            findEquiProbContext();
        } else if (numChildren == 0) {
            getInLowerContext();
            searchAndDecode();
            return;
        } else {
            decode();
        }

        constructAndAdd();

    }

    /**
     * Realiza-se a busca do nó corrente em um nível mais alto (k - 1), decrementando a substring de busca
     * (de tamanho kMáx) e percorrendo a árvore, a partir da raiz, para alcançar o nó em um contexto menor.
     * Ao encontrar o nó correspondente em um nível mais alto, este passa a ser o nó corrente.
     *
     * @throws IOException
     */
    public void getInLowerContext() throws IOException {
        lowerMessage = removeEmptySpaces(lowerMessage);
        lowerMessage = decrementMessage(lowerMessage);
        this.currentNode = searchNode(lowerMessage);
    }

    /**
     * Busca o nó a partir da raiz utilizando a substring de busca (symbols).
     *
     * @param symbols
     * @return
     * @throws IOException
     */
    private PPMNode searchNode(int[] symbols) throws IOException {
        int searchLevel = 0; // Indica o nível de busca (contexto) na árvore.
        int maxLevel = symbols.length; // Corresponde ao contexto máximo (O tamanho da substring)
        PPMNode searchNode = this.root; // Inicia a busca pela raiz.
        PPMNode auxNode; // Nó auxiliar para realizar a verificação se o nó atual possui um nó filho com determinado símbolo

        while (searchLevel < maxLevel) {
            if (symbols[searchLevel] == -1)
                break;

            searchNode = searchNode.findChild(symbols[searchLevel]);
            searchLevel++;
        }

        return searchNode;

    }

    /**
     * Realiza-se a decodificação a partir dos intervalos do contexto (nós filhos) do nó corrente, realizando a
     * contagem das frequência e passando (no formato de tabela de frequências) para o decodificador aritmético.
     * Em seguida o índice retornado pelo decodificador aritmético é utilizado para extrair o símbolo lido e, em
     * seguida, à substring para posteriores adições/buscas.
     *
     * @throws IOException
     */
    private void decode() throws IOException {
        int i = 0, index, numChildren = currentNode.getChildren().size();
        int[] symbols = new int[numChildren]; // Array com os símbolos do contexto do nó corrente
        int[] frequencies = (numChildren == 256) ? new int[numChildren] : new int[numChildren + 1]; // Caso o nó corrente possua todos os símbolos do alfabeto, descosidera-se o rô
        String tableDescription = "";

        System.out.println("Current context: " + currentNode.getSymbol());
        for (PPMNode child : currentNode.getChildren()) {
            tableDescription += i + ": " + child.getSymbol() + ", ";
            symbols[i] = child.getSymbol();
            frequencies[i] = child.getFrequency();
            i++;
        }

        // Indica que o nó atual não possui todos os símbolos possíveis dentro do seu contexto (há a existência do rô)
        if (numChildren != 256) {
            tableDescription += i + ": " + "Ro";
            frequencies[i] = numChildren;
        }

        System.out.println(tableDescription);
        SimpleFrequencyTable frequencyTable = new SimpleFrequencyTable(frequencies);
        System.out.println(frequencyTable);
        index = decoder.read(frequencyTable);

        /**
         * Caso o índice retornado pelo decodificador for o rô, verifica se o nó atual é a raiz da árvore,
         * para fazer a busca no contexto k -1. Caso o nó atual não seja a raiz, resgata o nó correspondente
         * em um contexto menor e realiza-se a decodificação a partir do contexto deste novo nó corrente.
         */
        if (index == numChildren) {
            if (currentNode.equals(this.root)) {
                findEquiProbContext();
            } else {
                getInLowerContext();
                decode();
            }
        } else {
            addShiftAndWrite(symbols[index]);
        }

    }

    /**
     * Realiza a construção e adição de nós através da substring atual da árvore.
     *
     * @throws IOException
     */
    private void constructAndAdd() throws IOException {
        int[] contextMessage = subString.clone();
        int msgSize = contextMessage.length;
        PPMNode aux;

        for (int i = 0; i < msgSize; i++) {
            aux = searchAndAdd(contextMessage);
            contextMessage = decrementMessage(contextMessage);
            this.currentNode = (currentNode.getContextLevel() < aux.getContextLevel()) ? aux : currentNode;
        }
    }

    private PPMNode searchAndAdd(int[] symbols) throws IOException {
        int searchLevel = 0; // Indica o nível de busca (contexto) na árvore.
        int maxLevel = symbols.length; // Corresponde ao contexto máximo (O tamanho da substring)
        PPMNode searchNode = this.root; // Inicia a busca pela raiz.
        PPMNode auxNode; // Nó auxiliar para realizar a verificação se o nó atual possui um nó filho com determinado símbolo

        while (searchLevel < maxLevel) {
            if (symbols[searchLevel] == -1)
                break;

            auxNode = searchNode.findChild(symbols[searchLevel]);
            if (auxNode != null) {
                /**
                 * Caso o símbolo encontrado esteja no final da string e foi encontrado na árvore
                 * (o que indica que ele já foi encontrado antes dentro deste contexto),
                 * então incrementa a sua frequência.
                 **/
                if (searchLevel == maxLevel - 1) {
                    System.out.println("Increment Symbol: " + symbols[searchLevel] + " in context: " + searchNode.getSymbol());
                    searchNode = auxNode;
                    searchNode.incrementFrequency();
                } else {
                    searchNode = auxNode;
                }
            } else {
                System.out.println("Add Symbol: " + symbols[searchLevel] + " in context: " + searchNode.getSymbol() + "\n");
                searchNode.addChild(symbols[searchLevel]);
                searchNode = searchNode.findChild(symbols[searchLevel]);
            }

            searchLevel++;

        }

        return searchNode;

    }

    /**
     * Realiza a decodificação a partir do contexto da equiprobabilidade (k -1).
     *
     * @throws IOException
     */
    private void findEquiProbContext() throws IOException {
        int j = 0, symbol = 0, index = 0, size = equivalentProbabilitySymbols.length;
        int[] frequencies = new int[size], remainingSymbols = new int[size - 1];

        System.out.println("Equivalent probable context (K = -1)");
        Arrays.fill(frequencies, 1);

        index = decoder.read(new SimpleFrequencyTable(frequencies));
        symbol = equivalentProbabilitySymbols[index];

        for (int i = 0; i < size; i++) {
            if (equivalentProbabilitySymbols[i] != symbol)
                remainingSymbols[j++] = equivalentProbabilitySymbols[i];
        }

        System.out.println("Simbolo removido: " + symbol);

        addShiftAndWrite(symbol); // Adiciona-se o símbolo na substring da árvore, para futuras operações de adição/busca

        this.equivalentProbabilitySymbols = remainingSymbols;

    }

    /**
     * Realiza o deslocamento dos símbolos para à esquerda (eliminando o primeiro elemento do array)
     * e adiciona o símbolo passado como argumento na ultima posição. Após esta adição, é realizado
     * a escrita no arquivo de saída.
     *
     * @param symbol
     * @return
     * @throws IOException
     */
    private void addShiftAndWrite(int symbol) throws IOException {
        int i, size = subString.length;

        // Realiza o deslocamento para à esquerda das posições e insere o símbolo em questão no final.
        for (i = 0; i < size - 1; i++) {
            subString[i] = subString[i + 1];
        }
        subString[i] = symbol;
        lowerMessage = subString.clone();

        out.write(symbol);

    }

    /**
     * Cria uma sub-mensagem da mensagem passada como argumento, com comprimento reduzido em um elemento:
     * Comprimento(novaMsg) = Comprimento(msgOriginal) - 1.
     * E eliminando o primeiro caractere da mensagem original.
     *
     * @param message
     * @return
     */
    private int[] decrementMessage(int[] message) {

        int newSize = message.length - 1;
        int[] newMessage = new int[newSize]; //Cria um array com o comprimento (n) da mensagem inicial - 1

        for (int i = 0; i < newSize; i++) {
            newMessage[i] = message[i + 1];
        }

        return newMessage;
    }

    /**
     * Método que retorna um array sem os espaços em brancos (-1) do array passado como argumento;
     * @param message
     * @return
     */
    private int[] removeEmptySpaces(int[] message) {

        return (message[0] == -1) ? removeEmptySpaces(decrementMessage(message)) : message;
    }

    public void showTree() {
        System.out.println(root.printNode(-1));
    }


}
