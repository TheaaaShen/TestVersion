package util;

import java.util.Comparator;

public class PeakEntropyComparator implements Comparator{

	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		PeakEntropy oA=(PeakEntropy)arg0;
		PeakEntropy oB=(PeakEntropy)arg1;
		if(oA.intensity>oB.intensity)
		{
			return -1;
		}else if(oA.intensity<oB.intensity)
		{
			return 1;
		}else
		{
			return 0;
		}
		
	}

}
