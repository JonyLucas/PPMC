package huffmanCoding.encoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TreeBuilder {

    static public HuffmanTree buildTree(HashMap<Integer, Integer> symbolFrequency){

        ArrayList<HuffmanNode> sheets = createSheets(symbolFrequency);
        HuffmanNode root = createParents(sheets);
        return new HuffmanTree(root);

    }

    static private ArrayList<HuffmanNode> createSheets(HashMap<Integer, Integer> symbolFrequency){
        ArrayList<HuffmanNode> sheets = new ArrayList<HuffmanNode>();

        for(Integer symbol : symbolFrequency.keySet()){
            int frequency = symbolFrequency.get(symbol);
            sheets.add(new HuffmanNode(symbol, frequency));
        }

        Collections.sort(sheets, Collections.reverseOrder());


//        for(HuffmanNode hn : sheets){
//            System.out.println("Symbol: " + hn.getSymbols() + " - Frequency: " + hn.getFrequency());
//        }
//        System.out.println();


        return sheets;
    }

    static private HuffmanNode createParents(ArrayList<HuffmanNode> huffmanNodes){
        int size = huffmanNodes.size();
        HuffmanNode parentNode = huffmanNodes.get(0);

        for(int i = size-1; i > 0; i--)
        {
            HuffmanNode leftSon = huffmanNodes.remove(i);
            HuffmanNode rightSon = huffmanNodes.remove(i-1);

            parentNode = new HuffmanNode(rightSon, leftSon);
            huffmanNodes.add(parentNode);
            Collections.sort(huffmanNodes, Collections.reverseOrder());


//            for(HuffmanNode hn : huffmanNodes){
//                System.out.println(i + "- Symbols: " + hn.getSymbols() + " - Frequencies: " + hn.getFrequency());
//            }
//            System.out.println();


        }

        return parentNode;
    }

}
