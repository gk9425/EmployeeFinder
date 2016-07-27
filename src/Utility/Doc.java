/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

/**
 *
 * @author Kumar
 */
public class Doc implements java.io.Serializable {
    
    public int docId;    
    public double tw;     
    public Doc(int did, double wt){
        docId = did;        
        tw = wt;
    }
}
