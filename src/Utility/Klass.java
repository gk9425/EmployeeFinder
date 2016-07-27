/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.util.*;

/**The class describes attributes of the Labe used for classification 
 *
 * @author Kumar
 */
public class Klass implements java.io.Serializable{    
   /* Attributes*/ 
   private int id;
   private String className;
   private int docCount4Class;
   private int tokenCount4Class;        
   private HashMap <String, Double> termCondProb; 
   
    
    public Klass(int id, int count, String name){
        this.id = id;
        this.docCount4Class = count;
        this.className = name;
    }

    /**
     * @return the id
     */
    public int getClassId() {
        return id;
    }
    
    /**
     * @return the nC
     */
    public int getClassDocCount() {
        return docCount4Class;
    }
    
    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the tokenCount
     */
    public int getClassTokenCount() {
        return tokenCount4Class;
    }

    /**
     * @param tokenCount the tokenCount to set
     */
    public void setClassTokenCount(int tokenCount) {
        this.tokenCount4Class = tokenCount;
    }
    

    /**
     * @return the termCondProb
     */
    public HashMap <String, Double> getClassTermCondProb() {
        return termCondProb;
    }

    /**
     * @param probability the condProb to set
     */
    public void setClassTermCondProb(HashMap <String, Double> probability) {
        this.termCondProb = probability;
    }
    
}
