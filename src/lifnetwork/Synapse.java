/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

/**
 *
 * @author Libra
 */
final public class Synapse {
    final private float weight;
    final private int pre;
    final private int post;

    public Synapse(float weight, int pre, int post) {
        this.weight = weight;
        this.pre = pre;
        this.post = post;
    }

    public int getPre() {
        return pre;
    }

    public int getPost() {
        return post;
    }
    
    


    
}
