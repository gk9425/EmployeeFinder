/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import Utility.*;
import java.util.*;
import java.util.Map.*;

/**
 *
 * @author Kumar
 */
public class Tfidf {
    public ArrayList<VsmEntity> categoryList;
    private ArrayList<String> stopWords;
  
    public Tfidf(String path){
        //populate stopwords
        stopWords = Helper.GetStopWords(); 
        String resourcePath = path+"\\Resume\\Resources\\Search";
        String[] resources = Helper.GetFiles(resourcePath);
        int resLen = resources.length;
        if( resLen > 0 ){
            categoryList = Helper.DeSerializeCategoryList(resourcePath);      
        }    
        else{  
            path += "\\Resume\\Train";
            categoryList = new ArrayList<>(); 
          
            ArrayList<String> termList;
            HashMap<Integer, String> docNameMap;
            HashMap<String,ArrayList<Doc>> docLists;        
                    
            //get all categories
            String[] categories = Helper.GetFolders(path);
            int l1Count   = categories.length;

            //iterate categories and create inverted index       
            for(int i = 0; i < l1Count; i++){
                //initialize category variables

                termList    = new ArrayList<>();
                docNameMap  = new HashMap<>();
                docLists    = new HashMap<>();
                double[] docLength;
                ArrayList<Doc> docList;


                String categoryName = categories[i];

                // Get and iterate files in a category
                String[] files = Helper.GetFiles(path+"\\"+categoryName);
                int l2Count =  files.length;

                for(int j = 0; j < l2Count; j++){

                    ArrayList<String> fC;
                    ArrayList<String> fC1;
                    // read file content
                    String filename = files[j];
                    fC = Helper.GetFileContentAsList(path +"\\"+ categoryName +"\\"+ filename);

                    //remove stop words
                    fC.removeAll(stopWords);

                    //apply stemmer
                    fC1 = Helper.GetStemmedList(new ArrayList<String>(fC));                
                    int l3Count = fC1.size();

                    for(int k = 0; k < l3Count; k++){
                         String token = fC1.get(k);
                         //optimization : leaving out one character tokens
                         if(token.length()>1){
                         if(! termList.contains(token)){                 
                            termList.add(token);
                            docList = new ArrayList<>();
                            Doc objDoc = new Doc(j, 1);
                            docList.add(objDoc);
                            docLists.put(token, docList);
                        }
                        else{                 
                            docList = docLists.get(token);
                            boolean match = false;
                            for(Doc docItem : docList){
                                if(docItem.docId == j){
                                    docItem.tw++;  
                                    match= true;
                                    break;
                                }                    
                            }

                            if(! match){
                                Doc objDoc = new Doc(j, 1); //initial frequency
                                docList.add(objDoc);                     
                             }                
                        }
                         
                     }    

                    }

                    // map document name with id
                    docNameMap.put(j, filename);                                                
                }

                 /*Calculate document term weights*/
                docLength = new double[l2Count];
                int termCount = termList.size();
                for(int j=0; j < termCount; j++){
                    docList = docLists.get(termList.get(j));
                    int docfreq = docList.size();
                    Doc objDoc;
                    int docCount = docList.size();
                    for(int k=0;k<docCount;k++){
                        objDoc = docList.get(k);
                        double tfidf =(1+Math.log10(objDoc.tw))*Math.log10(l2Count/(docfreq*1.0));
                        docLength[objDoc.docId] += Math.pow(tfidf, 2);
                        objDoc.tw = tfidf;
                        docList.set(k, objDoc);
                    }        
                }


                /* Calculation for document Length Normalization */   
                for(int j=0;j<l2Count;j++){
                    docLength[j] = Math.sqrt(docLength[j]);
                }

                /* Normalize document vector */      
                for(int j = 0; j < termCount; j++){
                    docList = docLists.get(termList.get(j));
                    int docCount4Term = docList.size();
                    Doc doc;
                    for(int k = 0; k < docCount4Term; k++){
                        doc = docList.get(k);
                        doc.tw = doc.tw/docLength[doc.docId]; 
                        docList.set(k, doc);
                    }
                }


                VsmEntity objVsmEntiy = new VsmEntity(categoryName);
                objVsmEntiy.setDocLists(docLists);
                objVsmEntiy.setDocNameMap(docNameMap);

                categoryList.add(objVsmEntiy);                                  

            }
            
            Helper.SerializeCategoryList(categoryList, resourcePath);
        }    
    }
    
    
    public ArrayList<RankedResult> rankedResult(ArrayList<String> qc, String label, int recordCount){
        int numOfRecords = (recordCount > 5)?recordCount:5;
        String[] resultRet = new String[numOfRecords];
        VsmEntity categoryVsm = getVsmEntity(label);
        HashMap<String,ArrayList<Doc>> docLists = categoryVsm.getDocLists();
        HashMap<Integer, String> docs = categoryVsm.getDocNameMap();
                          
        /*Variable declearion*/ 
        ArrayList<Doc> docList;      
        HashMap<String,Query> queryTerms = new  HashMap<>();
        HashMap<Integer, Double> result = new HashMap<>();
   
        ArrayList<String> qc1;

        //apply stemmer
        qc1 = Helper.GetStemmedList(new ArrayList<String>(qc));
    
        int qTrCount  = qc1.size();
    
        /* Calculate query term frequency  */        
        for(int i =0; i<qTrCount;i++){
            String term = qc1.get((i));
                if(! queryTerms.containsKey(term)){
                    Query qterm = new Query(term, 1);
                    queryTerms.put(term, qterm);
                }
                else{
                    queryTerms.get(term).qw++;          
                }
        }
            
    /* Calculate query term weight
       weighting scheme:(1 + log f(t,q))* log N/n(t)
    */  
    Query qt = null;
    double qlength = 0.0;   
    
    for(Map.Entry item : queryTerms.entrySet() ){
        qt = (Query)item.getValue();
        if((qt != null ) && (docLists.containsKey(qt.qterm))){          
           int df =  (docLists.get(qt.qterm)).size();
           double tfidf  = (1+Math.log10(qt.qw))*Math.log10(docs.size()/(df*1.0));
           qt.qw = tfidf;
           qlength += Math.pow(tfidf, 2); 
        }
    }
    
    /* Calculation for Query Length Normalization */    
    qlength = Math.sqrt(qlength);
    
    /* Normalizing query vector */
    for(Map.Entry item : queryTerms.entrySet() ){
        qt = (Query)item.getValue();
        if((qt!= null) && (docLists.containsKey(qt.qterm)))         
           qt.qw = (qt.qw) /(qlength);
    }
        
    /* Calculate Cosine Similarity(Query, Document) */
   
    
    for(Map.Entry item : queryTerms.entrySet() ){  
        String term = (String)item.getKey();
        Query qItem = (Query)item.getValue();
        
        if(docLists.containsKey(term)){ //termList.contains(term)
            docList = docLists.get(term);
            for(Doc doc : docList){
                double score = doc.tw *  qItem.qw;
        
                if(! result.containsKey(doc.docId)){
                    result.put(doc.docId, score);
                }
                else{
                    score+= result.get(doc.docId);
                    result.put(doc.docId, score);
                }        
            }
        }
    }

    /*sort on score and print result*/
        int counter = 1;
        
        ArrayList<RankedResult> fResult = new ArrayList<>();
        
        LinkedHashMap sortedResult = Helper.SortHashMap(result);
        Set<Entry<Integer, Double>> sortedSet =  sortedResult.entrySet();
         /*System.out.println(String.format("%-15s %-40s %s", "Rank", "Resume", "Score"));
         System.out.println(String.format("%-15s %-40s %s", "----", "---------------", "---------"));*/
        
        for(Entry<Integer, Double> mapping : sortedSet ){
           /*System.out.println(String.format("%-15s %-40s %s", "Rank"+counter , docs.get(mapping.getKey()),   mapping.getValue()));*/
            resultRet[counter-1] = docs.get(mapping.getKey());
            RankedResult rItem = new RankedResult();
            rItem.Rank = counter;
            rItem.DocName = docs.get(mapping.getKey());
            rItem.Score = mapping.getValue();
            
            ArrayList<String> match = new ArrayList<>();
            
            for(Map.Entry item : queryTerms.entrySet() ){  
                String term = (String)item.getKey();
           
                if(docLists.containsKey(term)){ //termList.contains(term)
                    docList = docLists.get(term);
                    for(Doc doc : docList){
                        if(doc.docId == mapping.getKey()){
                          if(! match.contains(term))  {
                                match.add(term);   
                          }
                        }
                    }
                }
            }
            rItem.MatchedTerms = match;
            fResult.add(rItem);
            
            
            
            if(numOfRecords <= counter)
               break;
           counter++;               
        }
        
        //Attach matching terms
        

        
        return fResult;
     }
     
     
     
     public VsmEntity getVsmEntity(String labelName){         
        for(VsmEntity v : categoryList){
            if( v.getCategory().endsWith(labelName) ){
                return v;
             } 
         }
         return null;
     }
    
}    
    
    
    