/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.util.*;

/**
 *
 * @author Kumar
 */
public class VsmEntity implements java.io.Serializable{
    private String category;
    private HashMap<Integer, String> docNameMap;
    private HashMap<String,ArrayList<Doc>> docLists;
    
    public VsmEntity(String category){
        this.category = category;
    }

   /**
     * @return the category
     */
    public String getCategory(){
        return category;
    }
        
    /**
     * @return the docNameMap
     */
    public HashMap<Integer, String> getDocNameMap() {
        return docNameMap;
    }

    /**
     * @param docNameMap the docNameMap to set
     */
    public void setDocNameMap(HashMap<Integer, String> docNameMap) {
        this.docNameMap = docNameMap;
    }

    /**
     * @return the docLists
     */
    public HashMap<String,ArrayList<Doc>> getDocLists() {
        return docLists;
    }
    
    /**
     * @param docLists
     */
    public void setDocLists(HashMap<String,ArrayList<Doc>> docLists)  {
        this.docLists = docLists;
    }
}




