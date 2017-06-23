package util;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class PeakEntropy.
 */
public class PeakEntropy {

    /** The m/z of this peak. */
    public double mz;
    
    /** The intensity of this peak. */
    public double intensity;
    
    /** The entropy of this peak. */
    public double entropy;
    
    /** The structure id list. ?? */
    //public ArrayList<Integer> strucIdList; // why this is needed here ??

    /**
     * Instantiates a new peak entropy.
     *
     * @param mz the mz
     * @param intensity the intensity
     * @param strucIdList the struc id list
     * @param entropy the entropy
     */
    public PeakEntropy(double mz, double intensity, ArrayList<Integer> strucIdList, double entropy) {
        this.mz = mz;
        this.intensity = intensity;
        //this.strucIdList = strucIdList;
        this.entropy = entropy;
    }
}
