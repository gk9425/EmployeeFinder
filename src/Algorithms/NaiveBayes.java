/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import Utility.*;
import java.util.*;

/**
 *
 * @author Kumar
 */
public class NaiveBayes {
    
    ArrayList<Klass> classList;
    HashSet<String> vocabulary;
    int totalDocCount;
    ArrayList<String> stopWords;        
    private  LinkedHashMap<String, Double> detailResult;
    
    public NaiveBayes(String action, String path){
        
    stopWords = Helper.GetStopWords();     
    String resourcePath = (action.equals("Classify")) ? "\\Resume\\Resources\\Classify"
                                                     : "\\Job\\Resources\\Classify" ;   
        
    resourcePath =  path +  resourcePath;                                         
    String[] resources = Helper.GetFiles(resourcePath);
    int resLen = resources.length;
    if( resLen > 0 ){
        classList = Helper.DeSerializeClassList(resourcePath);
        vocabulary = Helper.DeSerializeVocabulary(resourcePath);
        totalDocCount = Helper.GetDocumentCount(classList);
    }    
    else{        
        //initialize variables        
        classList = new ArrayList<>(); 
        vocabulary = new HashSet<>();
        path += (action.equals("Classify")) ? "\\Resume\\Train" : "\\Job\\Train";
        String[] classLabels = Helper.GetFolders(path);

        //iterate classes
        int classCount = classLabels.length;
        for(int i = 0; i < classCount; i++){ 

            String classlabel =  classLabels[i];         
            String[] files = Helper.GetFiles(path+"\\"+classlabel); 

            //iterate files
            int classDocCount =  files.length;
            totalDocCount += classDocCount;

            //counts all token for a class
            int classTokenCount = 0;

            HashMap <String, Double> classTermCondProb = new HashMap <>();

            for(int j = 0; j < classDocCount; j++ ){

                //String strFileText = Helper.GetFileContents(path +"\\"+ classlabel +"\\"+ files[j]); 
                
                ArrayList<String> strFileTextList = Helper.GetFileContentAsList(path +"\\"+ classlabel +"\\"+ files[j]);
                //String[] tokens = strFileText.split("[ .,?!:;$%/|<>'&*+()\\\"\\\\-\\\\^]+"); //[\" ()_,?:;%&-]+
                strFileTextList.removeAll(stopWords);
                classTokenCount = strFileTextList.size();
                for(String token : strFileTextList){
                    //optimization : leaving out one character tokens
                    //if(token.length() > 1){
                        //classTokenCount++;
                        // add to vocabulary 
                        vocabulary.add(token);
                        if(classTermCondProb.containsKey(token)) {
                            double count = classTermCondProb.get(token);
                            // Multinomial : incrementing count
                            classTermCondProb.put(token, count+1 ); 
                        }
                        else{
                            classTermCondProb.put(token, 1.0 );                            
                        }
                    }                    
                //}                        
            } // loop for files in a class

            Klass objClass = new Klass(i, classDocCount,  classlabel);
            objClass.setClassTokenCount(classTokenCount);
            objClass.setClassTermCondProb(classTermCondProb);

            //add class object to list
            classList.add(objClass);

        } //loop for each class

        int vCount = vocabulary.size(); 
        //compute the class probability
        for (Klass c : classList){
            HashMap <String, Double> condProb = c.getClassTermCondProb();                 
            Iterator<Map.Entry<String, Double>> iterator = condProb.entrySet().iterator(); 
                while(iterator.hasNext()){
                    Map.Entry<String, Double>cpItem = iterator.next(); 
                    String tK = cpItem.getKey(); 
                    Double tC = cpItem.getValue();
                    tC = (tC+1)/(c.getClassTokenCount()+ vCount);
                    condProb.put(tK, tC);
                } 

        }
        
        Helper.SerializeClassList(classList, resourcePath);
        Helper.SerializeVocabulary(vocabulary, resourcePath);
    }    
}
        
