package tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import debug.MyTimer;
import spectrum.Peak;
import spectrum.PeakMaxIntensityComparator;
import spectrum.PeakMzComparator;
import util.*;

public class FetchFeature6 {
    double P = 0.75;
    double P_Noise = 0.1;

    public ArrayList<PeakEntropyInfo> FetchMatchMzPeak2(ArrayList<CompareInfo> candiCompareInfoList, int cutTime,
            int peakLevel) {
        /*
         * preProbScore init
         */
        int candiStrucNum = candiCompareInfoList.size();
        double[] preProbArray = new double[candiStrucNum];

        Hashtable<Peak, ArrayList<Integer>> peakIDHash = 
                new Hashtable<Peak, ArrayList<Integer>>();

        for(int i = 0; i < candiStrucNum; i++) {
            if(candiCompareInfoList.get(i) == null) {
                preProbArray[i] = 0;
            } else {
                preProbArray[i] = candiCompareInfoList.get(i).getScore();

            }
        }

        /*
         * for each experiment peak detect if contains in the candidate sub
         * glycan structures j can be see an the StrucID
         */
        ArrayList<Peak> subCandiPeakList = new ArrayList<Peak>();
        /*
         * each union matched exp peak
         */
        int topIntensNums = 5;

        Peak[] experSp = this.getUnionMatchedPeakArray(candiCompareInfoList, topIntensNums);

        for(int i = 0; i < experSp.length; i++) {
            Peak iterExperiPeak = experSp[i];
            if(iterExperiPeak == null) {
                continue;
            }
            int Detecter = 0;
            ArrayList<Integer> candiIDList = new ArrayList<Integer>();
            /*
             * each candi_structure matchPeakList
             */
            for(int j = 0; j < candiStrucNum; j++) {
                if(candiCompareInfoList.get(j) == null) {
                    continue;
                } else {
                    ArrayList<PeakInfo> tmpMatchPeakList = candiCompareInfoList.get(j).getMatchPeakList();
                    for(PeakInfo iterPeak : tmpMatchPeakList) {
                        if(iterPeak.getPeakMz() == iterExperiPeak.getMz()) {
                            Detecter++;
                            candiIDList.add(j);
                            break;
                        }
                    }

                }

            }
            
            /*
             * common_matched_peak search more than one candi_struc matched this
             * exp_peak
             */
            if(Detecter > 1) {
                peakIDHash.put(iterExperiPeak, candiIDList);
                subCandiPeakList.add(iterExperiPeak);
            }
        }
        
        MyTimer.showTime("In FetchFeature6:\t before count entropy");
        /*
         * count entropy
         * 
         * @subCandiiPeakList contains all candidate peaks for next stage
         * spectrum
         */
        double eEntropy = 0;
        ArrayList<PeakEntropyInfo> peakEntropyInfoList = new ArrayList<PeakEntropyInfo>();
        for(Peak iterCommPeak : subCandiPeakList) {
            ArrayList<Integer> candiIDList = peakIDHash.get(iterCommPeak);
            /*
             * for each candidate peak count the average entropy
             */
            MyTimer.showTime("In FetchFeature6:\t before countProbScore");
            PeakEntropyInfo peakEntropyInfo = this.countProbScore(iterCommPeak, candiIDList, candiCompareInfoList,
                    preProbArray, cutTime);
            MyTimer.showTime("In FetchFeature6:\t after countProbScore");
            /*
             * eEntropy=this.countExpectEntropy(probScoreArrayList); double
             * peakMz=FormatNum.DoubleFormat(iterCommPeak.getMZ(),3); double
             * peakIntens=FormatNum.DoubleFormat(iterCommPeak.getIntensity(),3);
             * double peakEntropy=FormatNum.DoubleFormat(eEntropy,3);
             * System.out.println(peakMz+"\t"+ peakIntens+"\t"+
             * candiIDList+"\t"+peakEntropy); PeakEntropy peakEntropyInfo=new
             * PeakEntropy(peakMz,peakIntens,candiIDList,peakEntropy);
             */

            peakEntropyInfo.setMsLevel(peakLevel);
            peakEntropyInfoList.add(peakEntropyInfo);
        }

        return peakEntropyInfoList;

    }

    public ArrayList<Integer> prescoreFilterIndex(double[] prescoreArray, int Top) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();

