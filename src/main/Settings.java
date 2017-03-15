package main;

/**
 * The Class Settings.
 * This class stores some variables and constants used in the program.
 */
public class Settings {
    
    // program input
    public static final String HOME_DIR = ".";
    
    /** library of structure of carb lib dir*/
    public static final String CARB_LIB_DIR = HOME_DIR + "/carbbank"; 
    
    /** The folder containing all spectra**/
    public static final String SPECTRA_DIR = HOME_DIR + "/spectra";
    
    /** The Constant SPECTRA_FILE_SUFFIX: the suffix of one spectrum. */
    public static final String SPECTRA_FILE_SUFFIX = ".mzXML";
    
    /** The name of the library file storing glycan structures */
    public static String STRUCTURE_LIBRARY_FILE = CARB_LIB_DIR + 
            "/lib_top5_new.txt";
    
    /** Output folder **/
    public static final String OUT_FOLDER = HOME_DIR + "/result";
    
    /** 
     * Spectra that should be loaded. 
     * This variable determines the input spectra when batch_version is 
     * set to false.
     **/
    static final String[] SPECTRA_FILES_LOADED = {
            "/HMO_1549/1549.mzXML", "/HMO_1549/1549_1086.mzXML",
            "/HMO_1549/1549_486.mzXML", "/HMO_1549/1549_660.mzXML", 
            "/HMO_1549/1549_880.mzXML", 
    };
    
    /** whether write a !final_result.txt in order to get the final result **/
    public static boolean write_final_result = true;
    
    /** whether redirect the program's standard output to a specific file **/ 
    public static boolean redirect_program_output = false;
    
    /** The output file of the program's redirected standard output. **/ 
    public static String redirect_output_file = 
                                HOME_DIR + "/result/output.txt";
    
    /**
     * whether compute all sub folders or just a few spectra defined
     * in SPECTRA_FILES_LOADED
     */
    public static boolean batch_version = true;
    
    // Parameters in the process of computation:
    
    /** 
     * <p>This constant is the searching window of Mass of parent iron.
     * This is used when searching candidate structures.
     * 搜索质量的容差
     */
    public static final double SEARCHING_WINDOW = 2;
    
    /** filtering all peaks of which the intensity is < maxInten * filterRatio **/
    public static double filterRatio = 0.01; 
    
    /** Whether sort all input spectra. **/
    public static boolean sort_spectra = true;
    
    /** Whether compute the distinguishing power. **/
    public static boolean compute_DP = false;
    
    /** 碎裂次数 **/
    public static int maxNumberOfCleavage = 3;
    
    /**
     * 搜索窗口，匹配peak与peak的容差
     * 正常是0.3，由于仪器校准，所以0.6
     */
    public static double peak_matching_duration = 0.6;
    
    // Parameters in scoring function
    public static double scoreSumNPlusLogInts_alpha = 5;
}
