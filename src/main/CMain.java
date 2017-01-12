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
    
    static final String HOME_DIR = ".";
    /** library of structure of carb lib dir*/
    static final String CARB_LIB_DIR = HOME_DIR + "/carbbank"; 
    // The folder containing all spectra
    static final String SPECTRA_DIR = HOME_DIR + "/spectra";
    
    /** The Constant SPECTRA_FILE_SUFFIX: the suffix of one spectrum. */
    static final String SPECTRA_FILE_SUFFIX = ".mzXML";
    
    /** The name of the library file storing glycal structures */
    static final String STRUCTURE_LIBRARY_FILE = CARB_LIB_DIR + 
            //"/filter_mass/filter-mass.1053.5201.txt";
            //"/RnaseB_lib_only_high_man.txt";
            //"/lib_HMO.txt";
            "/lib_top5_new.txt";
    
    // Output folder
    static final String OUT_FOLDER = HOME_DIR + "/result";
    
    // Spectra that should be loaded
    static final String[] SPECTRA_FILES_LOADED = {
            "/HMO_1549/1549.mzXML", "/HMO_1549/1549_1086.mzXML",
            "/HMO_1549/1549_486.mzXML", "/HMO_1549/1549_660.mzXML", 
            "/HMO_1549/1549_880.mzXML", 
//            "/Man-7D3/1988_667.mzXML", "/Man-7D3/1988_667_447.mzXML",
//            "/Man-7D3/1988_1084.mzXML", "/Man-7D3/1988_1084_839.mzXML"
    };
    
    /*static final String[] SPECTRA_FILES_LOADED = {
            "/2151.mzXML"
    };*/
    
    static final double filterRatio = 0.01; // filtering < maxInten * filterRatio
    public static final boolean sort_spectra = true;
    public static final boolean compute_DP = false;
    // write a !final_result.txt in order to get the final result
    public static final boolean write_final_result = true;
    // redirect the program's standard output to a specific file 
    public static final boolean redirect_program_output = false;
    public static final String redirect_output_file = 
                                HOME_DIR + "/result/output.txt";
    // whether compute all sub folders or just a few spectra defined 
    // in SPECTRA_FILES_LOADED
    public static final boolean batch_version = true;
        
    /**
     * <p>testing version of main function.
     *
     * @param args currently not used
     */
    public static void main(String[] args){
        if(redirect_program_output){
            try {
                PrintStream ps = new PrintStream(new FileOutputStream(redirect_output_file));
                System.setOut(ps); 
            } catch (FileNotFoundException e) {
                Print.pl("Redirecting output to file enters an error.");
                e.printStackTrace();
            }  
        }
        if(batch_version){
            mainBatch(args);
        } else {
            mainSingle(args);
        }
    }
    public static void mainSingle(String[] args){
        
        int cutTime=3; // 碎裂次数
        double WIN=0.6; // 搜索窗口，匹配peak与peak的容差
        // 正常是0.3，由于仪器校准，所以0.6
        //double filterRatio=0.01; // 过滤< maxInten * filterRatio
        
        MyTimer.setStart();
        
        // load structure library (static)
        BatchWork.loadStrucLib(STRUCTURE_LIBRARY_FILE);
        
        BatchWork batchwork = new BatchWork();
        
        // load spectra files (input files)
        ArrayList<String> spectraFilePaths = new ArrayList<String>();
//        spectraFilePaths = FileUtil.getFilePathsByPathAndSuffix(
//                spectra_files_directory, SPECTRA_FILE_SUFFIX);
        for(String path: SPECTRA_FILES_LOADED){
            spectraFilePaths.add(SPECTRA_DIR + path);
        }
        
        MyTimer.showTime("load lib and spectra");
        
        batchwork.batchWork(spectraFilePaths, cutTime, WIN, filterRatio, OUT_FOLDER);
        //batchwork.writeOut2(outFolder);
        MyTimer.showTime("end of program");
    }
    
    public static void mainBatch(String[] args){// batched version
        
        int cutTime=3; // 碎裂次数
        double WIN=0.6; // 搜索窗口，匹配peak与peak的容差
        // 正常是0.3，由于仪器校准，所以0.6
        //double filterRatio=0.05; // 过滤< maxInten * filterRatio
        
        MyTimer.setStart();
        
        // load structure library (static)
        BatchWork.loadStrucLib(STRUCTURE_LIBRARY_FILE);
        
        ArrayList<String> dirNames = FileUtil.listDirNames(SPECTRA_DIR);
        String spectra_files_directory;
        String outFolder;
        for(String dirName: dirNames){
            Print.pl("!dealing with folder: "+ dirName);
            spectra_files_directory = SPECTRA_DIR + "/" + dirName;
            FileUtil.mkDir(OUT_FOLDER, dirName);
            outFolder = OUT_FOLDER +"/" + dirName + "/";
            BatchWork batchwork = new BatchWork();
            
            // load spectra files (input files)
            ArrayList<String> spectraFilePaths;
            spectraFilePaths = FileUtil.getFilePathsByPathAndSuffix(
                    spectra_files_directory, SPECTRA_FILE_SUFFIX);
            MyTimer.showTime("load lib and spectra");
            
            batchwork.batchWork(spectraFilePaths, cutTime, WIN, filterRatio, outFolder);
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
