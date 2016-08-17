package tools;

import java.io.*;
import java.util.*;

import org.eurocarbdb.MolecularFramework.io.CarbohydrateSequenceEncoding;
import org.eurocarbdb.MolecularFramework.io.SugarImporterFactory;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;

import spectrum.Peak;
import util.*;

public class BatchWork {
    
    /** 
     * <p>This constant is the searching window of Mass of parent iron.
     * This is used when searching candidate structures.
     */
    static final double SEARCHING_WINDOW = 2;

    /** The structure library. */
    static StructureLib strucLib;

    /** All spectra loaded form spectra files(input of this program). */
    ArrayList<SPComponent> spList = new ArrayList<SPComponent>();

    /** 
     * This boolean variable suggests whether candidate structures have been
     * selected by searching the structure library
     */
    boolean areCandidatesLoaded = false;
    
    Hashtable<String, double[]> probArrayHash = 
            new Hashtable<String, double[]>();
    Hashtable<String, ArrayList<FragNode>> candiStrucHash = 
            new Hashtable<String, ArrayList<FragNode>>();
    Hashtable<String, SPComponent> spComponentHash = 
            new Hashtable<String, SPComponent>();
    Hashtable<String, ArrayList<PeakEntropyInfo>> entropyListHash = 
            new Hashtable<String, ArrayList<PeakEntropyInfo>>();
    Hashtable<String, String[]> resultInfoArrayHash = 
            new Hashtable<String, String[]>();
    ArrayList<FragNode> candiFragNodeList;
    ArrayList<PeakEntropyInfo> candiPeakEntropyList = 
            new ArrayList<PeakEntropyInfo>();
    
    

    /**
     * <p>Load structure library file.
     *
     * @param strucFile path of the structure file
     * @return true, if the library is successfully loaded
     */
    public static boolean loadStrucLib(String strucFile) {
        SugarFragment candiGlycan = new SugarFragment();
        strucLib = candiGlycan.loadStrucLib(strucFile);
        return true;
    }
    // TODO: unfinished doc
    /**
     * <p>This function .
     *
     * @param spectrumFilePaths an array containing file paths of spectra 
     *        pending to be loaded
     * @param cutTime the cut time ????
     * @param WIN the win ????
     * @param filterRatio the ratio parameter of filter
     *        peaks of which intensity is blow () will be ignored
     * @param outFolder the out folder
     */
    public void batchWork(ArrayList<String> spectraFilePaths, int cutTime,
            double WIN, double filterRatio,String outFolder) {
        // load spectra files, spectra are stored in spList
        loadSpectra(spectraFilePaths);
        
        FragMz fragMz = new FragMz();
        // A array containing prior probabilities of candidate structures.
        // For MS2, it is currently uniform distribution
        // For MS3+, It is the posterior probabilities using previous spectra
        double[] preProbArray;
        for (SPComponent spectrum : spList) {
            ArrayList<FragNode> candiStrucList = new ArrayList<FragNode>();
            if (this.areCandidatesLoaded) {
                // If the candidate structures are already gotten 
                // by searching the structure library
                candiStrucList = this.candiFragNodeList;
                // candiStrucList=this.candiStrucHash.get(iterSP.getSpPreFileID());
            } else {
                // If the the candidate structures are not gotten, 
                // search the structure library by M/Z of the parent iron.
                // backup: iterSP.getPreMzList().get(0)
                //         gets the M/Z of the first precursor ion
                candiStrucList = this.searchLib(spectrum.getPreMzList().get(0));
                this.areCandidatesLoaded = true;
            }
            // If no candidate is found, it is an error
            if (candiStrucList == null) {
                System.out.println("No candidate structure of which mass"
                        + " is between " + spectrum.getPreMzList().get(0) 
                        + "+-" + SEARCHING_WINDOW);
                continue; // ignore this spectrum
            }
            
            // detect one cut or two cut, then search FragNode ??
            if(!checkSpectrum(spectrum)){
                continue;// ignore this spectrum
            }
            
            int candiNum = candiStrucList.size();
            if (spectrum.getSpLevel() == 2) {
                // If current spectrum is MS2, initialize the probability array
                // with uniform distribution over all candidate structures
                preProbArray = fragMz.getInitEqualProb(candiNum);
            } else {
                // If current spectrum is not MS2, get the probability array
                // by previous spectrum file ID
                preProbArray = probArrayHash.get(spectrum.getSpPreFileID());
                // Print current spectrum file ID and previous one
                System.out.println("current spectrum: "+spectrum.getSpFileID() 
                    + "\t previous spectrum: " + spectrum.getSpPreFileID());
            }
            
            ScoreEntropyResult tmpResult = fragMz.executeCount(candiStrucList,
                    spectrum, spectrum.getPreMzList(), cutTime,
                    spectrum.getSpLevel(), WIN, preProbArray, filterRatio);
    
            if (tmpResult == null) {
                continue;
            }
            
    
            ArrayList<CompareInfo> scoreInfoList = tmpResult.getScoreInfoList();
            String[] infoArray = new String[candiNum];
            double[] probArray = new double[candiNum];
    
            for (int i = 0; i < scoreInfoList.size(); i++) {
                CompareInfo tmp = scoreInfoList.get(i);
                if (tmp != null) {
                    infoArray[i] = scoreInfoList.get(i).getScoreInfoStr();
                    probArray[i] = scoreInfoList.get(i).getScore();
                } else {
                    infoArray[i] = "0.0";
                    probArray[i] = 0.0;
                }
            }
    
            resultInfoArrayHash.put(spectrum.getSpFileID(), infoArray);
            probArrayHash.put(spectrum.getSpFileID(), probArray);
            candiStrucHash.put(spectrum.getSpFileID(), candiStrucList);
            
            //count next stage peak entropy annotation entropyListHash for test
            ArrayList<PeakEntropyInfo> entropyList = 
                    tmpResult.getPeakEntropyList();
            if(entropyList!=null){
                this.candiPeakEntropyList.addAll(entropyList);
            }
            
            this.updateEntropy(probArray, candiPeakEntropyList);
            
            entropyListHash.put(spectrum.getSpFileID(),
                    this.candiPeakEntropyList);
            this.writeOut(spectrum, outFolder);
            spComponentHash.put(spectrum.getSpFileID(), spectrum);
            
        }
    }

