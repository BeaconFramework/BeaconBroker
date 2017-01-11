/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Giuseppe Tricomi
 */
public class FileFunction {

    public FileFunction() {
    }
    
    
     /**
     * It returns a String that contains the whole manifest.
     * @param fileName
     * @return 
     */
    public String readFromFile(String fileName){
        Path p=Paths.get(fileName);
        String manifest="";
        try (InputStream in = Files.newInputStream(p);
            BufferedReader reader =
              new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                manifest=manifest+"\n"+line;
            }
        } catch (IOException x) {
            System.err.println(x.getMessage());
            System.err.println(x);
        }
        return manifest;
    }
}
