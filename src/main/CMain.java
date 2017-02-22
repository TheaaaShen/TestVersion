package main;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import debug.MyTimer;
import debug.Print;
import file.util.FileUtil;
import tools.BatchWork;

public class CMain {
    
    
        
    /**
     * <p>testing version of main function.
     *
     * @param args currently not used
     */
    public static void main(String[] args){
        if(Settings.redirect_program_output){
            try {
                PrintStream ps = new PrintStream(new FileOutputStream(Settings.redirect_output_file));
                System.setOut(ps); 
            } catch (FileNotFoundException e) {
                Print.pl("Redirecting output to file enters an error.");
                e.printStackTrace();
            }  
        }
        if(Settings.batch_version){
            mainBatch(args);
        } else {
            mainSingle(args);
        }
    }
    public static void mainSingle(String[] args){
        
        int cutTime = Settings.maxNumberOfCleavage; // ËéÁÑ´ÎÊý
        double WIN = Settings.peak_matching_duration; // ËÑË÷´°¿Ú£¬Æ¥ÅäpeakÓëpeakµÄÈÝ²î
        
        MyTimer.setStart();
        
        // load structure library (static)
        BatchWork.loadStrucLib(Settings.STRUCTURE_LIBRARY_FILE);
        
        BatchWork batchwork = new BatchWork();
        
        // load spectra files (input files)
        ArrayList<String> spectraFilePaths = new ArrayList<String>();
//        spectraFilePaths = FileUtil.getFilePathsByPathAndSuffix(
//                spectra_files_directory, SPECTRA_FILE_SUFFIX);
        for(String path: Settings.SPECTRA_FILES_LOADED){
            spectraFilePaths.add(Settings.SPECTRA_DIR + path);
        }
        
        MyTimer.showTime("load lib and spectra");
        
        batchwork.batchWork(spectraFilePaths, cutTime, WIN, Settings.filterRatio, Settings.OUT_FOLDER);
        //batchwork.writeOut2(outFolder);
        MyTimer.showTime("end of program");
    }
    
    public static void mainBatch(String[] args){// batched version
        
        int cutTime = Settings.maxNumberOfCleavage; // ËéÁÑ´ÎÊý
        double WIN = Settings.peak_matching_duration; // ËÑË÷´°¿Ú£¬Æ¥ÅäpeakÓëpeakµÄÈÝ²î
        
        MyTimer.setStart();
        
        // load structure library (static)
        BatchWork.loadStrucLib(Settings.STRUCTURE_LIBRARY_FILE);
        
        ArrayList<String> dirNames = FileUtil.listDirNames(Settings.SPECTRA_DIR);
        String spectra_files_directory;
        String outFolder;
        for(String dirName: dirNames){
            Print.pl("!dealing with folder: "+ dirName);
            spectra_files_directory = Settings.SPECTRA_DIR + "/" + dirName;
            FileUtil.mkDir(Settings.OUT_FOLDER, dirName);
            outFolder = Settings.OUT_FOLDER +"/" + dirName + "/";
            BatchWork batchwork = new BatchWork();
            
            // load spectra files (input files)
            ArrayList<String> spectraFilePaths;
            spectraFilePaths = FileUtil.getFilePathsByPathAndSuffix(
                    spectra_files_directory, Settings.SPECTRA_FILE_SUFFIX);
            MyTimer.showTime("load lib and spectra");
            
            batchwork.batchWork(spectraFilePaths, cutTime, WIN, Settings.filterRatio, outFolder);
            //batchwork.writeOut2(outFolder);
            MyTimer.showTime("end of program");
        }
    }
    // SP = spectra
    
//  /**
//  * publication version
//  *
//  * @param args the arguments
//  */
// public static void main(String[] args)
// {
//     
//     int cutTime=3;
//     double WIN=0.6;
//     double filterRatio=0.01;
////     filterRatio=Double.parseDouble(args[0]);
//     String  strucLib=args[0];
//     String outFolder=args[1];
//     BatchWork.loadStrucLib(strucLib);
//     BatchWork test=new BatchWork();
//     ArrayList<String> fileList=new ArrayList<String>();
//     for(int i=2;i<args.length;i++)
//     {
//         fileList.add(args[i]);
//     }
//     test.batchWork(strucLib, fileList, cutTime, WIN, filterRatio,OUT_FOLDER);
//     
// }
}
