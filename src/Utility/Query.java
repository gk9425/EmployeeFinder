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
public class Query {
    public String qterm;    
    public double qw;     
    public Query(String qterm, double qw){
        this.qterm = qterm;        
        this.qw = qw;
    }
}
