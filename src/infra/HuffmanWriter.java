package infra;

import java.io.*;
import java.util.HashMap;

public class HuffmanWriter {

    private DataOutputStream writer;

    public void saveFile(DataOutputStream out, String finalCode){

        try {
            this.writer = out;
            writeCode(around(finalCode));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCode(String finalCode) throws IOException
    {
        int aux = 0, k = 7;

        for(int i = 0; i < finalCode.length(); i++)
        {
            aux += Character.getNumericValue(finalCode.charAt(i))*Math.pow(2, k);
            k--;
            if(k < 0)
            {
                k = 7;
                writer.writeByte(aux);
                aux = 0;
            }
        }
    }

    private String around(String finalCode)
    {
        while(finalCode.length()%8 != 0) {
            finalCode += '0';
        }
        return finalCode;
    }

}
