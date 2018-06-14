package huffmanCoding.encoder;

public class HuffmanTree {

    private HuffmanNode root;
    private HuffmanNode searchNode;

    public HuffmanTree(HuffmanNode node){
        this.root = node;
        searchNode = root;
    }

    public String codifySymbol(int symbol, String code){
        if(searchNode.hasSymbol(symbol)){

            HuffmanNode leftSon = searchNode.getLeftSon();
            HuffmanNode rightSon = searchNode.getRightSon();

            if(leftSon != null && leftSon.hasSymbol(symbol)){
                searchNode = leftSon;
                return codifySymbol(symbol, (code+"1"));
            }else if(rightSon != null && rightSon.hasSymbol(symbol)){
                searchNode = rightSon;
                return codifySymbol(symbol, (code+"0"));
            }else {
                return code;
            }
        }else{
            return null;
        }
    }

}
