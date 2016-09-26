package tools;

import java.util.ArrayList;

import spectrum.Peak;
import util.PeakEntropy;

public class PeakEntropyInfo {
    
    /** The peak corresponding to this class. It contains m/z and intensity.*/
    Peak peak;
    int msLevel;
    ArrayList<Integer> candiIDList; // why this is needed ?
    
    /**
     * <p>contains P(S_j|G_i)
     * <p>featureProbList: f[j][i]
     * <p>n: the number of candidate structures(maybe fragments for MS3+)
     * 
     * <p>One element double[] means the probability list that G_i(i in[1, m]) 
     * generates theoretical spectrum S_j
     * 
     * <p>m: the number of all possible theoretical spectra.
     * For a particular structure G' from G_i (1<=i<=n), if G' may generate 
     * this peak (represented by this class), generating next stage spectra 
     * s_k (1<=k<=m') of G'. m' may >= 1, since some peaks may not appear.
     * All these s_k of different G' form S_j. m = m' U m'' U m''' ....
     * 
     */
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
        this.msLevel = 0; 
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
