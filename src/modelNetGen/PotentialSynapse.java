/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.sql.Date;

/**
 *
 * @author Libra
 */
public class PotentialSynapse {

    private Date date;
    private int group;
    private int div;
    private int id1;
    private int id2;
    private boolean fwdGlu;
    private boolean revGlu;
    private int dist;

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getGroup() {
        return group;
    }

    public void setDiv(int div) {
        this.div = div;
    }

    public void setFwdGlu(boolean fwdGlu) {
        this.fwdGlu = fwdGlu;
    }

    public void setRevGlu(boolean revGlu) {
        this.revGlu = revGlu;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public void setId1(int id1) {
        this.id1 = id1;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }
    
    

    public boolean getFwdGlu(){
        return fwdGlu;
    }
    
    public boolean getRevGlu(){
        return revGlu;
    }

    public int getDiv() {
        return div;
    }

    public int getDist() {
        return dist;
    }

    public int getId1() {
        return id1;
    }

    public int getId2() {
        return id2;
    }
    
       
}
