/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import static texter.Texter.TITLE;
import static texter.Texter.textArea;

/**
 *
 * @author ropali
 */
public class AutoSave extends Thread{
    File file;
    JTextArea text;
    JLabel label;
    FileWriter fw;
 
    public AutoSave(File file, JTextArea text, JLabel label){
        this.file = file;
        this.text = text;
        this.label = label;
    }

    @Override
    public void run() {
        while(file != null && !text.getText().equals("")){
            
            try {
                
                fw = new FileWriter(file);
                
                fw.write(textArea.getText());
                fw.flush();
                label.setText("File Automatically Saved");
                sleep(10000);
                

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException ex) {
                label.setText("Auto Save Disabled");
                ex.printStackTrace();
            }
        }
        
        try {
            if(fw != null){
                fw.close();
            }
            
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }
    
    
    
}
