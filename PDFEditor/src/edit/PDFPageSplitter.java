/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author Wessel
 */
public class PDFPageSplitter {
    public static List<File> splitPDF(File pdf, boolean randomDir) throws IOException {
        PDDocument document = PDDocument.load(pdf);
        
        String dest;
        if(randomDir) {
            dest = createRandomDir().getAbsolutePath() + "\\" + pdf.getName();
        } else {
            dest = pdf.getName();
        }
        
        ArrayList<File> files = new ArrayList<>();
        for(int i = 0; i < document.getNumberOfPages(); i++) {
            PDDocument doc = new PDDocument();
            doc.addPage(document.getPage(i));
            doc.save(new File(dest + i));
            doc.close();
            files.add(new File(dest + i));
        }
        
        document.close();
        
        return files;
    }
    
    private static File createRandomDir() {
        Random random = new Random();
        File randomFolder = null;
        
        do {
            randomFolder = new File("" + random.nextInt());
        }while(randomFolder.exists());
        
        randomFolder.mkdir();
        
        return randomFolder;
    }
}