    public String classify(ArrayList<String> docTokens, boolean detailFlag){     
       
        //Initialize varaibels for probability calculations                         
        int vCount = vocabulary.size();
        int classCount = classList.size();
	double[] score = new double[classCount]; 
        
        //Calculate the class probability
        for( int i=0; i<score.length; i++ ){ 
            score[i] = Math.log10(classList.get(i).getClassDocCount()*1.0/totalDocCount); 
        }
                        
	//Iterate for each class 
        //Add probabilty for terms
        //Apply Laplace smoothing
        
	for(int i=0; i<classCount; i++){             
            HashMap <String, Double> condProb = classList.get(i).getClassTermCondProb();
            int classTC = classList.get(i).getClassTokenCount();
            for(String token: docTokens){                    
                if(condProb.containsKey(token)) 
                    score[i] += Math.log10(condProb.get(token)); 
                else
                    //Laplace smoothing
                    score[i] += Math.log10(1.0/(classTC + vCount)); 
                } 
        }
        
        //convert log value back to base 10 value
        //for(int i = 0 ; i< classCount; i++){               
           // score[i] =  Math.exp(score[i]);
        //}
            
        
        //Set class probability when flag passed 'detailFlag' is true
        if(detailFlag){
            HashMap <String,Double> result = new  HashMap<String, Double>();                  
            for(int i = 0 ; i< classCount; i++){               
                result.put(getClassName(i), score[i]); //Math.exp()
            } 
                
            detailResult =  Helper.SortHashMapStr(result);
        } 
       
        //Logic to fetch maximum value
        int classID = 0; 
        double maxScore = score[0];
        for(int i=1;i<score.length;i++){ 
            if(score[i]>maxScore){
                maxScore =  score[i];
                classID = i;
            }                                      
        } 
        
        //return Name of the predicted class label
        return getClassName(classID);
    }
    
    public  HashMap <String,HashMap<String, Integer>> getAccuracy (String path, String category ){
        
        HashMap <String,HashMap<String, Integer>> cMatrix; 
        String resourcePath = (category.equals("Search"))?"\\Job\\Resources\\Accuracy"
                              : "\\Resume\\Resources\\Accuracy";
        resourcePath = path+resourcePath;
        
        String[] resources = Helper.GetFiles(resourcePath);
        int resLen = resources.length;
        if( resLen > 0 ){
            cMatrix = Helper.DeSerializeConfusionMatrix(resourcePath);
        }    
        else{        
        //Variables decelaration              
        HashMap<String, Integer> cMatrixRow;
        String[] classLabels;
        int classCount;
        
        //Determines on what category the accuracy needs to be determined                
        String testPath = (category.equals("Search"))?"\\Job\\Test": "\\Resume\\Test";
        testPath  =  path + testPath; 
       
        //Initialize confusion matrix variable
        cMatrix = new HashMap <String,HashMap<String, Integer>>();
        
        //Get all class labels from test folder
        //Determine number of classes
        classLabels = Helper.GetFolders(testPath);
        classCount = classLabels.length;
       
        //Iterate each class label         
        for( int i = 0; i < classCount; i++ ){
            
            //Variables decelaration
            String classlabel;
            String[] files;
            int classDocCount;
            
            //initialize row for confusion matrix
            cMatrixRow = new HashMap<String, Integer>();            
            classlabel =  classLabels[i];
            
            //Gets all files for the class
            //Iterate files
            files = Helper.GetFiles(testPath+"\\"+classlabel);                          
            classDocCount =  files.length;

            for(int j = 0; j < classDocCount; j++ ){
                //Variable decleration
                ArrayList<String> strFileTextList;
                String predictedClass;
                
                //Fetch file contents in a list
                //Predict class
                strFileTextList = Helper.GetFileContentAsList(testPath +"\\"+ classlabel +"\\"+ files[j]);                              
                predictedClass = classify(strFileTextList, false);
                
                //Build confusion matrix row
                if(cMatrixRow.containsKey(predictedClass)){
                    //Entry is present
                    int count = cMatrixRow.get(predictedClass);
                    count++;
                    cMatrixRow.put(predictedClass,count);
                }
                else{
                    //Enter first entry
                    cMatrixRow.put(predictedClass, 1); 
                }
            }
            
            //Add row  to confusion matrix
            cMatrix.put(classlabel, cMatrixRow);
        } 
     
        Helper.SerializeConfusionMatrix(cMatrix, resourcePath);
     }    
      //return confusion matrix
        return cMatrix;  
    }
                   
    public LinkedHashMap<String, Double> getResultDetails(){
        
       return detailResult;         
    }
    
    public void ReSetResultDetails(){
        
        detailResult = null;        
    }
    
    public String getClassName(int classId){
        
        for (Klass c : classList){
            if(c.getClassId() == classId){
                return c.getClassName();
            }            
        }
        return "";
    }    
}
