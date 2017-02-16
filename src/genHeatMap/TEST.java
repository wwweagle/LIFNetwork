/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genHeatMap;

/**
 *
 * @author Libra
 */
public class TEST {

    public void test() {
        for (int i=0;i<get6();i++) {
            System.out.println(i);
        }
    }
    Integer[] testArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

    public Integer[] func() {
        System.out.println("called again");
        return testArray;
    }
    
    public int get6(){
        System.out.println("getting");
        return 6;
    }
}
