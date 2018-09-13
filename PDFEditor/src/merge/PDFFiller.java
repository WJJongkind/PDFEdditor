/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package merge;

import config.FillConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import util.PDFCoordinate;
import util.PDFMerger;
import util.PDFPageSplitter;

/**
 *
 * @author Wessel
 */
public class PDFFiller {
    public static File fillPDF(String pdfPath, String targetPath, FillConfiguration configuration) throws Exception {
        List<File> separated = PDFPageSplitter.splitPDF(new File(pdfPath), true);
        
        List<File> filledPages = doFill(separated, configuration);
        
        PDDocument merged = PDFMerger.mergePDF(filledPages);
        merged.save(new File(targetPath));
        return null;
    }
    
    private static List<File> doFill(List<File> pages, FillConfiguration configuration) throws Exception {
        List<File> files = new ArrayList<>();
        for(int i = 0; i < pages.size() && i < configuration.getPages(); i++) {
            PDDocument target = PDDocument.load(pages.get(i));
            PDPage page = target.getPage(0);
            PDDocument overlay = drawOverlay(
                                                configuration.getConfguration(i), 
                                                configuration.getOffsetX(), 
                                                configuration.getOffsetY()
                                            );
            
            Overlay overlayObj = new Overlay();
            overlayObj.setOverlayPosition(Overlay.Position.FOREGROUND);
            overlayObj.setInputPDF(target);
            overlayObj.setAllPagesOverlayPDF(overlay);
            
            Map<Integer, String> ovmap = new HashMap<>();
            PDDocument finished = overlayObj.overlay(ovmap);
            
            files.add(new File(pages.get(i).getAbsolutePath() + "f"));
            finished.save(files.get(i));
        }
        
        return files;
    }
    
    private static PDDocument drawOverlay(List<FillConfiguration.ConfigEntry> drawables, float offsetX, float offsetY) throws Exception {
        PDDocument overlay = new PDDocument();
        PDPage page = new PDPage();
        overlay.addPage(page);
        PDFont font = PDType1Font.HELVETICA;

        PDPageContentStream contentStream = new PDPageContentStream(overlay, page);

        contentStream.setFont(font, 10 );
        contentStream.beginText();
        for(FillConfiguration.ConfigEntry entry : drawables) {
            contentStream.newLineAtOffset(entry.getLocation().getX() + offsetX, entry.getLocation().getY() + offsetY);
            contentStream.showText(entry.getValue().toString());
        }
        contentStream.endText();
        
        contentStream.close();
        return overlay;
    }
}
