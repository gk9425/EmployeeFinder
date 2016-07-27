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
public class RankedResult {
    
    public int Rank;
    public String DocName;
    public Double Score;
    public ArrayList<String> MatchedTerms;
    
    public String GetMatchedTerms(){
       StringBuilder sb = new StringBuilder();
        for(String s : MatchedTerms){
          sb =  sb.append(s).append(" , ");          
        }
        return sb.toString();
    }
    
}
