package tools;

import java.util.ArrayList;
import java.util.Hashtable;

import util.*;
public class ScoreModel {
	
	/**
	 * Score model:
	 * @P=0.7
	 * match peaks： 
	 * probScore=Math.pow(P, matchNum)*Math.pow(1-P, unionMatchedNum-matchNum);
	 * probScore=probScore/sumProbScore;
	 */
	public static ArrayList<CompareInfo> scoreA(ArrayList<ArrayList<FragNode>> candiSpList,DataPoint[] expSpData,double[] preScoreArray,double WIN)
	{
		double P=0.75;
		ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
		for(int i=0;i<candiSpList.size();i++)
		{
			ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
			if(theorySpPeakList==null)
			{
			    candiSpMatchList.add(null);	
			}else
			{
			ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch2(theorySpPeakList,expSpData,WIN);
		    CompareInfo matchResult=new CompareInfo();
		    matchResult.setCandiID(String.valueOf(i));
		    matchResult.setMatchPeakList(matchedPeakList);
		    
		    candiSpMatchList.add(matchResult);
			}
		}
		
		int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
		double unionScore=0;
		for(int j=0;j<candiSpMatchList.size();j++)
		{
			CompareInfo tmpInfo=candiSpMatchList.get(j);
			if(tmpInfo==null)
			{
				continue;
			}else
			{
			int matchNum=tmpInfo.getMatchPeakList().size();
			double probScore=Math.pow(P, matchNum)*Math.pow(1-P, unionMatchedNum-matchNum);
			String infoStr=matchNum+"\t"+unionMatchedNum+"\t"+expSpData.length+"\t"+probScore+"\t"+preScoreArray[j];
			
//			System.out.println(infoStr);
//			System.out.println(probScore+"\t"+preScoreArray[j]);
			probScore=probScore*preScoreArray[j];
		
			candiSpMatchList.get(j).setScore(probScore);
			candiSpMatchList.get(j).setMatchedNum(matchNum);
			candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
			candiSpMatchList.get(j).setScoreInfoStr(infoStr);
			unionScore=unionScore+probScore;
			
			}
		}
		for(int k=0;k<candiSpMatchList.size();k++)
		{
			if(candiSpMatchList.get(k)!=null)
			{
			double normalizedScore=candiSpMatchList.get(k).getScore()/unionScore;
			candiSpMatchList.get(k).setScore(normalizedScore);
//			System.out.println("Prob. :"+normalizedScore);
			candiSpMatchList.get(k).setScoreInfoStr(candiSpMatchList.get(k).candiStrucID+"\t"+normalizedScore+"\t"+candiSpMatchList.get(k).getScoreInfoStr());
			}
		}
		return candiSpMatchList;
		
	}
	
	/*
	 * 共同匹配峰的个数
	 */
	public static int getUnionMatchedPeakNum(ArrayList<CompareInfo> candiSpMatchList)
	{
		ArrayList<Double> unionList=new ArrayList<Double>();
		for(CompareInfo iterCandi:candiSpMatchList)
		{
			if(iterCandi!=null)
			{
				for(PeakInfo iterPeak:iterCandi.getMatchPeakList())
				{
					if(unionList.contains(iterPeak.getPeakMz()))
					{
						continue;
					}else
					{
						unionList.add(iterPeak.getPeakMz());
					}
				}
			}
		}
		return unionList.size();
	}
	
