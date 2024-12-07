import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import java.io.File;

public class mainClass {
    public static void main(String[] args) {

        File img = new File("/src/files/text.jpg");
        ITesseract instance = new Tesseract();

        try {
            String text = instance.doOCR(img);
            System.out.println(text);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("2ects");
    }
}
