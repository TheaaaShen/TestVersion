package util;

import java.util.Comparator;

public class FragNodeMassComparator implements Comparator<FragNode>{

    @Override
    public int compare(FragNode o1, FragNode o2) {
        // TODO Auto-generated method stub
        if(o1.getSubtreeMass()>o2.getSubtreeMass())
        {
            return 1;
        }else if(o1.getSubtreeMass()<o2.getSubtreeMass())
        {
            return -1;
        }else
        {
            int state=o1.getStrucID().compareTo(o2.getStrucID());
            if(state>0)
            {
                return 1;
            }else if(state<0)
            {
                return -1;
            }else
            {
                return 0;
            }
            
            
        }
        
    }

}
