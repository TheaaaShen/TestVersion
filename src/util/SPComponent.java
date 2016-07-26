package util;

import java.io.File;
import java.util.ArrayList;

public class SPComponent {
	String spFileID;
	String spPreFileID;
	int spLevel;
	ArrayList<Integer> cutTreePath;
	DataPoint[] peakArray;
	ArrayList<Double> preMzList;

	public SPComponent(File file) {
		peakArray = loadSp(file.getAbsolutePath());
		spFileID = file.getName().split("\\.")[0];
		String[] splits = spFileID.split("_");
		spLevel = splits.length + 1;
		preMzList = new ArrayList<Double>();
		if (spLevel > 2) {
			spPreFileID = spFileID.substring(0, spFileID.lastIndexOf("_"));
		}else
		{
			spPreFileID=null;
		}
		for (String iterMzStr : splits) {
			preMzList.add(Double.parseDouble(iterMzStr));
		}
	}

	public DataPoint[] loadSp(String spFile)
	{
		DataPoint[] expIonArray=null;
		try{
			MzXMLReader mzxmlReader=new MzXMLReader();
			mzxmlReader.init(spFile);
			expIonArray=mzxmlReader.get_peak_list();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return expIonArray;
	}
	public int getSpLevel() {
		return this.spLevel;
	}

	public DataPoint[] getPeakArray() {
		return this.peakArray;
	}

	public ArrayList<Double> getPreMzList() {
		return this.preMzList;
	}

	public String getSpPreFileID() {
		return this.spPreFileID;
	}

	public String getSpFileID() {
		return this.spFileID;
	}
}
