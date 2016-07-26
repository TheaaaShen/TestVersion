package tools;

import java.io.*;
import java.util.*;

import org.eurocarbdb.MolecularFramework.io.CarbohydrateSequenceEncoding;
import org.eurocarbdb.MolecularFramework.io.SugarImporterFactory;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;

import util.*;

public class BatchWork {
	// all spetras loaded form files
	ArrayList<SPComponent> spList = new ArrayList<SPComponent>();
	
	
	Hashtable<String, double[]> probArrayHash = new Hashtable<String, double[]>();
	Hashtable<String, ArrayList<FragNode>> candiStrucHash = new Hashtable<String, ArrayList<FragNode>>();
	Hashtable<String, SPComponent> spComponentHash = new Hashtable<String, SPComponent>();
	Hashtable<String, ArrayList<PeakEntropyInfo>> entropyListHash = new Hashtable<String, ArrayList<PeakEntropyInfo>>();
	Hashtable<String, String[]> resultInfoArrayHash = new Hashtable<String, String[]>();
	ArrayList<FragNode> candiFragNodeList;
	ArrayList<PeakEntropyInfo> candiPeakEntropyList=new ArrayList<PeakEntropyInfo>();
	boolean libLoaded = false;
	static StructureLib strucLib;

	/**
	 * Load the spectrums from files (previous name: loadSPFile)
	 * @param spectrumPaths  an array containing file paths of spectrums pending to load
	 */
	private void loadSpectraFile(ArrayList<String> spectrumPaths) {
		try {
			
			for (String iterFile : spectrumPaths) {
				SPComponent tmp = new SPComponent(new File(iterFile));
				spList.add(tmp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeOut(SPComponent iterSP,String outFolder) {

		try {
			String spFileID = iterSP.getSpFileID();
			BufferedWriter outfile = new BufferedWriter(new FileWriter(outFolder+spFileID+".txt"));

				ArrayList<PeakEntropyInfo> peakEntropyList = this.entropyListHash
						.get(spFileID);
				
					writeBuffer2(outfile, iterSP,peakEntropyList);
			
			outfile.flush();
			outfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void writeOut2(String outFile) {

		try {
			BufferedWriter outfile = new BufferedWriter(new FileWriter(outFile));

			for (SPComponent iterSP : spList) {
				String spFileID = iterSP.getSpFileID();

				ArrayList<PeakEntropyInfo> peakEntropyList = this.entropyListHash
						.get(iterSP.getSpFileID());
				if (this.resultInfoArrayHash.containsKey(spFileID)) {
					writeBuffer2(outfile, iterSP,peakEntropyList);
				}
			}
			outfile.flush();
			outfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void writeBuffer2(BufferedWriter outfile, SPComponent spComponent,ArrayList<PeakEntropyInfo> peakEntropyList) {
		String[] infoArray = this.resultInfoArrayHash.get(spComponent
				.getSpFileID());
		
		try {
			
			outfile.write(spComponent.getSpFileID() + "\t"
					+ spComponent.getSpLevel());
			outfile.newLine();
			for (int i = 0; i < infoArray.length; i++) {
				if (infoArray[i] == null) {
					outfile.write("null");

				}
				/*
				 * do not print the score =0 result info
				 */
				else if (!infoArray[i].equalsIgnoreCase("0.0")) {
					outfile.write(infoArray[i]);
				} else {
					continue;
				}

				outfile.newLine();
			}

			/*
			 * max intens peak outprint
			 */
			DataPoint maxIntensPeak = Spectrum.getMaxIntensPeak(spComponent
					.getPeakArray());
			outfile.write("MaxIntensPeak:" + maxIntensPeak.getMZ());
			outfile.newLine();

			if (peakEntropyList != null)
				for (int j = 0; j < peakEntropyList.size(); j++) {
					PeakEntropyInfo tmpEntropy = peakEntropyList.get(j);
					outfile.write(tmpEntropy.peak.getMZ() + "\t" + tmpEntropy.peak.getIntensity()
							+ "\t" + tmpEntropy.candiIDList + "\t"
							+ tmpEntropy.peakEntropy+"\t"+tmpEntropy.msLevel);
					
					outfile.newLine();
				}

			outfile.newLine();
			outfile.flush();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public ArrayList<FragNode> searchLib(double searchMass) {
		double WIN = 2;
		CarbohydrateSequenceEncoding encode = CarbohydrateSequenceEncoding.carbbank;
		ArrayList<Double> indexList = BatchWork.strucLib.getMassIndexList();
		Hashtable<Double, ArrayList<String>> indexStrucHash = BatchWork.strucLib
				.getCandiStrucHash();
		ArrayList<FragNode> reList = new ArrayList<FragNode>();
		try {
			for (Double iterIndex : indexList) {
				if (iterIndex.doubleValue() < searchMass + WIN
						&& iterIndex.doubleValue() > searchMass - WIN) {
					ArrayList<String> strucStrList = indexStrucHash
							.get(iterIndex);
					int strucID = 1;
					for (String iterStruc : strucStrList) {
						Sugar su = SugarImporterFactory.importSugar(iterStruc,
								encode);
						ConvertSugar test = new ConvertSugar();
						FragNode strucFragNode = test.convert(su);
						strucFragNode.setStrucID(String.valueOf(++strucID));
						reList.add(strucFragNode);
					}
				}
			}
			this.candiFragNodeList = reList;
			this.libLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reList;
	}

	public static boolean loadStrucLib(String strucFile) {
		SugarFragment candiGlycan = new SugarFragment();
		strucLib = candiGlycan.loadStrucLib(strucFile);
		return true;
	}

	public ArrayList<Integer> prescoreFilterIndex(double[] prescoreArray,
			int Top) {
		ArrayList<Integer> indexList = new ArrayList<Integer>();

		double maxProb = 0.0;
		int maxID = 0;
		while (Top > 0) {
			for (int i = 0; i < prescoreArray.length; i++) {
				if (!indexList.contains(i) && prescoreArray[i] > maxProb) {
					maxProb = prescoreArray[i];
					maxID = i;
				}
			}
			indexList.add(maxID);

			for (int i = 0; i < prescoreArray.length; i++) {
				if (!indexList.contains(i) && prescoreArray[i] == maxProb) {
					indexList.add(i);
				}
			}
			maxID = 0;
			maxProb = 0;
			Top--;
		}
		Collections.sort(indexList);
		return indexList;
	}

	public double[] prescoreFilter(double[] prescoreArray,
			ArrayList<Integer> indexList) {
		double[] reArray = new double[indexList.size()];
		int i = 0;
		for (Integer iterIndex : indexList) {
			reArray[i++] = prescoreArray[iterIndex];
		}
		return reArray;
	}

	public ArrayList<FragNode> candiStrucFilter(ArrayList<FragNode> candiStruc,
			ArrayList<Integer> indexList) {
		ArrayList<FragNode> reList = new ArrayList<FragNode>();
		for (Integer iterIndex : indexList) {
			reList.add(candiStruc.get(iterIndex));
		}
		return reList;
	}

    /**
     * 
     * @param spectrumFilePaths an array containing file paths of spectras pending to load
     * @param cutTime
     * @param WIN
     * @param filterRatio
     * @param outFolder
     */
	public void batchWork(ArrayList<String> spectrumFilePaths, int cutTime,
			double WIN, double filterRatio,String outFolder) {
		FragMz2 test = new FragMz2();
		loadSpectraFile(spectrumFilePaths);
		double[] preProbArray;
		for (SPComponent iterSP : spList) {
			ArrayList<FragNode> candiStrucList = new ArrayList<FragNode>();
			if (this.libLoaded) {
				candiStrucList = this.candiFragNodeList;
				// candiStrucList=this.candiStrucHash.get(iterSP.getSpPreFileID());

			} else {
				candiStrucList = this.searchLib(iterSP.getPreMzList().get(0));
			}

			if (candiStrucList == null) {
				System.out.println("deteee");
				continue;
			}
			

			/**
			 * detect one cut or two cut then search FragNode;
			 */
			DataPoint[] expSp = iterSP.getPeakArray();
			/*
			 * spectrum at least exist 2 peaks
			 */
			if (expSp == null || expSp.length < 2) {
				System.out.println("spectrum is error:" + iterSP.getSpFileID());
				continue;
			} else {
				System.out.println("spectrum is ok:" + iterSP.getSpFileID());
			}

			int candiNum = candiStrucList.size();
			if (iterSP.getSpLevel() == 2) {

				preProbArray = test.getInitEqualProb(candiNum);

			} else {
				preProbArray = probArrayHash.get(iterSP.getSpPreFileID());
				System.out.println(iterSP.getSpFileID() + "\t"
						+ iterSP.getSpPreFileID());
			}
			
			ScoreEntropyResult tmpResult = test.executeCount(candiStrucList,
					iterSP, iterSP.getPreMzList(), cutTime,
					iterSP.getSpLevel(), WIN, preProbArray, filterRatio);

			if (tmpResult == null) {
				continue;
			}
			

			ArrayList<CompareInfo> scoreInfoList = tmpResult.getScoreInfoList();
			String[] infoArray = new String[candiNum];
			double[] probArray = new double[candiNum];

			for (int i = 0; i < scoreInfoList.size(); i++) {
				CompareInfo tmp = scoreInfoList.get(i);
				if (tmp != null) {
					infoArray[i] = scoreInfoList.get(i).getScoreInfoStr();
					probArray[i] = scoreInfoList.get(i).getScore();
				} else {
					infoArray[i] = "0.0";
					probArray[i] = 0.0;
				}
			}

			resultInfoArrayHash.put(iterSP.getSpFileID(), infoArray);
			probArrayHash.put(iterSP.getSpFileID(), probArray);
			candiStrucHash.put(iterSP.getSpFileID(), candiStrucList);
			
			/*
			 * count next stage peak entropy annotation entropyListHash for test
			 */
			ArrayList<PeakEntropyInfo> entropyList=tmpResult.getPeakEntropyList();
			if(entropyList!=null)
			{
				this.candiPeakEntropyList.addAll(entropyList);
			}
			
			this.updateEntropy(probArray, candiPeakEntropyList);
			
			entropyListHash.put(iterSP.getSpFileID(),
					this.candiPeakEntropyList);
			this.writeOut(iterSP, outFolder);
			spComponentHash.put(iterSP.getSpFileID(), iterSP);
			
		}
	}

	public void updateEntropy(double[] preProbArray,ArrayList<PeakEntropyInfo> candiPeakEntropyList)
	{
		for(int i=0;i<candiPeakEntropyList.size();i++)
		{
			if(candiPeakEntropyList.get(i)!=null)
			{
				candiPeakEntropyList.get(i).updateEntroy(preProbArray);
			}
		}
	}
	
	/* test code
	 * 
	public static void main(String[] args) {
		
//		String strucLib = "E:/Glycan/Glycan3/lib/CandiStructureLib.txt";
//		String spFolder = "E:/Glycan/Glycan3/spectra/testbug/test1/";
//		String outFolder = "E:/Glycan/Glycan3/spectra/testbug/test1_result/";
		String strucLib="E:/Glycan/2015-06-11/lib/CandiStructureLib.txt";
		String spFolder="E:/Glycan/2015-06-11/mzxml2/2396/";
		String outFolder="E:/Glycan/2015-06-11/result/";
		int cutTime=3;
		double WIN=0.6;
		double filterRatio=0.01;
		
		BatchWork.loadStrucLib(strucLib);
		BatchWork test = new BatchWork();
//		test.batchWork(strucLib, spFolder, cutTime, WIN, filterRatio,outFolder);
		// test.writeOut(outFolder);
	}
	*/

}