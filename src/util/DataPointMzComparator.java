package util;

import java.util.Comparator;

public class DataPointMzComparator implements Comparator<DataPoint>{

	@Override
	public int compare(DataPoint arg0, DataPoint arg1) {
		if(arg0.getMZ()>arg1.getMZ())
		{
			return 1;
		}else if(arg0.getMZ()<arg1.getMZ())
		{
			return -1;
		}else
		{
			return 0;
		}
		
		// TODO Auto-generated method stub
	}
	

}
