/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 *
 * @author Wessel
 */
public class PDFMerger {
    public static void mergePDF(List<File> sources, File target) throws IOException {
        PDDocument doc = new PDDocument();
        List<PDDocument> opened = new ArrayList<>();
        for(File file : sources) {
            PDDocument src = PDDocument.load(file);
            Iterator<PDPage> it = src.getPages().iterator();
            while(it.hasNext()) {
                doc.addPage(it.next());
            } 
            opened.add(src);
        }
        
        doc.save(target);
        
        for(PDDocument src : opened) {
            src.close();
        }
    }
}
