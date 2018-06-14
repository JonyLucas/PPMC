package huffmanCoding.decoder;

import huffmanCoding.decoder.HuffmanNode;

public class HuffmanTree {

    private HuffmanNode root;

    public HuffmanTree( HuffmanNode node)
    {
        root = node;
    }

    public HuffmanNode getLeaf(String message)
    {
        int i = 0;
        HuffmanNode node = root;

        for(; i < message.length(); i++)
        {
            if(message.charAt(i) == '0') node = node.getRightSon();
            else node = node.getLeftSon();
        }

        if(node.getSymbols().size() != 1) return null;

        return node;
    }


}
