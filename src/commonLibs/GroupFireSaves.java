/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonLibs;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Libra
 */
final public class GroupFireSaves {
    final private Float connProb;
    final private Float weightScale;
    final private String hashString;
    final private List<int[]> fireList;

    public GroupFireSaves(Float connProb, Float weightScale, String hashString, List<int[]> fireList) {
        this.connProb = connProb;
        this.weightScale = weightScale;
        this.hashString = hashString;
        this.fireList=new ArrayList<>();
        this.fireList.addAll(fireList);
    }
    
    
}
