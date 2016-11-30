package main;


import java.util.ArrayList;

import debug.MyTimer;
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
            "/filter_mass/11_24.txt";
            //"/RnaseB_lib.txt";
            //"/standard_lib.txt";
    
    // Output folder
    static final String OUT_FOLDER = HOME_DIR + "/result";
    
    // Spectra that should be loaded
//    static final String[] SPECTRA_FILES_LOADED = {
//            "/1579.mzXML", "/1579_1084.mzXML", "/1579_1302.mzXML", 
//            "/1579_1302_1084.mzXML"
//            };
    static final String[] SPECTRA_FILES_LOADED = {
            "/Man-7D3/1988.mzXML", "/Man-7D3/1988_1712.mzXML",
            "/Man-7D3/1988_1712_667.mzXML", "/Man-7D3/1988_709.mzXML", 
            "/Man-7D3/1988_667.mzXML", "/Man-7D3/1988_667_447.mzXML",
            "/Man-7D3/1988_1084.mzXML", "/Man-7D3/1988_1084_839.mzXML"
    };
    
    /*static final String[] SPECTRA_FILES_LOADED = {
            "/2151.mzXML"
    };*/
    
    
//    /**
//     * publication version
//     *
//     * @param args the arguments
//     */
//    public static void main(String[] args)
//    {
//        
//        int cutTime=3;
//        double WIN=0.6;
//        double filterRatio=0.01;
////        filterRatio=Double.parseDouble(args[0]);
//        String  strucLib=args[0];
//        String outFolder=args[1];
//        BatchWork.loadStrucLib(strucLib);
//        BatchWork test=new BatchWork();
//        ArrayList<String> fileList=new ArrayList<String>();
//        for(int i=2;i<args.length;i++)
//        {
//            fileList.add(args[i]);
//        }
//        test.batchWork(strucLib, fileList, cutTime, WIN, filterRatio,OUT_FOLDER);
//        
//    }
    
    
    /**
     * <p>testing version of main function.
     *
     * @param args currently not used
     */
    /*public static void main(String[] args){
        
        int cutTime=3; // 碎裂次数
        double WIN=0.6; // 搜索窗口，匹配peak与peak的容差
        // 正常是0.3，由于仪器校准，所以0.6
        double filterRatio=0.01; // 过滤< maxInten * filterRatio
        
        MyTimer.setStart();
        
        // load structure library (static)
        BatchWork.loadStrucLib(STRUCTURE_LIBRARY_FILE);
        
        BatchWork batchwork=new BatchWork();
        
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
    }*/
    
    public static void main(String[] args){// batched version
        
        int cutTime=3; // 碎裂次数
        double WIN=0.6; // 搜索窗口，匹配peak与peak的容差
        // 正常是0.3，由于仪器校准，所以0.6
        double filterRatio=0.01; // 过滤< maxInten * filterRatio
        
        MyTimer.setStart();
        
        // load structure library (static)
        BatchWork.loadStrucLib(STRUCTURE_LIBRARY_FILE);
        
        ArrayList<String> dirNames = FileUtil.listDirNames(SPECTRA_DIR);
        String spectra_files_directory;
        String outFolder;
        for(String dirName: dirNames){
            System.out.println("!dealing with folder: "+ dirName);
            spectra_files_directory = SPECTRA_DIR + "/" + dirName;
            FileUtil.mkDir(OUT_FOLDER, dirName);
            outFolder = OUT_FOLDER +"/" + dirName + "/";
            BatchWork batchwork=new BatchWork();
            
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
}
