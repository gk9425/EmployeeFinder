# EmployeeFinder

EmployeeFinder’; a tool used to retrieve and rank resumes by estimation of their usefulness for a user query (employer’s requirement for a job opening), in the form of a job description document. The tool made use of data mining algorithms for classification and ranking (Multinomial Naive Bayes, Term Frequency – Inverted Document Frequency (Tf-IDF), Porter Stemmer) that were implemented in Java. The user interface was developed using Java Swing (AWT).

Steps

In order to successfully run “Employee Finder” program follow the steps mentioned below: 

Step1. Unzip “KPT_Project_Group1”. The folder consists of following 3 zip file(s) and 1 document:
1.	EmployeeFinder.zip
2.	Data.zip
3.	Jar.zip
4.	Readme.doc

Step 2: Unzip the EmployeeFinder.zip and save files on the local disk. EmployeeFinder folder consists of folders and files which are created for a project in NetBeans IDE.
Step 3: Unzip the data.zip folder and save files on the local disc. Make sure the project folder and the Data folder are at the same level. Otherwise need to set the ‘path’ in the “EmployerFinder.java” file.

The data folder consists of 2 folders 
•	Job - It consists of all the subfolders related to job description
•	Resume- It consists of all the subfolders related to resume

The Job folder again consists of following folders:

1.	Test – Contains job description files for each category for the purpose of calculating algorithms effectiveness. Categories available are Computer, Healthcare, Hospitality and Management. For now 50 files exists under each category
2.	Train - Contains job description files for each category for the purpose of training data to predict category for a new job description. Categories available along their files count are Computer (199), Healthcare (177), Hospitality (172) and Management (199).
3.	Resources: Contains serialized files which are cached for the program to enhance user experience. . The files need to be deleted if either new files are added or the content of the existing files are deleted.

The Resume folder again consists of following folders:
4.	Test – Contains resume files for each category for the purpose of calculating algorithms effectiveness. Categories available are Computer, Healthcare, Hospitality and Management. For now 50 files exists under each category
5.	Train - Contains resume files for each category for the purpose to predict category for a new resume and to retrieve ranked resumes for the job description which is provided as input. Categories available along their files count are Computer (447), Healthcare (351), Hospitality (489) and Management (464).
6.	Resources: Contains serialized files which are cached for the program to enhance user experience. The files need to be deleted if either new files are added or the content of the existing files are deleted.
Note:  The arrangement of the sub-folders in the Data folder need to follow the same structure as mentioned above since the application used the relative paths.

Step 4: Open EmployeeFinder folder as a project in NetBeans IDE.

Step 5: Unzip the ‘Jar.zip’ folder. In the NetBeans IDE, in the project navigation right click on the ‘Library’ folder to add/resolve ‘jar’ files to the project. The jar files need to be added from the Jar folder.

Step 6: Run the EmployeeFinder.java project

Step 7: The application presents the user with the functionality to either retrieve resumes based on a job description or to classify a particular resume. 

 


