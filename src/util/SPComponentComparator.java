package util;

import java.util.Comparator;

/**
 * The Class SPComponentComparator.
 * This class is a comparator of spectra.
 * The spectrum with the smaller SP level is smaller.
 * If the SP levels of the two specta are the same. The spectrum with the 
 * smaller fileID is smaller. 
 */
public class SPComponentComparator implements Comparator<SPComponent> {

    @Override
    public int compare(SPComponent oA, SPComponent oB) {
//        SPComponent oA = (SPComponent) o1;
//        SPComponent oB = (SPComponent) o2;
        if (oA.getSpLevel() > oB.getSpLevel()) {
            return 1;
        } else if (oA.getSpLevel() < oB.getSpLevel()) {
            return -1;
        } else {
            return oA.getSpFileID().compareToIgnoreCase(oB.getSpFileID());
            //return 0;
        }

    }
}