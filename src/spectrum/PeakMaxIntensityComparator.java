package spectrum;

import java.util.Comparator;

/**
 * <p>This is a Comparator that uses Intensity of a peak.
 * This Comparator sorts peaks in descending order, which means that the peak
 * with the highest intensity is the smallest one.
 * <p>Previous Name: DataPointMaxIntensityComparator
 */
public class PeakMaxIntensityComparator implements Comparator<Peak>{

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * 
     */
    @Override
    public int compare(Peak a, Peak b) {
        if(a.getIntensity() > b.getIntensity()) {
            return -1; // a < b
        } else if(a.getIntensity() < b.getIntensity()) {
            return 1; // a > b
        } else {
            return 0; // a == b
        }
    }
    
}