	/**
	 * 实验谱峰去match理论谱峰，不同片段对应的相同峰只作为label list保留
	 */
	public static ArrayList<PeakInfo> theroExpSpMatch(ArrayList<FragNode> theorSpList,DataPoint[] expSpData,double WIN)
	{
		ArrayList<PeakInfo> matchPeakList=new ArrayList<PeakInfo>();
		double sumIntens=0;
		for(DataPoint iterExpPeak:expSpData)
		{
			double expMz=iterExpPeak.getMZ();
			sumIntens=sumIntens+iterExpPeak.getIntensity();
			
			ArrayList<FragNode> candiFragNodeList=new ArrayList<FragNode>();
			for(FragNode iterTheor:theorSpList)
			{
				double theorMz=iterTheor.getSubtreeMass();
//				System.out.println("theor:"+theorMz);
				
				if(expMz<theorMz+WIN&&expMz>theorMz-WIN)
				{
					candiFragNodeList.add(iterTheor);
				}
			}
			if(candiFragNodeList.size()>0)
			{
				System.out.println("exp:"+expMz);
				PeakInfo matchPeak=new PeakInfo(expMz,iterExpPeak.getIntensity(),candiFragNodeList);
				matchPeakList.add(matchPeak);
			}
		}
		for(int i=0;i<matchPeakList.size();i++)
		{
			matchPeakList.get(i).setSumIntens(sumIntens);
		}
		System.out.println("matched & all:"+matchPeakList.size()+"\t"+theorSpList.size());
		return matchPeakList;
		
		
	}
	public static ArrayList<PeakInfo> getTheorPeakInfoList(ArrayList<FragNode> theorSpList)
	{
		ArrayList<PeakInfo> reInfoList=new ArrayList<PeakInfo>();
		Hashtable<Double,ArrayList<FragNode>> peakHash=new Hashtable<Double,ArrayList<FragNode>>();
		ArrayList<Double> mzList=new ArrayList<Double>();
		for(FragNode iterNode:theorSpList)
		{
			double iterMass=iterNode.getSubtreeMass();
			if(mzList.contains(iterMass))
			{
				peakHash.get(iterMass).add(iterNode);
			}else
			{
				mzList.add(iterMass);
				ArrayList<FragNode> valueList=new ArrayList<FragNode>();
				valueList.add(iterNode);
				peakHash.put(iterMass, valueList);
			}
		}
		for(Double iterMz:mzList)
		{
			PeakInfo tmpInfo=new PeakInfo(iterMz,peakHash.get(iterMz));
			reInfoList.add(tmpInfo);
		}
		return reInfoList;
	}
	public static ArrayList<PeakInfo> theroExpSpMatch2(ArrayList<FragNode> theorSpList,DataPoint[] expSpData,double WIN)
	{
//		theorSpList=DataFilter.filterTheorySpSameMZ2(theorSpList);
		theorSpList=DataFilter.filterTheorySpSameMassIon(theorSpList);
		ArrayList<PeakInfo> theorPeakInfoList=getTheorPeakInfoList(theorSpList);
		ArrayList<PeakInfo> matchPeakList=new ArrayList<PeakInfo>();
		ArrayList<Double> mzList=new ArrayList<Double>();
		for(PeakInfo iterInfo:theorPeakInfoList)
		{
			double theorMz=iterInfo.getPeakMz();
//			System.out.println(theorMz+"\t"+iterTheor.getIonTypeNote()+"\t"+iterTheor.getStrucID());
		    double tmpMz=0;
			double tmpIntens=0;
			for(DataPoint iterExpPeak:expSpData)
			{
				double expMz=iterExpPeak.getMZ();
				
				if(Math.abs(expMz-theorMz)<WIN)
				{
					if(iterExpPeak.getIntensity()>tmpIntens)
					{
						tmpIntens=iterExpPeak.getIntensity();
						tmpMz=expMz;
					}
				}else if(expMz-theorMz>WIN)
				{
					break;
				}
				
			}
			if(!mzList.contains(tmpMz)&&tmpMz>10.0)
			{
			mzList.add(tmpMz);
			iterInfo.setPeakMz(tmpMz);
			iterInfo.setPeakIntens(tmpIntens);
			matchPeakList.add(iterInfo);
			}
			
		}
		
		System.out.println("matched & all:"+matchPeakList.size()+"\t"+theorSpList.size());
		return matchPeakList;
				
	}
	
