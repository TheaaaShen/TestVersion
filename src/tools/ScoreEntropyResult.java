package tools;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ScoreEntropyResult.
 * This class represents the result of calculation of one spectrum.
 * It contains two parts. One is the distinguishing power of peaks.
 * Other is the scores of every candidate structure.
 */
public class ScoreEntropyResult {
    
    /** 
     * This list contains the distinguishing power(entropy) of every 
     * **peak**.
     */
    ArrayList<PeakEntropyInfo> peakEntropyList;
    
    /** 
     * This list contains the scores and matched peaks of every
     * **candidate structure**.
     */
    ArrayList<CompareInfo> scoreInfoList;

    /**
     * Instantiates a new score entropy result.
     *
     * @param scoreInfoList the score info list
     * @param peakEntropyList the peak entropy list
     */
    public ScoreEntropyResult(ArrayList<CompareInfo> scoreInfoList, ArrayList<PeakEntropyInfo> peakEntropyList) {
        this.peakEntropyList = peakEntropyList;
        this.scoreInfoList = scoreInfoList;
    }

    /**
     * Gets the peak entropy list.
     *
     * @return the peak entropy list
     */
    public ArrayList<PeakEntropyInfo> getPeakEntropyList() {
        return this.peakEntropyList;
    }

    /**
     * Gets the score info list.
     *
     * @return the score info list
     */
    public ArrayList<CompareInfo> getScoreInfoList() {
        return this.scoreInfoList;
    }
}
