/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import cowlite.io.common.FileDataReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.PDFCoordinate;

/**
 *
 * @author Wessel
 */
public class FillConfiguration {
    private float offsetX, offsetY;
    
    private List<List<ConfigEntry>> configuration;
    
    private static String SPLIT = "#VALUE#";
    
    private static String SPLIT_REGEX = "(.*)" + SPLIT + "(.*)";
    
    public FillConfiguration() {
        this.offsetX = 0;
        this.offsetY = 0;
        this.configuration = new ArrayList<>();
        this.configuration.add(new ArrayList<>());
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public List<ConfigEntry> getConfguration(int page) {
        return configuration.get(page);
    }

    public void setConfigurationForPage(int page, List<ConfigEntry> configuration) {
        if(page < this.configuration.size()) {
            this.configuration.remove(page);
        }
        
        if(page >= this.configuration.size()) {
            for(int i = this.configuration.size(); i < page - 1; i++) {
                this.configuration.add(new ArrayList<>());
            }
            this.configuration.add(configuration);
        } else {
            this.configuration.add(page, configuration);
        }
    }
    
    public void setConfiguration(List<List<ConfigEntry>> configuration) {
        this.configuration = configuration;
    }
    
    public void addConfigurationOnPage(int page, PDFCoordinate loc, Object var) {
        System.out.println(this.configuration.size());
        System.out.println(page);
        for(int i = this.configuration.size(); i <= page; i++) {
            this.configuration.add(new ArrayList<>());
        }
        this.configuration.get(page).add(new ConfigEntry(var, loc));
    }
    
    public int getPages() {
        return this.configuration.size();
    }
    
    public void saveConfiguration(File f) throws FileNotFoundException, IOException {
        f.createNewFile();
        PrintWriter out = new PrintWriter(f);
        out.println("offsetX:" + offsetX);
        out.println("offsetY:" + offsetY);
        for(List<ConfigEntry> config : this.configuration) {
            out.println("#");
            for(ConfigEntry entry : config) {
                out.println(entry.getValue()+ SPLIT + entry.getLocation());
            }
        }
        out.close();
    }
    
    public static FillConfiguration loadConfiguration(File f) throws IOException {
        FileDataReader reader = new FileDataReader();
        reader.setPath(f);
        List<String> lines = reader.getDataStringLines();
        FillConfiguration configuration = new FillConfiguration();
        configuration.setOffsetX(Float.parseFloat(lines.get(0)));
        configuration.setOffsetY(Float.parseFloat(lines.get(1)));
        lines.remove(0);
        lines.remove(0);
        
        List<List<ConfigEntry>> config = new ArrayList<>();
        List<ConfigEntry> pageConfig = null;
        
        Pattern pattern = Pattern.compile(SPLIT_REGEX);
        for(String line : lines) {
            if(line.equals("#")) {
                pageConfig = new ArrayList<>();
                config.add(pageConfig);
            } else {
                Matcher m = pattern.matcher(line);
                if(m.find()) {
                    String[] location = m.group(1).split(",");
                    PDFCoordinate loc = new PDFCoordinate(Float.parseFloat(location[0]), Float.parseFloat(location[1]));
                    if(pageConfig != null) {
                        pageConfig.add(new ConfigEntry(m.group(2), loc));
                    }
                }
            }
        }
        
        configuration.setConfiguration(config);
        
        return configuration;
    }
    
    public static class ConfigEntry {
        private PDFCoordinate location;
        private Object value;
        
        public ConfigEntry(Object value, PDFCoordinate location) {
            this.location = location;
            this.value = value;
        }

        public PDFCoordinate getLocation() {
            return location;
        }

        public void setLocation(PDFCoordinate location) {
            this.location = location;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}