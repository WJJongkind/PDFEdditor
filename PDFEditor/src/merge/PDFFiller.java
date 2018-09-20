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
    
    public static void main(String[] args) throws Exception {
        FillConfiguration configuration = new FillConfiguration();
        configuration.setOffsetX(10f);
        configuration.setOffsetY(10f);
        configuration.addConfigurationOnPage(0, new PDFCoordinate(100f, 100f), "Hello wooooooooooorld");
        configuration.addConfigurationOnPage(1, new PDFCoordinate(200f, 200f), "This be page 2 my man");
        configuration.saveConfiguration(new File("C:\\Users\\Wessel\\Documents\\Formulier telefoon.cfg"));
        fillPDF("C:\\Users\\Wessel\\Documents\\Formulier telefoon.pdf", "C:\\Users\\Wessel\\Documents\\Formulier telefoon MERGED.pdf", configuration);
    }
    
    public static File fillPDF(String pdfPath, String targetPath, FillConfiguration configuration) throws Exception {
        List<File> separated = PDFPageSplitter.splitPDF(new File(pdfPath), true);
        
        List<File> filledPages = doFill(separated, configuration);
        
        PDFMerger.mergePDF(filledPages, new File(targetPath));
        
        for(File sep : separated) {
            System.out.println("Cleaning file : " + sep.delete() + "    " + sep);
        }
        for(File fil : filledPages) {
            System.out.println("Cleaning file : " + fil.delete() + "    " + fil);
            System.out.println("Cleaning folder of file: " + fil.getParentFile().delete());
        }
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
            finished.close();
            overlay.close();
            target.close();
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
        for(FillConfiguration.ConfigEntry entry : drawables) {
        contentStream.beginText();
            System.out.println("Showing text at: " + (entry.getLocation().getX() + offsetX) + "    " + (841.68 - entry.getLocation().getY() + offsetY));
            System.out.println("Text is: " + entry.getValue());
            contentStream.newLineAtOffset(entry.getLocation().getX() + offsetX, (int)(841.68 - entry.getLocation().getY() + offsetY));
            contentStream.showText(entry.getValue().toString());
            contentStream.endText();
        }
        
        contentStream.close();
        return overlay;
    }
}
