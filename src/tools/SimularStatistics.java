package tools;

import java.util.*;

import spectrum.Peak;
import util.*;

public class SimularStatistics {

    /*
     * count the probability score
     */
    public ScoreProbInfo compareNode(ArrayList<FragNode> fragnode_list,
            Peak[] exp_data_group, double ion_win, double preProb) {
        if (fragnode_list == null) {
            return null;
        }

        int re_num = 0;
        int no_num = 0;
        double score = 1;
        double PExist = 0.7;
        
        ArrayList<Peak> peakList = new ArrayList<Peak>();
        Hashtable<Peak, ArrayList<FragNode>> peakHash = new Hashtable<Peak, ArrayList<FragNode>>();
        System.out.println("theory peak mz list:");
         for(FragNode iter_the:fragnode_list)
//        for (FragNode iter_the : this.filtSameMz(fragnode_list)) 
        {
            // System.out.println(iter_the.getMass());
            boolean Matched = false;
            for (Peak iter_exp : exp_data_group) {
                if (iter_the.getSubtreeMass()> iter_exp.getMz()-ion_win
                        && iter_the.getSubtreeMass()< iter_exp.getMz()+ion_win) {
                    re_num++;
                    score = score * PExist;
                    Matched = true;
                    if(peakList.contains(iter_exp))
                    {
                        peakHash.get(iter_exp).add(iter_the);
                    }else
                    {
                        peakList.add(iter_exp);
                        ArrayList<FragNode> valueList=new ArrayList<FragNode>();
                        valueList.add(iter_the);
                        peakHash.put(iter_exp, valueList);
                    }
                    
                    break;
                }

            }
            if (!Matched) {
                score = score * (1 - PExist);
                no_num++;
            }
        }

        // score=(double)(re_num)/(no_num+re_num);
        score = score * preProb;
        // System.out.println(score);
        System.out.println("re_num" + re_num + "\t" + no_num);
        System.out.println((double) (re_num) / (no_num + re_num));
        ScoreProbInfo reInfo = new ScoreProbInfo(score, peakList, peakHash);
        reInfo.setIonNum(no_num + re_num);
        reInfo.setMatchedNum(re_num);
        return reInfo;

    }

    /*
     * candiScore function
    public ScoreProbInfo compareNode2(ArrayList<FragNode> fragnode_list,
            DataPoint[] exp_data_group, double ion_win, double preProb) {
        if (fragnode_list == null) {
            return null;
        }

        int re_num = 0;
        int no_num = 0;
        double score = 1;
        double PExist = 0.7;

        double iso_mass = 2;
        ArrayList<DataPoint> peakList = new ArrayList<DataPoint>();
        Hashtable<DataPoint, FragNode> peakHash = new Hashtable<DataPoint, FragNode>();
        System.out.println("theory peak mz list:");
        // for(FragNode iter_the:fragnode_list)

        // System.out.println(iter_the.getMass());

        ArrayList<FragNode> filtedSameMzList=this.filtSameMz(fragnode_list);
        for (DataPoint iter_exp : exp_data_group) {
            boolean Matched = false;
            for (FragNode iter_the : filtedSameMzList) {
                if (iter_the.getMass() + ion_win + iso_mass > iter_exp.getMZ()
                        && iter_the.getMass() - ion_win < iter_exp.getMZ()) {
                    re_num++;
                    
                    score = score * PExist;
                    Matched = true;
                    peakList.add(iter_exp);
                    peakHash.put(iter_exp, iter_the);
                    break;
                }

            }
            if (!Matched) {
                score = score * (1 - PExist);
                no_num++;
            }
        }

        // score=(double)(re_num)/(no_num+re_num);
        score = score * preProb;
        // System.out.println(score);
        System.out.println("re_num" + re_num + "\t" + no_num);
        System.out.println((double) (re_num) / (no_num + re_num));
        ScoreProbInfo reInfo = new ScoreProbInfo(score, peakList, peakHash);
        reInfo.setIonNum(no_num + re_num);
        reInfo.setMatchedNum(re_num);
        return reInfo;

    }
    */
    public ArrayList<FragNode> filtSameMz(ArrayList<FragNode> fragnode_list) {
        ArrayList<FragNode> filtFragNodeList = new ArrayList<FragNode>();
        ArrayList<Double> mzList = new ArrayList<Double>();
        for (FragNode iterNode : fragnode_list) {
            double tmpMz = FormatNum.DoubleFormat(iterNode.getSubtreeMass(), 4);
            if (!mzList.contains(tmpMz)) {
                mzList.add(tmpMz);
                filtFragNodeList.add(iterNode);
            }
        }
        return filtFragNodeList;
    }
    public ArrayList<FragNode> addCZIonMz(ArrayList<FragNode> fragnode_list)
    {
        ArrayList<FragNode> returnList=new ArrayList<FragNode>();
        for(FragNode iterNode:fragnode_list)
        {
            char[] ionTypeCharArray=iterNode.getIonTypeNote().toCharArray();
            ArrayList<ArrayList<Integer>> switchList=this.countCombination(ionTypeCharArray);
            for(ArrayList<Integer> iterList:switchList)
            {
                FragNode addNode=new FragNode();
                double addMass=0.0;
                for(Integer iterPos:iterList)
                {
                    if(ionTypeCharArray[iterPos]=='B')
                    {
                        ionTypeCharArray[iterPos]='C';
                        addMass=addMass+18;
                    }else
                    {
                        ionTypeCharArray[iterPos]='Z';
                        addMass=addMass-18;
                    }
                    
                }
                addNode.setSubtreeMass(iterNode.getSubtreeMass()+addMass);
                addNode.setIonTypeNote(String.valueOf(ionTypeCharArray));
                addNode.setSugarNode(iterNode.getSugarNode());
                returnList.add(addNode);
                
            }
        }
        return returnList;
    }
    private ArrayList<ArrayList<Integer>> countCombination(
            char[] charArray) {

        
        int all = charArray.length;
        int nbit = 1 << all;
        ArrayList<ArrayList<Integer>> return_list = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < nbit; i++) {
            ArrayList<Integer> tmpList = new ArrayList<Integer>();
            for (int j = 0; j < all; j++) {
                if ((i & (1 << j)) != 0) {
                    tmpList.add(j);
                }
            }
            return_list.add(tmpList);
            
        }
        // System.out.println(tt_list.size());
        return return_list;
    }    
}
