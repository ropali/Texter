/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texter;

/**
 *
 * @author ropali
 */
public class OSValidator {
    
    public static String OS = System.getProperty("os.name").toLowerCase();
    
    public static boolean isWindows(){
        return (OS.indexOf("wind") >= 0);
    }
    
    public static boolean isLinux(){
        return (OS.indexOf("inx") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0);
    }
    
    public static boolean isMac(){
        return (OS.indexOf("mac") >= 0);
    }
    
    
}