    /**
     * <p>Load the spectrums(input of GIPS) from files 
     * (previous name: loadSPFile).
     * <p>spectra are stored in spList
     * @param spectrumPaths  an array containing file paths of spectrums 
     *        pending to load
     */
    private void loadSpectra(ArrayList<String> spectrumPaths) {
        try {
            
            for (String iterFile : spectrumPaths) {
                SPComponent tmp = new SPComponent(new File(iterFile));
                spList.add(tmp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Search structure library by the mass of parent ion
     *
     * @param searchMass the mass of parent ion
     * @return the list of fragments of which mass is between searchMass +- WIN
     */
    public ArrayList<FragNode> searchLib(double searchMass) {
        CarbohydrateSequenceEncoding encode = 
                CarbohydrateSequenceEncoding.carbbank;
        ArrayList<Double> indexList = BatchWork.strucLib.getMassIndexList();
        Hashtable<Double, ArrayList<String>> indexStrucHash = 
                BatchWork.strucLib.getCandiStrucHash();
        ArrayList<FragNode> reList = new ArrayList<FragNode>();
        try {
            for (Double iterIndex : indexList) {
                if (iterIndex.doubleValue() < searchMass + SEARCHING_WINDOW
                    && iterIndex.doubleValue() > searchMass - SEARCHING_WINDOW) {
                    ArrayList<String> strucStrList = indexStrucHash
                            .get(iterIndex);
                    int strucID = 1;
                    for (String iterStruc : strucStrList) {
                        Sugar su = SugarImporterFactory.importSugar(iterStruc,
                                encode);
                        ConvertSugar test = new ConvertSugar();
                        FragNode strucFragNode = test.convert(su);
                        strucFragNode.setStrucID(String.valueOf(++strucID));
                        reList.add(strucFragNode);
                    }
                }
            }
            this.candiFragNodeList = reList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return reList;
    }
    
    /**
     * <p>Check spectrum whether contains at least 2 peaks. 
     *    Then print spectrum file ID.
     * <p>This function is previously part of batchWork function.
     *
     * @param spectrum the spectrum pending to check
     * @return true, if this spectrum contains no errors; 
     *         false, if this spectrum contains errors
     */
    public boolean checkSpectrum(SPComponent spectrum){
        boolean noError = true;
        // gets all peaks from current spectrum
        Peak[] peaks = spectrum.getPeakArray(); // previous name expSP
        // Every spectrum should exist at least 2 peaks,
        // otherwise it is an error
        if (peaks == null || peaks.length < 2) {
            noError = false;
            System.out.println("spectrum is error:" + spectrum.getSpFileID());
        } else {
            noError = true;
            System.out.println("spectrum is ok:" + spectrum.getSpFileID());
        }
        return noError;
    }

    public void writeOut(SPComponent iterSP,String outFolder) {
        try {
            String spFileID = iterSP.getSpFileID();
            BufferedWriter outfile = new BufferedWriter(
                    new FileWriter(outFolder+spFileID+".txt"));

            ArrayList<PeakEntropyInfo> peakEntropyList = 
                        this.entropyListHash.get(spFileID);
                
            writeBuffer2(outfile, iterSP,peakEntropyList);
            outfile.flush();
            outfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    public void writeOut2(String outFile) {

        try {
            BufferedWriter outfile = 
                    new BufferedWriter(new FileWriter(outFile));

            for (SPComponent iterSP : spList) {
                String spFileID = iterSP.getSpFileID();

                ArrayList<PeakEntropyInfo> peakEntropyList = 
                        this.entropyListHash.get(iterSP.getSpFileID());
                if (this.resultInfoArrayHash.containsKey(spFileID)) {
                    writeBuffer2(outfile, iterSP,peakEntropyList);
                }
            }
            outfile.flush();
            outfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeBuffer2(BufferedWriter outfile, SPComponent spComponent,
            ArrayList<PeakEntropyInfo> peakEntropyList) {
        String[] infoArray = this.resultInfoArrayHash.get(spComponent
                .getSpFileID());
        
        try {
            
            outfile.write(spComponent.getSpFileID() + "\t"
                    + spComponent.getSpLevel());
            outfile.newLine();
            for (int i = 0; i < infoArray.length; i++) {
                if (infoArray[i] == null) {
                    outfile.write("null");

                }
                /*
                 * do not print the score =0 result info
                 */
                else if (!infoArray[i].equalsIgnoreCase("0.0")) {
                    outfile.write(infoArray[i]);
                } else {
                    continue;
                }

                outfile.newLine();
            }

            /*
             * max intens peak outprint
             */
            Peak maxIntensPeak = Spectrum.getMaxIntensPeak(spComponent
                    .getPeakArray());
            outfile.write("MaxIntensPeak:" + maxIntensPeak.getMz());
            outfile.newLine();

            if (peakEntropyList != null)
                for (int j = 0; j < peakEntropyList.size(); j++) {
                    PeakEntropyInfo tmpEntropy = peakEntropyList.get(j);
                    outfile.write(tmpEntropy.peak.getMz() + "\t" 
                            + tmpEntropy.peak.getIntensity()
                            + "\t" + tmpEntropy.candiIDList + "\t"
                            + tmpEntropy.peakEntropy+"\t"+tmpEntropy.msLevel);
                    
                    outfile.newLine();
                }

            outfile.newLine();
            outfile.flush();

        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> prescoreFilterIndex(double[] prescoreArray,
            int Top) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();

        double maxProb = 0.0;
        int maxID = 0;
        while (Top > 0) {
            for (int i = 0; i < prescoreArray.length; i++) {
                if (!indexList.contains(i) && prescoreArray[i] > maxProb) {
                    maxProb = prescoreArray[i];
                    maxID = i;
                }
            }
            indexList.add(maxID);

            for (int i = 0; i < prescoreArray.length; i++) {
                if (!indexList.contains(i) && prescoreArray[i] == maxProb) {
                    indexList.add(i);
                }
            }
            maxID = 0;
            maxProb = 0;
            Top--;
        }
        Collections.sort(indexList);
        return indexList;
    }

    public double[] prescoreFilter(double[] prescoreArray,
            ArrayList<Integer> indexList) {
        double[] reArray = new double[indexList.size()];
        int i = 0;
        for (Integer iterIndex : indexList) {
            reArray[i++] = prescoreArray[iterIndex];
        }
        return reArray;
    }

    public ArrayList<FragNode> candiStrucFilter(ArrayList<FragNode> candiStruc,
            ArrayList<Integer> indexList) {
        ArrayList<FragNode> reList = new ArrayList<FragNode>();
        for (Integer iterIndex : indexList) {
            reList.add(candiStruc.get(iterIndex));
        }
        return reList;
    }

    public void updateEntropy(double[] preProbArray,
            ArrayList<PeakEntropyInfo> candiPeakEntropyList){
        for(int i=0;i<candiPeakEntropyList.size();i++)
        {
            if(candiPeakEntropyList.get(i)!=null)
            {
                candiPeakEntropyList.get(i).updateEntroy(preProbArray);
            }
        }
    }
    
}