package debug;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import file.util.FileUtil;
import main.Settings;
import tools.BatchWork;

public class Top5_Main {
    
    /** The glycans to be tested. */
    static String[] glycan = {"2-FL","3-FL","A2","L735","L736","Man5","Man6","Man7d1",
            "Man8_old","Man9_old","NA2","NA3","NA4","NGA2","NGA3","NGA4",  // Standards
            "1579", "1783", "1988", "2192", "2396"};  // RnaseB
    
    /** The correct number in result file corresponding to glycan array. */
    static int[] correctNo = {2,2,0,9,8,0,0,1,0,0,1,2,0,3,0,0,
            0, 0, 0, 0, 0};
    
//    static String[] glycan = {"Man5"};
//    static int[] correctNo = {0};
//    
    /** The correct candidate index of glycan. */
    static Map<String, Integer> correctCandidateIndex;
    
    static ArrayList<String> actual_glycan_list;
    
    
    /**
     * Initial some variables in Settings and this class.
     */
    public static void init(){
        Settings.STRUCTURE_LIBRARY_FILE = Settings.CARB_LIB_DIR + 
                "/lib_top5_new.txt";
        Settings.batch_version = true;
        Settings.redirect_program_output = false;
        Settings.sort_spectra = true;
        Settings.compute_DP = false;
        Settings.write_final_result = true;
        Settings.redirect_program_output = false;
        Settings.redirect_output_file = Settings.HOME_DIR 
                + "/result/Top5_output.txt";
        
        correctCandidateIndex = new HashMap<String, Integer>();
        for(int i = 0;i < glycan.length;i++){
            correctCandidateIndex.put(glycan[i], Integer.valueOf(correctNo[i]));
        }
        actual_glycan_list = new ArrayList<String>();
        
        if(Settings.redirect_program_output){
            try {
                PrintStream ps = new PrintStream(new FileOutputStream(Settings.redirect_output_file));
                System.setOut(ps); 
            } catch (FileNotFoundException e) {
                Print.pl("Redirecting output to file enters an error.");
                e.printStackTrace();
            }  
        }
    }
    
    
    /**
     * The main method in Top5_Main is used to test top5 data.
     *
     * @param args the arguments
     */
    public static void main(String[] args){
        init();
        MyTimer.setStart();
        ArrayList<Double> resultOfAllCandi = new ArrayList<Double>();
        
        // load structure library (static)
        BatchWork.loadStrucLib(Settings.STRUCTURE_LIBRARY_FILE);
        
        ArrayList<String> dirNames = FileUtil.listDirNames(Settings.SPECTRA_DIR);
        String spectra_files_directory;
        String outFolder;
        double opt_alpha = 0;
        double opt_jf = 0;
        ArrayList<Double> opt_scores = null;
        
//        for(double alpha = 1; alpha < 10.0; alpha += 1){
            Settings.scoreSumNPlusLogInts_alpha = 0;
            resultOfAllCandi.clear();
            for(String dirName: dirNames){
                Print.pl("!dealing with folder: "+ dirName);
                spectra_files_directory = Settings.SPECTRA_DIR + "/" + dirName;
                FileUtil.mkDir(Settings.OUT_FOLDER, dirName);
                outFolder = Settings.OUT_FOLDER +"/" + dirName + "/";
                
                if(!correctCandidateIndex.containsKey(dirName)){
                    continue;
                } 
                actual_glycan_list.add(dirName);
                
                int rightIndex = correctCandidateIndex.get(dirName).intValue();
                Print.pl("right Index = " + rightIndex);
                
                BatchWork batchwork = new BatchWork();
                
                // load spectra files (input files)
                ArrayList<String> spectraFilePaths;
                spectraFilePaths = FileUtil.getFilePathsByPathAndSuffix(
                        spectra_files_directory, Settings.SPECTRA_FILE_SUFFIX);
                MyTimer.showTime("load lib and spectra");
                
                batchwork.batchWork(spectraFilePaths, Settings.maxNumberOfCleavage,
                        Settings.peak_matching_duration, Settings.filterRatio, outFolder);
                
                double[] finalProbablities = batchwork.getFinalScoreArray();
                double correctScore = finalProbablities[rightIndex];
                resultOfAllCandi.add(Double.valueOf(correctScore));
                Print.pl("finalProbablities: " + finalProbablities);
                Print.pl("rightProbablity: " + finalProbablities[rightIndex]);
                
                MyTimer.showTime("end of program");
            }
            printTestResult(resultOfAllCandi);
            double jf = computeJudgeFunciton(resultOfAllCandi);
//            Print.pl("alpha: "+alpha+"\tjudge function: "+jf);
            if(jf > opt_jf){
                opt_jf = jf;
//                opt_alpha = alpha;
                opt_scores = cloneList(resultOfAllCandi);
            }
//        }
        Print.pl("opt_jf = "+opt_jf+"\topt_alpha = "+opt_alpha);
        printTestResult(opt_scores);
    }
    
    public static void printTestResult(ArrayList<Double> resultOfAllCandi){
        Print.p("Dir order: {");
        for(String s:actual_glycan_list){
            Print.p(s+",");
        }
        Print.pl("}");
        Print.p("resultOfAllCandidate: {");
        //DecimalFormat formatter = new DecimalFormat("#0.000");
        for(Double d: resultOfAllCandi){
            //Print.p(formatter.format(d)+",");
            Print.p(d.doubleValue()+",");
        }
        Print.pl("}");
    }
    
    public static double computeJudgeFunciton(ArrayList<Double> resultOfAllCandi){
        double ret = 1;
        for(Double d: resultOfAllCandi){
            ret *= d.doubleValue();
        }
        return ret;
    }
    
    public static ArrayList<Double> cloneList(ArrayList<Double> list) {
        ArrayList<Double> clone = new ArrayList<Double>(list.size());
        for (Double item : list) clone.add(Double.valueOf(item.doubleValue()));
        return clone;
    }
}
