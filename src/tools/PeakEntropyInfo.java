package tools;

import java.util.ArrayList;

import spectrum.Peak;
import util.PeakEntropy;

public class PeakEntropyInfo {
    
    /** The peak corresponding to this class. It contains m/z and intensity.*/
    Peak peak;
    int msLevel;
    ArrayList<Integer> candiIDList; // why this is needed ?
    ArrayList<double[]> featureProbList; // what is  feature probability?
    double peakEntropy;
    ArrayList<PeakEntropy> entropyList;

    /** 
     * This variable suggests whether this peak is used. If a peak is used,
     * there exist a next level MS the m/z of whose precursor ion is peak.mz.
     */
    boolean isUsed;

    public PeakEntropyInfo(Peak peak) {
        this.peak = peak;
        int msLevel = 0; // wrong??
        this.peakEntropy = -1;
        this.featureProbList = new ArrayList<double[]>();
        this.candiIDList = new ArrayList<Integer>();
        this.entropyList = new ArrayList<PeakEntropy>();
        this.isUsed = false;
    }

    public void setMsLevel(int level) {
        this.msLevel = level;
    }

    public void setFeatureProbList(ArrayList<double[]> featureProbList) {
        this.featureProbList = featureProbList;
    }

    public void setCandiIDList(ArrayList<Integer> candiIDList) {
        this.candiIDList = candiIDList;
    }

    public void setEntropyList(ArrayList<PeakEntropy> entropyList) {
        this.entropyList = entropyList;
    }

    public void setEntropy(double peakEntropy) {
        this.peakEntropy = peakEntropy;
    }

    public double getEntropy() {
        return this.peakEntropy;
    }

    public void updateEntroy(double[] preProbArray) {
        this.peakEntropy = this.countExpectEntropy(this.arrayNormalize(
                featureProbList, candiIDList, preProbArray));
    }

    private ArrayList<double[]> arrayNormalize(ArrayList<double[]> probArrayList,
            ArrayList<Integer> candiIDList, double[] preProbArray) {
        ArrayList<double[]> reList = new ArrayList<double[]>();
        double[] preProbNeed = new double[candiIDList.size()];
        for(int m = 0; m < candiIDList.size(); m++) {
            preProbNeed[m] = preProbArray[candiIDList.get(m)];
        }

        for(int i = 0; i < probArrayList.size(); i++){
            double[] probArray = probArrayList.get(i);
            double[] tmpArray = new double[probArray.length];
            double sumProb = 0;
            for(int j = 0; j < probArray.length; j++){
                sumProb = sumProb + probArray[j] * preProbNeed[j];
            }
            for(int k = 0; k < probArray.length; k++){
                tmpArray[k] = probArray[k] * preProbNeed[k] / sumProb;
            }
            reList.add(tmpArray);
        }
        return reList;
    }

    private double countExpectEntropy(ArrayList<double[]> probArrayList) {
        double reEntropy = 0;
        for(double[] iterArray : probArrayList) {
            reEntropy = reEntropy + Entropy.countEntropy(iterArray);
        }

        reEntropy = reEntropy / probArrayList.size();
        return reEntropy;
    }

}
