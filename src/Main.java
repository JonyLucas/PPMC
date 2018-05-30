import ppm.decoder.PPMDecoder;
import ppm.encoder.PPMEncoder;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        int context;
        String inputFileEncoder = "src/examples/", outputFileEncoder = "src/resultsEncoder/", outputFileDecoder = "src/resultsDecoder/";
        Scanner scan = new Scanner(System.in);

        System.out.println("Digite o caminho do arquivo de entrada do codificador: ");
        inputFileEncoder += scan.nextLine();

        System.out.println("Digite o caminho do arquivo de saida do codificador: ");
        outputFileEncoder += scan.nextLine();

        System.out.println("Digite o caminho do arquivo de saida do decodificador: ");
        outputFileDecoder += scan.nextLine();

        System.out.println("Digite o tamanho do contexto do PPM: ");
        context = scan.nextInt();

        PPMEncoder encoder = new PPMEncoder(inputFileEncoder, outputFileEncoder, context);
        encoder.readAndCodify();

        PPMDecoder decoder = new PPMDecoder(outputFileEncoder, outputFileDecoder);
        decoder.readAndDecode();

        scan.close();

    }
}
