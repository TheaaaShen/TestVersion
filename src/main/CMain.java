package main;


import java.util.ArrayList;

import debug.MyTimer;
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
    static final String STRUCTURE_LIBRARY_FILE = CARB_LIB_DIR + "/CarbbankLibAll.txt";
    
    // Output folder
    static final String outFolder = HOME_DIR + "/result/";
    
    // Spectra that should be loaded
//    static final String[] SPECTRA_FILES_LOADED = {
//            "/1579.mzXML", "/1579_1084.mzXML", "/1579_1302.mzXML", 
//            "/1579_1302_1084.mzXML"
//            };
    /*static final String[] SPECTRA_FILES_LOADED = {
            "/1579.mzXML", "/1579_1084.mzXML", "/1987_1302.mzXML",
            "/1579_1302_1084.mzXML"
    };*/
    static final String[] SPECTRA_FILES_LOADED = {
            "/2151.mzXML"
    };
    
//    File[] files = getFilesByPathAndSuffix(DIR, SUFIX);
//    
//    for (File file : files) {
//        PDBFileIO pdb = new PDBFileIO();
//        ArrayList<Atom> allAtoms = pdb.readFile(file);
//        Glycon gl = new Glycon(allAtoms);
//        gl.getMassCenter();
//        gl.getDragPoint();
//        
//        //gl.removeResidue(6);
//        gl.rotate();
//        double ans = gl.get2DArea(gl.convexHell2D());
//        System.out.println(file.getName()+"\t\t"+ans);
//        
//    }
    
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
//        test.batchWork(strucLib, fileList, cutTime, WIN, filterRatio,outFolder);
//        
//    }
    
    
    /**
     * <p>testing version of main function.
     *
     * @param args currently not used
     */
    public static void main(String[] args){
        
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
        for(String path: SPECTRA_FILES_LOADED){
            spectraFilePaths.add(SPECTRA_DIR + path);
        }
        
        MyTimer.showTime("load lib and spectra");
        
        batchwork.batchWork(spectraFilePaths, cutTime, WIN, filterRatio, outFolder);
        //batchwork.writeOut2(outFolder);
        MyTimer.showTime("end of program");
    }
    // SP = spectra
}