        double maxProb = 0.0;
        int maxID = 0;
        int candiNum = Top;
        while (candiNum > 0) {
            for(int i = 0; i < prescoreArray.length; i++) {
                if(!indexList.contains(i) && prescoreArray[i] > maxProb) {
                    maxProb = prescoreArray[i];
                    maxID = i;
                }
            }

            indexList.add(maxID);

            for(int i = 0; i < prescoreArray.length; i++) {
                if(!indexList.contains(i) && prescoreArray[i] == maxProb) {
                    indexList.add(i);
                }
            }
            if(indexList.size() >= Top) {
                break;
            }
            maxID = 0;
            maxProb = 0;
            candiNum--;
        }
        Collections.sort(indexList);
        return indexList;
    }

    public double[] prescoreFilter(double[] prescoreArray, ArrayList<Integer> indexList) {
        double[] reArray = new double[indexList.size()];
        int i = 0;
        for(Integer iterIndex : indexList) {
            reArray[i++] = prescoreArray[iterIndex];
        }
        return reArray;
    }

    public ArrayList<CompareInfo> candiStrucFilter(ArrayList<CompareInfo> candiStruc, ArrayList<Integer> indexList) {
        ArrayList<CompareInfo> reList = new ArrayList<CompareInfo>();
        for(Integer iterIndex : indexList) {
            reList.add(candiStruc.get(iterIndex));
        }
        return reList;
    }

    public Peak[] getUnionMatchedPeakArray(ArrayList<CompareInfo> candiCompareInfoList, int topIntensNums) {
        ArrayList<Peak> peakList = new ArrayList<Peak>();
        ArrayList<Double> mzList = new ArrayList<Double>();
        for(int i = 0; i < candiCompareInfoList.size(); i++) {
            CompareInfo tmpCandiInfo = candiCompareInfoList.get(i);
            if(tmpCandiInfo != null) {
                ArrayList<PeakInfo> tmpPeakInfoList = tmpCandiInfo.getMatchPeakList();
                for(int j = 0; j < tmpPeakInfoList.size(); j++) {
                    Peak tmpPeak = new Peak(tmpPeakInfoList.get(j).getPeakMz(), tmpPeakInfoList.get(j).getPeakIntens());
                    if(!mzList.contains(tmpPeak.getMz())) {
                        peakList.add(tmpPeak);
                        mzList.add(tmpPeak.getMz());
                    }

                }
            }

        }
        int intensTop = topIntensNums;// 5 or 10
        Collections.sort(peakList, new PeakMaxIntensityComparator());
        List<Peak> topIntensPeakList;
        if(peakList.size() > intensTop) {
            topIntensPeakList = peakList.subList(0, intensTop);
        } else {
            topIntensPeakList = peakList;
        }

        Collections.sort(topIntensPeakList, new PeakMzComparator());

        Peak[] reArray = new Peak[intensTop];

        topIntensPeakList.toArray(reArray);
        return reArray;

    }

    /*
     * input: a experiment peak, candidate structure, pre_probability, structure
     * cut time output: all possible spectrum correspond candidate structure
     * probability score
     */
    public PeakEntropyInfo countProbScore(Peak comPeak, ArrayList<Integer> candiIDList,
            ArrayList<CompareInfo> candiCompareInfoList, double[] preProbArray, int cutTime) {
        ArrayList<ArrayList<Double>> candiSubPeakList = new ArrayList<ArrayList<Double>>();

        double WIN = 0.5;
        int commNum = 0;
        for(Integer iterID : candiIDList) {
            CompareInfo tmpCandiInfo = candiCompareInfoList.get(iterID);
            ArrayList<Double> subSubPeakList = this.getSubSpMzList(tmpCandiInfo, comPeak, cutTime);

            candiSubPeakList.add(subSubPeakList);
        }

        ArrayList<Double> commPeakList = DataFilter.getCommonPeak(candiSubPeakList);

        /*
         * delete the common peaks from the sub candidate structure peakList;
         */
        MyTimer.showTime("In FetchFeature6:\t before delete the common peaks");
        if(commPeakList.size() > 0) {
            DataFilter.deleteCommonPeak(candiSubPeakList, commPeakList);
        }

        ArrayList<double[]> probArrayList = new ArrayList<double[]>();
        /*
         * each candidate
         */
        MyTimer.showTime("In FetchFeature6:\t candiSubPeakList.size()"+candiSubPeakList.size());
        for(int i = 0; i < candiSubPeakList.size(); i++) {

            ArrayList<Double> featureList = candiSubPeakList.get(i);

            /*
             * each feature combination
             */
            for(ArrayList<Double> iterList : this.countCombination(featureList)) {
                double[] probArray = new double[candiIDList.size()];
                int peakNum = commNum + featureList.size();
                int matchNum = iterList.size();
                int unionNum = matchNum;
                probArray[i] = Math.pow(P, matchNum);// * Math.pow((1 - P),
                                                     // unionNum-matchNum);
                // * Math.pow((1 - P), unionNum-matchNum);
                /*
                 * each candidate
                 */
//                MyTimer.showTime("In FetchFeature6:\t candiSubPeakList.size "+candiSubPeakList.size());
                for(int j = 0; j < candiSubPeakList.size(); j++) {
                    if(i != j) {
                        ArrayList<Double> tmpFeatureList = candiSubPeakList.get(j);
                        int shareSame = 0;
                        for(Double iterPeak : iterList) {
                            for(Double iterTmp : tmpFeatureList) {
                                if(Math.abs(iterPeak - iterTmp) < WIN) {
                                    shareSame++;
                                    break;
                                }
                            }
                        }
                        peakNum = commNum + tmpFeatureList.size();
                        matchNum = shareSame;
                        probArray[j] = Math.pow(P, matchNum)// * Math.pow((1 -
                                                            // P),
                                                            // unionNum-matchNum);
                                * Math.pow((1 - P), unionNum - matchNum);

                    }
                }
                /**
                 * 
                 * @probArray:each combination correspond unionNum prob array
                 */
                probArrayList.add(probArray);

            }
        }
        PeakEntropyInfo tmpInfo = new PeakEntropyInfo(comPeak);
        tmpInfo.setCandiIDList(candiIDList);
        tmpInfo.setFeatureProbList(probArrayList);

        // return this.arrayNormalize(probArrayList, candiIDList, preProbArray);
        return tmpInfo;
    }

    public ArrayList<Double> getSubSpMzList(CompareInfo compareInfo, Peak comPeak, int cutTime) {
        double mzA = comPeak.getMz();
        double WIN = 0.5;
        PeakInfo peak = null;

        for(PeakInfo iterPeak : compareInfo.getMatchPeakList()) {
            double mzB = iterPeak.getPeakMz();
            if(Math.abs(mzB - mzA) < WIN) {
                peak = iterPeak;
                break;
            }
        }
        return this.getUinonMzList(peak.getCandiFragNodeList(), cutTime);
    }

    /**
     * 
     * @param fragNodeList
     * @param cutTime
     * @return
     */
    public ArrayList<Double> getUinonMzList(ArrayList<FragNode> fragNodeList, int cutTime) {
        ArrayList<Double> reList = new ArrayList<Double>();
        for(FragNode iterNode : fragNodeList) {
            /*
             * Theory Spectrum pattern select
             */
            for(FragNode iterPeak : iterNode.getCorrespondTheorySpList(cutTime)) {
                /*
                 * just consider by ions
                 */
                if(iterPeak.getIonType().equals(IonType.BIon) || iterPeak.getIonType().equals(IonType.YIon))
                    if(!reList.contains(iterPeak.getSubtreeMass())) {
                        reList.add(iterPeak.getSubtreeMass());
                    }
            }
        }
        return reList;
    }

    private ArrayList<ArrayList<Double>> countCombination(ArrayList<Double> featureMzList) {

        int all = featureMzList.size();
        int nbit = 1 << all;
        ArrayList<ArrayList<Double>> tt_list = new ArrayList<ArrayList<Double>>();
        for(int i = 0; i < nbit; i++) {
            ArrayList<Double> sbList = new ArrayList<Double>();
            for(int j = 0; j < all; j++) {
                if((i & (1 << j)) != 0) {
                    sbList.add(featureMzList.get(j));
                }
            }
            tt_list.add(sbList);

        }
        // System.out.println(tt_list.size());
        MyTimer.showTime("In FetchFeature6:\tAfter countCombination,its size is "+tt_list.size());
        return tt_list;
    }

}
