/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 *
 * @author Wessel
 */
public class PDFMerger {
    public static PDDocument mergePDF(List<File> sources) throws IOException {
        PDDocument doc = new PDDocument();
        for(File file : sources) {
            PDDocument src = PDDocument.load(file);
            Iterator<PDPage> it = src.getPages().iterator();
            while(it.hasNext()) {
                doc.addPage(it.next());
            } 
        }
        
        return doc;
    }
}
