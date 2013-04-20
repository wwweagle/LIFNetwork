/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

/**
 *
 * @author Libra
 */
public  class TEMP {
            
        /*
         * TEMP deserialize test
         */
        public static void deserial(){
        Set<Integer> set;
        try{
            FileInputStream f=new FileInputStream("set.ser");
            ObjectInputStream in=new ObjectInputStream(f);
            set=(Set<Integer>) in.readObject();
            in.close();
            f.close();
            System.out.println("deserialize succeed");
            System.out.println(set.size());
            
        }catch (IOException  | ClassNotFoundException e){
            System.out.println("deserialize failed");
        }
    } 
    
}
