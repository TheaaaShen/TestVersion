package util;

import java.util.Comparator;

public class SPComponentComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        // TODO Auto-generated method stub
        SPComponent oA = (SPComponent) o1;
        SPComponent oB = (SPComponent) o2;
        if (oA.getSpLevel() > oB.getSpLevel()) {
            return 1;
        } else if (oA.getSpLevel() < oB.getSpLevel()) {
            return -1;
        } else {
            return 0;
        }

    }
}