	/**
	 * 实验谱峰去match理论谱峰,保留理论谱中不同片段对应的相同峰
	 */
	public static ArrayList<PeakInfo> theroExpSpMatchB(ArrayList<FragNode> theorSpList,DataPoint[] expSpData,double WIN)
	{
		ArrayList<PeakInfo> matchPeakList=new ArrayList<PeakInfo>();
		for(DataPoint iterExpPeak:expSpData)
		{
			double expMz=iterExpPeak.getMZ();
			
//			System.out.println("exp:"+expMz);
			
			for(FragNode iterTheor:theorSpList)
			{
				ArrayList<FragNode> candiFragNodeList=new ArrayList<FragNode>();
				double theorMz=iterTheor.getSubtreeMass();
//				System.out.println("theor:"+theorMz);
				if(expMz<theorMz+WIN&&expMz>theorMz-WIN)
				{
					candiFragNodeList.add(iterTheor);
					PeakInfo matchPeak=new PeakInfo(expMz,iterExpPeak.getIntensity(),candiFragNodeList);
					matchPeakList.add(matchPeak);
					
				}
			}
		}
		System.out.println("matched & all:"+matchPeakList.size()+"\t"+theorSpList.size());
		return matchPeakList;
	}
	/**
	 * Score model B:
	 * @P=0.7
	 * probScore=Math.pow(P, matchNum)*Math.pow(1-P, unionMatchedNum-matchNum);
	 * probScore=probScore/sumProbScore;
	 */
	public static ArrayList<CompareInfo> scoreB(ArrayList<ArrayList<FragNode>> candiSpList,DataPoint[] expSpData,double[] preScoreArray,double WIN)
	{
		double P=0.7;
		ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
		double sumScore=0;
		for(int i=0;i<candiSpList.size();i++)
		{
			ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(candiSpList.get(i),expSpData,WIN);
		    CompareInfo matchResult=new CompareInfo();
		    int matchNum=matchedPeakList.size();
		    int misMatchNum=candiSpList.get(i).size()-matchNum;
		    double score=Math.pow(P, matchNum)*Math.pow(1-P, misMatchNum);
		    sumScore=sumScore+score;
		    
		    matchResult.setCandiID(String.valueOf(i));
		    matchResult.setMatchPeakList(matchedPeakList);
		    matchResult.setScore(score);
		    matchResult.setMatchedNum(matchedPeakList.size());
		    candiSpMatchList.add(matchResult);
		    
		}
		
		for(int k=0;k<candiSpMatchList.size();k++)
		{
			double probScore=candiSpMatchList.get(k).getScore()/sumScore;
			candiSpMatchList.get(k).setScore(probScore);
			System.out.println("Prob. :"+probScore);
		}
		return candiSpMatchList;
		
	}
	
	/**
	 * Score Model C:
	 * @P=0.7
	 * probScore=Math.pow(P, matchNum)*Math.pow(1-P, missMatchNum);
	 * probScore=probScore/sumProbScore;
	 * 
	 */
	
	public static ArrayList<CompareInfo> scoreC(ArrayList<ArrayList<FragNode>> candiSpList,DataPoint[] expSpData,double[] preScoreArray,double WIN)
	{
		double P=0.7;
		ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
		double sumScore=0;
		for(int i=0;i<candiSpList.size();i++)
		{
			ArrayList<PeakInfo> matchedPeakList=theroExpSpMatchB(candiSpList.get(i),expSpData,WIN);
		    CompareInfo matchResult=new CompareInfo();
		    int matchNum=matchedPeakList.size();
		    int misMatchNum=candiSpList.get(i).size()-matchNum;
		    double score=Math.pow(P, matchNum)*Math.pow(1-P, misMatchNum);
		    sumScore=sumScore+score;
		    
		    matchResult.setCandiID(String.valueOf(i));
		    matchResult.setMatchPeakList(matchedPeakList);
		    matchResult.setScore(score);
		    matchResult.setMatchedNum(matchedPeakList.size());
		    candiSpMatchList.add(matchResult);
		    
		}
		
		for(int k=0;k<candiSpMatchList.size();k++)
		{
			double probScore=candiSpMatchList.get(k).getScore()/sumScore;
			candiSpMatchList.get(k).setScore(probScore);
			System.out.println("Prob. :"+probScore);
		}
		return candiSpMatchList;
		
	}
	/**
	 * countEntropy for probability
	 */
	public static double coutEntropy(ArrayList<CompareInfo> probInfoList)
	{
		ArrayList<Double> probScoreList=new ArrayList<Double>();
		for(int i=0;i<probInfoList.size();i++)
		{
			CompareInfo tmpInfo=probInfoList.get(i);
			if(tmpInfo==null)
			{
				probScoreList.add(0.0);
			}else{
				/*
				 * modify different score
				 */
//				probScoreList.add(tmpInfo.probScore);
				probScoreList.add(tmpInfo.getScore());
			}
		}
		return Entropy.countEntropy(probScoreList);
	}

