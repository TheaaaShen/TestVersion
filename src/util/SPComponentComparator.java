package util;

import java.util.Comparator;

import main.Settings;

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
            int l = Math.min(oA.preMzList.size(), oB.preMzList.size());
            for(int i = 0; i < l;i++){
                if(Math.abs(oA.preMzList.get(i) - oB.preMzList.get(i)) 
                        > Settings.peak_matching_duration){
                    if(oA.preMzList.get(i) > oB.preMzList.get(i)){
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
            return oA.getSpFileID().compareToIgnoreCase(oB.getSpFileID());
            //return 0;
        }

    }
}