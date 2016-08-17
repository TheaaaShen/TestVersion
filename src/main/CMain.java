package main;


import java.util.ArrayList;

import tools.BatchWork;

public class CMain {
    
    static final String HOME_DIR = "..";
    /** library of structure of carb lib dir*/
    static final String CARB_LIB_DIR = HOME_DIR + "/carbbank"; 
    // spectra dir
    static final String SPECTRA_DIR = HOME_DIR + "/spectra";
    // structure file from lib
    static final String strucFile = CARB_LIB_DIR + "/CarbbankLibAll.txt";
    
    // Output folder
    static final String outFolder = HOME_DIR + "/Result";
    
    // Spectra that should be loaded
    static final String[] SPECTRA_FILES_LOADED = {
            "/1579.mzXML", "/1579_1084.mzXML"};
    
    
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
        
        int cutTime=3;
        double WIN=0.6;
        double filterRatio=0.01;
//        String base="E:/Glycan/Glycan3/";
//        String base2="E:/Glycan/Glycan5/2015-08-14/spectra/20151023/";
//        String strucLib="E:/Glycan/Glycan5/2015-06-11/lib/CandiStructureLib.txt";
//        String outFolder="E:/Glycan/Glycan5/2015-06-11/";
        
//        String base2="E:/Glycan/Glycan5/2015-06-11/spectra/mzxml6/1783/";
        
        //String ms2=base2+"/1579.mzXML";
        //String ms3=base2+"/1579_1084.mzXML";
        
        // load structure library (static)
        BatchWork.loadStrucLib(strucFile);
        BatchWork batchwork=new BatchWork();
        
        // load spectra files (input files)
        ArrayList<String> spectraFilePaths = new ArrayList<String>();
        for(String path: SPECTRA_FILES_LOADED){
            spectraFilePaths.add(SPECTRA_DIR + path);
        }
        
        batchwork.batchWork(spectraFilePaths, cutTime, WIN, filterRatio, outFolder);
        //batchwork.writeOut2(outFolder);
        
    }
    // SP = spectra
}
