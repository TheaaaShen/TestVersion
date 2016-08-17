package spectrum;

import java.util.Comparator;

/**
 * <p>This is a Comparator that uses M/Z of a peak.
 * This Comparator sorts peaks in ascending order, which means that the peak with 
 * the smallest M/Z is the smallest one.
 * <p>Previous name:DataPointMzComparator
 */
public class PeakMzComparator implements Comparator<Peak>{

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Peak a, Peak b) {
        if(a.getMz() > b.getMz()) {
            return 1; // a > b
        } else if (a.getMz()<b.getMz()) {
            return -1; // a < b
        } else {
            return 0; // a == b
        }
    }
    

}