	/**
	 * scoreD:
	 * matched peak sum intens ratio
	 * 
	 */
	public static ArrayList<CompareInfo> scoreD(ArrayList<ArrayList<FragNode>> candiSpList,DataPoint[] expSpData,double[] preScoreArray,double WIN)
	{
		ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
		double sumScore=0;
		for(int i=0;i<candiSpList.size();i++)
		{
			ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(candiSpList.get(i),expSpData,WIN);
		    CompareInfo matchResult=new CompareInfo();
		    int matchNum=matchedPeakList.size();
//		    int missMatchNum=candiSpList.get(i).size()-matchNum;
		    
		    double score=ScoreModel.coutSumMatchIntensRatio(matchedPeakList);
		    sumScore=sumScore+score;
		    
		    
		    matchResult.setCandiID(String.valueOf(i));
		    matchResult.setMatchPeakList(matchedPeakList);
		    matchResult.setScore(score);
		    matchResult.setMatchedNum(matchedPeakList.size());
		    candiSpMatchList.add(matchResult);
		    
		}
		
		for(int k=0;k<candiSpMatchList.size();k++)
		{
			double probScore=candiSpMatchList.get(k).getScore()/sumScore;
			candiSpMatchList.get(k).setScore(probScore);
			System.out.println("Prob. :"+probScore);
		}
		return candiSpMatchList;
		
	}
	
