/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;
import java.io.*;
import java.math.*;
import java.util.*;

import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;


/**
 *
 * @author Kumar
 */
 public class Helper {
    // to access stopword file 
    InputStream in = getClass().getResourceAsStream("/Utility/stopwords.txt");
        
    public static String[] GetFolders(String path){      
        File files = new File(path); 
        String[] arrFolders = files.list(new FilenameFilter(){     
                                @Override
                                public boolean accept(File current, String name) {
                                    return new File(current, name).isDirectory();
                                }              
                            });        
        return arrFolders;         
    }
    
    public static File GetFile(String fPath){
        File f = new File(fPath);
        if(f.exists() && !f.isDirectory()) { 
                return f;
        }
        
        return null;        
    }
    
    
    public static String[] GetFiles(String path){      
        File files = new File(path); 
        String[] arrFiles = files.list(new FilenameFilter(){     
                                @Override
                                public boolean accept(File current, String name) {
                                    return new File(current, name).isFile();
                                }              
                            });        
        return arrFiles;         
    }
    
    public static String GetFileContents(String filePath){
        StringBuffer strBldr = new StringBuffer();  
        try{
            Scanner scan = new Scanner(new File(filePath)); 
            while (scan.hasNextLine()) { 
                String line = scan.nextLine(); 
                strBldr = strBldr.append(line); 
            }             
        }
        catch (IOException io) { 
            System.out.println(io.getMessage()); 
        }         
        return strBldr.toString();        
    }
    
    public static ArrayList<String> GetPdfFileContentAsList(File fName){        
         ArrayList<String> strList = new ArrayList<>();
        try{
            //create file writer
            PDFParser parser = new PDFParser( new FileInputStream(fName));
            parser.parse();
            COSDocument cosDoc = parser.getDocument();
            PDFTextStripper pdfStripper = new PDFTextStripper();
            PDDocument pdDoc = new PDDocument(cosDoc);
            String parsedText = pdfStripper.getText(pdDoc);
            cosDoc.close();
            pdDoc.close(); 
            
            
            String terms[] = parsedText.split("[ .,?!:;$%/|<>'&*+()\\\"\\\\-\\\\^]+");
            for(String item : terms){
                 //optimization : leaving out one character tokens
                    if(item.length()>1)
                        strList.add(item.toLowerCase()); //case folding
                } 
        }
        catch(Exception e){
            System.out.println("Error occcurred while reading the pdf file");
        }  
        return strList;
    }
        
    
    public static ArrayList<String> GetFileContentAsList(String filePath){
        String line = null;
        ArrayList<String> strList = new ArrayList<>();  
        try{
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(filePath);
                
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
                
            while((line = bufferedReader.readLine()) != null) {
                String[] terms = line.split("[ .,?!:;$%/|<>'&*+()\\\"\\\\-\\\\^]+"); //[^\\w\\*]
                for(String item : terms){
                     //optimization : leaving out one character tokens
                    if(item.length()> 1)
                    strList.add(item.toLowerCase()); //case folding
                }    
            }            
            // Always close files.
            bufferedReader.close();   
        }            
        catch (IOException io) { 
            System.out.println(io.getMessage()); 
        }         
        return strList;      
    }
    
    public static ArrayList<String> GetStopWords(){
        ArrayList<String> stopWords = new ArrayList<>();
        Helper a = new Helper();        
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(a.in));
             while(br.readLine() != null){
                String stopWord = br.readLine();
                stopWords.add(stopWord);
            }
            br.close(); 
        }
        catch(IOException  ex){ 
            System.out.println(ex.getMessage()); 
        }
        
        return stopWords;
    }
    
    
     //Stemm String List
    public static ArrayList<String> GetStemmedList(ArrayList<String> source){
      ArrayList<String> stemmedList = new ArrayList<>();   
      Stemmer st = new Stemmer();
      for(String str : source){
        st.add(str.toCharArray(), str.length()); 
	st.stem(); 				
        stemmedList.add(st.toString());         
      }
      return stemmedList;        
    }
    
    
    public static LinkedHashMap SortHashMap(HashMap<Integer, Double> sourceMap){
                
        //custome comparer
        Comparator<Map.Entry<Integer, Double>> valueComparator = new Comparator<Map.Entry<Integer,Double>>() {
        @Override public int compare(Map.Entry<Integer, Double> e1, Map.Entry<Integer, Double> e2)
        { 
            BigDecimal aa = 
                new BigDecimal(e1.getValue(), MathContext.DECIMAL64).subtract(new BigDecimal(e2.getValue(), MathContext.DECIMAL64));
                                        
            return (aa.compareTo(BigDecimal.ZERO)> 0)?-1:1;                                 
        } };
        
        // Sort method needs a List, so let's first convert Set to List in Java 
        List<Map.Entry<Integer, Double>> listOfEntries = new ArrayList<>(sourceMap.entrySet());
        Collections.sort(listOfEntries, valueComparator); 
        LinkedHashMap<Integer, Double> sortedByValue = new LinkedHashMap<>(listOfEntries.size());
        for(Map.Entry<Integer, Double> entry : listOfEntries){
            sortedByValue.put(entry.getKey(), entry.getValue()); 
        }
        return sortedByValue;
    }
    
     public static LinkedHashMap SortHashMapStr(HashMap<String, Double> sourceMap){
                
        //custome comparer
        Comparator<Map.Entry<String, Double>> valueComparator = new Comparator<Map.Entry<String,Double>>() {
        @Override public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2)
        { 
            BigDecimal aa = 
                new BigDecimal(e1.getValue(), MathContext.DECIMAL64).subtract(new BigDecimal(e2.getValue(), MathContext.DECIMAL64));
                                        
            return (aa.compareTo(BigDecimal.ZERO)> 0)?-1:1;                                 
        } };
        
        // Sort method needs a List, so let's first convert Set to List in Java 
        List<Map.Entry<String, Double>> listOfEntries = new ArrayList<>(sourceMap.entrySet());
        Collections.sort(listOfEntries, valueComparator); 
        LinkedHashMap<String, Double> sortedByValue = new LinkedHashMap<>(listOfEntries.size());
        for(Map.Entry<String, Double> entry : listOfEntries){
            sortedByValue.put(entry.getKey(), entry.getValue()); 
        }
        return sortedByValue;
    }
    
    /*public static HashSet<String> InitializeVocabulary(){     
        HashSet<String> vocabulary = null;
        
     
        
        return vocabulary;   
    }*/
                
    public static void SerializeClassList(ArrayList<Klass> obj, String path){         
        try{
            FileOutputStream fileOut = new FileOutputStream(path + "\\classList.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
        }         
      catch(IOException i){      
        System.out.println("Error occurred while serialization class list");
      }        
    }
    
    public static ArrayList<Klass>  DeSerializeClassList(String path){
        ArrayList<Klass> kList = null;
        try{
            FileInputStream fileIn = new FileInputStream(path+"\\classList.ser");    
            ObjectInputStream in = new ObjectInputStream(fileIn); 
            kList = (ArrayList<Klass>)in.readObject();
            in.close();
            fileIn.close();                  
        }
        catch(IOException i){      
            System.out.println("class list error");       
        }
        catch(ClassNotFoundException c){      
            System.out.println("class list class not found");     
        }
       return kList; 
    }
    
    public static void SerializeVocabulary(HashSet<String> obj, String path){
        try{
            FileOutputStream fileOut = new FileOutputStream(path+"\\vocabulary.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
        }         
        catch(IOException i){      
        System.out.println("Error occurred while serialization vocabulary list");
      }          
        
    }
    
    public static HashSet<String> DeSerializeVocabulary(String path){
    HashSet<String> vocabulary = null;   
        try{
            FileInputStream fileIn = new FileInputStream(path+"\\vocabulary.ser");    
            ObjectInputStream in = new ObjectInputStream(fileIn); 
            vocabulary = (HashSet<String>)in.readObject();
            in.close();
            fileIn.close();                  
        }
        catch(IOException i){      
            System.out.println("Employee class error");       
        }
        catch(ClassNotFoundException c){      
            System.out.println("Employee class not found");     
        }
    return vocabulary;        
    }
    
    public static int GetDocumentCount( ArrayList<Klass> obj){
        int docCount = 0;        
        for(Klass k : obj){
            docCount += k.getClassDocCount();            
        }                
        return docCount;        
    }
    
    
    public static void SerializeCategoryList(ArrayList<VsmEntity> obj, String path){
        try{
            FileOutputStream fileOut = new FileOutputStream(path +"\\categoryList.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
        }         
        catch(IOException i){      
        System.out.println("Error occurred while serialization category list");
      }          
        
    }
    
    
    public static ArrayList<VsmEntity> DeSerializeCategoryList(String path){
        ArrayList<VsmEntity> classList = null;
        try{
            FileInputStream fileIn = new FileInputStream(path + "\\categoryList.ser");    
            ObjectInputStream in = new ObjectInputStream(fileIn); 
            classList = (ArrayList<VsmEntity>)in.readObject();
            in.close();
            fileIn.close();                  
        }
        catch(IOException i){      
            System.out.println("CategoryList class error");       
        }
        catch(ClassNotFoundException c){      
            System.out.println("CategoryList class not found");     
        }                            
        return classList;        
    }
    
    
    public static void SerializeConfusionMatrix(HashMap <String,HashMap<String, Integer>> obj, String path){
        try{
            FileOutputStream fileOut = new FileOutputStream(path +"\\confusionMatrix.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
        }         
        catch(IOException i){      
        System.out.println("Error occurred while serialization confusion matrix");
      }          
        
    }
    
    
    public static HashMap <String,HashMap<String, Integer>> DeSerializeConfusionMatrix(String path){
        HashMap <String,HashMap<String, Integer>> confusionMatrix = null;
        try{
            FileInputStream fileIn = new FileInputStream(path + "\\confusionMatrix.ser");    
            ObjectInputStream in = new ObjectInputStream(fileIn); 
            confusionMatrix = ( HashMap <String,HashMap<String, Integer>>)in.readObject();
            in.close();
            fileIn.close();                  
        }
        catch(IOException i){      
            System.out.println("Error occurred while deserialization confusion matrix");       
        }
        catch(ClassNotFoundException c){      
            System.out.println("Confusion matrix class not found");     
        }                            
        return confusionMatrix;        
    }    
}