	/**
	 * Score Model E:
	 * @P=0.7
	 * probScore=Math.pow(P, matchNum)*Math.pow(1-P, missMatchNum)*Math.pow(P_Noise,unionMatchedNum-matchNum);
	 * probScore=probScore/sumProbScore;
	 * 
	 */
	public static ArrayList<CompareInfo> scoreE(ArrayList<ArrayList<FragNode>> candiSpList,DataPoint[] expSpData,double[] preScoreArray,double WIN)
	{
		double P=0.65;
		double P_Noise=0.1;
		ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
		for(int i=0;i<candiSpList.size();i++)
		{
			ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
			if(theorySpPeakList==null)
			{
			    candiSpMatchList.add(null);	
			}else
			{
			ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(theorySpPeakList,expSpData,WIN);
		    CompareInfo matchResult=new CompareInfo();
		    matchResult.setCandiID(String.valueOf(i));
		    matchResult.setMatchPeakList(matchedPeakList);
		    candiSpMatchList.add(matchResult);
			}
		}
		
		
		int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
		double unionScore=0;
		for(int j=0;j<candiSpMatchList.size();j++)
		{
			CompareInfo tmpInfo=candiSpMatchList.get(j);
			if(tmpInfo==null)
			{
				continue;
			}else
			{
				int matchNum=tmpInfo.getMatchPeakList().size();
				int peakNum=DataFilter.filterTheorySpSameMZ(candiSpList.get(j)).size();
				int misMatchNum=peakNum-matchNum;
				double probScore=Math.pow(P, matchNum)*Math.pow(1-P, misMatchNum)*Math.pow(P_Noise, unionMatchedNum-matchNum);
				System.out.println(matchNum+"\t"+peakNum+"\t"+unionMatchedNum);
				System.out.println(probScore+"\t"+preScoreArray[j]);
				probScore=probScore*preScoreArray[j];
			
				candiSpMatchList.get(j).setScore(probScore);
				candiSpMatchList.get(j).setMatchedNum(matchNum);
				candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
				
				unionScore=unionScore+probScore;
			}
			
			
		}
		for(int k=0;k<candiSpMatchList.size();k++)
		{
			if(candiSpMatchList.get(k)!=null)
			{
				double normalizedScore=candiSpMatchList.get(k).getScore()/unionScore;
				candiSpMatchList.get(k).setScore(normalizedScore);
				System.out.println("Prob. :"+normalizedScore);
			}else
			{
				System.out.println("Prob. :"+0.0);
			}
			
		}
		return candiSpMatchList;
		
	}
	/**
	 * Score Model F:
	 * @P=0.65
	 * @M=same m/z labels' Num
	 * P_Enhance=1-(1-P)^M
	 * probScore={P_Enhance...}*Math.pow(1-P, missMatchNum)*Math.pow(P_Noise,unionMatchedNum-matchNum);
	 * probScore=probScore/sumProbScore;
	 * 
	 */
	public static ArrayList<CompareInfo> scoreF(ArrayList<ArrayList<FragNode>> candiSpList,DataPoint[] expSpData,double[] preScoreArray,double WIN)
	{
		double P=0.65;
		double P_Noise=0.1;
		ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
		for(int i=0;i<candiSpList.size();i++)
		{
			ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
			if(theorySpPeakList==null)
			{
			    candiSpMatchList.add(null);	
			}else
			{
			ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(theorySpPeakList,expSpData,WIN);
		    CompareInfo matchResult=new CompareInfo();
		    matchResult.setCandiID(String.valueOf(i));
		    matchResult.setMatchPeakList(matchedPeakList);
		    candiSpMatchList.add(matchResult);
			}
		}
		
		int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
		double unionScore=0;
		for(int j=0;j<candiSpMatchList.size();j++)
		{
			CompareInfo tmpInfo=candiSpMatchList.get(j);
			if(tmpInfo==null)
			{
				continue;
			}else
			{
				int matchNum=tmpInfo.getMatchPeakList().size();
				double P_Inhence=1;
				for(int i=0;i<matchNum;i++)
				{
					PeakInfo matchInfo=tmpInfo.getMatchPeakList().get(i);
					int candiLabelNum=matchInfo.getCandiFragNodeList().size();
					P_Inhence=P_Inhence*(1-Math.pow((1-P),candiLabelNum));
				}
				
				
				int peakNum=DataFilter.filterTheorySpSameMZ(candiSpList.get(j)).size();
				int misMatchNum=peakNum-matchNum;
				double probScore=P_Inhence*Math.pow(1-P, misMatchNum)*Math.pow(P_Noise, unionMatchedNum-matchNum);
				System.out.println(matchNum+"\t"+peakNum+"\t"+unionMatchedNum);
				System.out.println(probScore+"\t"+preScoreArray[j]);
				probScore=probScore*preScoreArray[j];
			
				candiSpMatchList.get(j).setScore(probScore);
				candiSpMatchList.get(j).setMatchedNum(matchNum);
				candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
				
				unionScore=unionScore+probScore;
			}
			
			
		}
		for(int k=0;k<candiSpMatchList.size();k++)
		{
			if(candiSpMatchList.get(k)!=null)
			{
				double normalizedScore=candiSpMatchList.get(k).getScore()/unionScore;
				candiSpMatchList.get(k).setScore(normalizedScore);
				System.out.println("Prob. :"+normalizedScore);
			}else
			{
				System.out.println("Prob. :"+0.0);
			}
			
		}
		return candiSpMatchList;
		
	}
	/**
	 * Score Model F:
	 * @P=0.65
	 * @M=same m/z labels' Num
	 * P_Enhance=1-(1-P)^M
	 * probScore={P_Enhance...}*Math.pow(1-P, missMatchNum)*Math.pow(P_Noise,unionMatchedNum-matchNum);
	 * probScore=probScore/sumProbScore;
	 * 
	 */
	public static ArrayList<CompareInfo> scoreG(ArrayList<ArrayList<FragNode>> candiSpList,DataPoint[] expSpData,double[] preScoreArray,double WIN)
	{
		double P=0.65;
		double P_Noise=0.5;
		int minMissNum=Integer.MAX_VALUE;

		ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
		for(int i=0;i<candiSpList.size();i++)
		{
			ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
			if(theorySpPeakList==null)
			{
			    candiSpMatchList.add(null);	
			}else
			{
			ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(theorySpPeakList,expSpData,WIN);
		    CompareInfo matchResult=new CompareInfo();
		    matchResult.setCandiID(String.valueOf(i));
		    matchResult.setMatchPeakList(matchedPeakList);
		    candiSpMatchList.add(matchResult);
			}
		}
		
		int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
		double unionScore=0;
		int labelNum=0;
		for(int j=0;j<candiSpMatchList.size();j++)
		{
			CompareInfo tmpInfo=candiSpMatchList.get(j);
			if(tmpInfo==null)
			{
				continue;
			}else
			{
				int matchNum=tmpInfo.getMatchPeakList().size();
				double P_Inhence=1;
				for(int i=0;i<matchNum;i++)
				{
					PeakInfo matchInfo=tmpInfo.getMatchPeakList().get(i);
					int candiLabelNum=matchInfo.getCandiFragNodeList().size();
					labelNum=labelNum+candiLabelNum;
					P_Inhence=P_Inhence*(1-Math.pow((1-P),candiLabelNum));
				}
				
				/*
				 * peakNum different from scoreF
				 */
				int peakNum=candiSpList.get(j).size();
				int misMatchNum=peakNum-matchNum;
				double initScore=P_Inhence*Math.pow(P_Noise, unionMatchedNum-matchNum);
				String infoStr=matchNum+"\t"+labelNum+"\t"+peakNum+"\t"+unionMatchedNum+"\t"+initScore+"\t"+preScoreArray[j];
				
				System.out.println(infoStr);
				initScore=initScore*preScoreArray[j];
			
				if(minMissNum>misMatchNum)
				{
					minMissNum=misMatchNum;
				}
				candiSpMatchList.get(j).setScore(initScore);
				candiSpMatchList.get(j).setMatchedNum(matchNum);
				candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
				candiSpMatchList.get(j).setMissMatchedNum(misMatchNum);
				candiSpMatchList.get(j).setScoreInfoStr(infoStr);
			}
			
			
		}
		for(int k=0;k<candiSpMatchList.size();k++)
		{
			if(candiSpMatchList.get(k)!=null)
			{
				double probScore=candiSpMatchList.get(k).getScore()*Math.pow(1-P, candiSpMatchList.get(k).getMissMatchedNum()-minMissNum);
				candiSpMatchList.get(k).setScore(probScore);
				unionScore=unionScore+probScore;
			}else
			{
				
			}
			
		}
		for(int k=0;k<candiSpMatchList.size();k++)
		{
			if(candiSpMatchList.get(k)!=null)
			{
				double normalizedScore=candiSpMatchList.get(k).getScore()/unionScore;
				candiSpMatchList.get(k).setScore(normalizedScore);
				System.out.println("Prob. :"+normalizedScore);
			}else
			{
				System.out.println("Prob. :"+0.0);
			}
			
		}
		return candiSpMatchList;
		
	}
	public static ArrayList<CompareInfo> scoreH(ArrayList<ArrayList<FragNode>> candiSpList,DataPoint[] expSpData,double[] preScoreArray,double WIN)
	{
		ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
		for(int i=0;i<candiSpList.size();i++)
		{
			ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
			if(theorySpPeakList==null)
			{
			    candiSpMatchList.add(null);	
			}else
			{
			ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(theorySpPeakList,expSpData,WIN);
		    CompareInfo matchResult=new CompareInfo();
		    matchResult.setCandiID(String.valueOf(i));
		    matchResult.setMatchPeakList(matchedPeakList);
		    candiSpMatchList.add(matchResult);
		    
			}
		}
		
		int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
		double unionScore=0;
		for(int j=0;j<candiSpMatchList.size();j++)
		{
			CompareInfo tmpInfo=candiSpMatchList.get(j);
			if(tmpInfo==null)
			{
				continue;
			}else
			{
				int matchNum=tmpInfo.getMatchPeakList().size();
				
				int peakNum=candiSpList.get(j).size();
				int misMatchNum=peakNum-matchNum;
		
				
				candiSpMatchList.get(j).setMatchedNum(matchNum);
				candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
			}
		}
		
		return candiSpMatchList;
		
	}
	public static double coutSumMatchIntensRatio(ArrayList<PeakInfo> matchPeakList)
	{
		double sumIntens=0;
		for(int i=0;i<matchPeakList.size();i++)
		{
			sumIntens=sumIntens+matchPeakList.get(i).getPeakIntens();
		}
		return sumIntens/matchPeakList.get(0).getSumIntens();
	}
	
	public static void main(String[] args)
	{
		
	}

}
