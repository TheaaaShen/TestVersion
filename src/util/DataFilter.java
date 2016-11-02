package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import spectrum.Peak;
import spectrum.PeakMaxIntensityComparator;
import spectrum.PeakMzComparator;

public class DataFilter {
    public static Peak[] experiSpPeakFiltMaxSumIntens(Peak[] expIonArray, double filtIntensRatio) {
        Peak[] filtedIonArray;
        ArrayList<Peak> dataPointList = new ArrayList<Peak>();
        ArrayList<Peak> filtedPointList = new ArrayList<Peak>();
        double sumIntens = 0;
        double filtSumIntens = 0;
        double filtRatio = filtIntensRatio;
        int index;
        for(Peak iterPeak : expIonArray) {
            sumIntens = sumIntens + iterPeak.getIntensity();
            dataPointList.add(iterPeak);
        }
        Collections.sort(dataPointList, new PeakMaxIntensityComparator());

        for(index = 0; index < dataPointList.size(); index++) {

            filtSumIntens = filtSumIntens + dataPointList.get(index).getIntensity();
            if(filtSumIntens / sumIntens > filtRatio) {
                break;
            } else {
                filtedPointList.add(dataPointList.get(index));
            }

        }
        Collections.sort(filtedPointList, new PeakMzComparator());

        filtedIonArray = new Peak[filtedPointList.size()];
        for(int i = 0; i < filtedPointList.size(); i++) {
            filtedIonArray[i] = filtedPointList.get(i);
        }
        return filtedIonArray;

    }

    public static Peak[] experiSpPeakFiltMinSinglePeak(Peak[] expIonArray, double filtRelateRatio) {
        Peak[] filtedIonArray;
        ArrayList<Peak> dataPointList = new ArrayList<Peak>();
        ArrayList<Peak> filtedPointList = new ArrayList<Peak>();
        double sumIntens = 0;
        double filtRelateIntens = 0;
        double filtRatio = filtRelateRatio;
        int index;
        for(Peak iterPeak : expIonArray) {
            sumIntens = sumIntens + iterPeak.getIntensity();
            dataPointList.add(iterPeak);
        }
        Collections.sort(dataPointList, new PeakMaxIntensityComparator());
        filtRelateIntens = dataPointList.get(0).getIntensity() * filtRatio;
        for(index = 0; index < dataPointList.size(); index++) {

            if(dataPointList.get(index).getIntensity() < filtRelateIntens) {
                break;
            } else {
                filtedPointList.add(dataPointList.get(index));
            }
        }

        Collections.sort(filtedPointList, new PeakMzComparator());

        filtedIonArray = new Peak[filtedPointList.size()];
        for(int i = 0; i < filtedPointList.size(); i++) {
            filtedIonArray[i] = filtedPointList.get(i);
        }
        return filtedIonArray;
    }

    public static Peak[] experiSpPeakFiltMinSinglePeak(ArrayList<Peak> expIonArray, double filtRelateRatio) {
        Peak[] filtedIonArray;
        ArrayList<Peak> dataPointList = expIonArray;
        ArrayList<Peak> filtedPointList = new ArrayList<Peak>();
        double sumIntens = 0;
        double filtRelateIntens = 0;
        double filtRatio = filtRelateRatio;
        int index;
        for(Peak iterPeak : expIonArray) {
            sumIntens = sumIntens + iterPeak.getIntensity();
        }
        Collections.sort(dataPointList, new PeakMaxIntensityComparator());
        filtRelateIntens = dataPointList.get(0).getIntensity() * filtRatio;
        for(index = 0; index < dataPointList.size(); index++) {

            if(dataPointList.get(index).getIntensity() < filtRelateIntens) {
                break;
            } else {
                filtedPointList.add(dataPointList.get(index));
            }
        }

        Collections.sort(filtedPointList, new PeakMzComparator());

        filtedIonArray = new Peak[filtedPointList.size()];
        for(int i = 0; i < filtedPointList.size(); i++) {
            filtedIonArray[i] = filtedPointList.get(i);
        }
        return filtedIonArray;
    }

    public static Peak[] experiSpPeakFiltMinSinglePeakIntens(Peak[] expIonArray, double minIntens) {
        Peak[] filtedIonArray;
        ArrayList<Peak> filtedPointList = new ArrayList<Peak>();
        for(int i = 0; i < expIonArray.length; i++) {

            if(expIonArray[i].getIntensity() > minIntens)
                filtedPointList.add(expIonArray[i]);
        }
        filtedIonArray = new Peak[filtedPointList.size()];

        for(int j = 0; j < filtedPointList.size(); j++) {
            filtedIonArray[j] = filtedPointList.get(j);
        }
        return filtedIonArray;
    }

    public static Peak[] experiSpPeakFiltIntensTopRank(Peak[] expIonArray, int topM) {
        Peak[] filtedIonArray;
        ArrayList<Peak> dataPointList = new ArrayList<Peak>();
        ArrayList<Peak> filtedPointList = new ArrayList<Peak>();
        double sumIntens = 0;
        double filtRelateIntens = 0;
        int index;
        for(Peak iterPeak : expIonArray) {
            sumIntens = sumIntens + iterPeak.getIntensity();
            dataPointList.add(iterPeak);
        }
        Collections.sort(dataPointList, new PeakMaxIntensityComparator());
        for(index = 0; index < dataPointList.size() && index < topM; index++) {

            if(dataPointList.get(index).getIntensity() < filtRelateIntens) {
                break;
            } else {
                filtedPointList.add(dataPointList.get(index));
            }
        }

        Collections.sort(filtedPointList, new PeakMzComparator());

        filtedIonArray = new Peak[filtedPointList.size()];
        for(int i = 0; i < filtedPointList.size(); i++) {
            filtedIonArray[i] = filtedPointList.get(i);
        }
        return filtedIonArray;
    }

    public static Peak[] getNormalizedPeakArray(Peak[] expIonArray, double maxIntens) {
        return Normalize.NormalizeIntens(expIonArray, maxIntens);
    }

    /*
     * filter the NodeList delete the cut more 2 times node not have the cut
     * from "right end NGA"
     */
    public static ArrayList<FragNode> filtRightEndNGAIon(ArrayList<FragNode> nodeList) {
        ArrayList<FragNode> filtedList = new ArrayList<FragNode>();

        for(int i = 0; i < nodeList.size(); i++) {
            FragNode tmpNode = nodeList.get(i);

            if(tmpNode.getIonTypeNote().length() <= 1) {
                filtedList.add(tmpNode);
                /*
                 * statistic right end NGA cutoff
                 */
                if(tmpNode.getStrucID().charAt(0) == '2') {
                    System.out.println(tmpNode.getSubtreeMass() + "\tdetected");
                }
            }
            // else
            // if(tmpNode.getIonTypeNote().equals("BY")||tmpNode.getIonTypeNote().equals("BY"))
            else if(tmpNode.getIonTypeNote().length() > 1) {
                if(tmpNode.getStrucID().charAt(0) == '2') {
                    filtedList.add(tmpNode);
                }
            }

        }
        return filtedList;
    }

    public static ArrayList<ArrayList<FragNode>> filtRightEndNGAIonList(ArrayList<ArrayList<FragNode>> candiSPList) {
        ArrayList<ArrayList<FragNode>> filterList = new ArrayList<ArrayList<FragNode>>();
        for(int i = 0; i < candiSPList.size(); i++) {
            filterList.add(DataFilter.filtRightEndNGAIon(candiSPList.get(i)));
        }
        return filterList;
    }

    // 过滤理论谱，多次断裂和次多次断裂对应同样碎片时认为是最少断裂次数形成的
    public static ArrayList<FragNode> filterTheorySpSameMassIon(
            ArrayList<FragNode> nodeList) {
        if(nodeList == null){
            return nodeList;
        }
        ArrayList<FragNode> filtedList = new ArrayList<FragNode>();
        for(int i = 0; i < nodeList.size();) {
            FragNode tmpNode = nodeList.get(i);
            i++;
            ArrayList<FragNode> candiNodeList = new ArrayList<FragNode>();
            ArrayList<Integer> candiNodeAsciiList = new ArrayList<Integer>();
            while (i < nodeList.size() && (int) tmpNode.getSubtreeMass() == (int) nodeList.get(i).getSubtreeMass()
                    && tmpNode.getStrucID().equals(nodeList.get(i).getStrucID())) {
                FragNode detectedNode = nodeList.get(i);
                if(tmpNode.getIonTypeNote().length() > detectedNode.getIonTypeNote().length()) {
                    tmpNode = detectedNode;
                    candiNodeAsciiList.clear();
                    candiNodeList.clear();

                } else if(tmpNode.getIonTypeNote().length() == detectedNode.getIonTypeNote().length()) {
                    int detectedAsciiValue = countStrAsciiValue(detectedNode.getIonTypeNote());
                    if(countStrAsciiValue(tmpNode.getIonTypeNote()) == detectedAsciiValue) {

                    } else {
                        if(!candiNodeAsciiList.contains(detectedAsciiValue)) {
                            candiNodeList.add(detectedNode);
                            candiNodeAsciiList.add(detectedAsciiValue);
                        }

                    }
                }
                i++;
            }
            candiNodeList.add(tmpNode);
            filtedList.addAll(candiNodeList);

        }
        return filtedList;
    }

    // 过滤理论谱，过滤掉相同m/z的峰,
    public static ArrayList<Double> filterTheorySpSameMZ(ArrayList<FragNode> nodeList) {
        ArrayList<Double> filteredList = new ArrayList<Double>();
        for(FragNode iterNode : nodeList) {
            Double tmpMz = iterNode.getSubtreeMass();
            if(!filteredList.contains(tmpMz)) {
                filteredList.add(tmpMz);
            }
        }
        return filteredList;
    }

    public static ArrayList<FragNode> filterTheorySpSameMZ2(ArrayList<FragNode> nodeList) {
        ArrayList<Double> filteredList = new ArrayList<Double>();
        ArrayList<FragNode> reNodeList = new ArrayList<FragNode>();
        for(FragNode iterNode : nodeList) {
            Double tmpMz = iterNode.getSubtreeMass();
            if(!filteredList.contains(tmpMz)) {
                filteredList.add(tmpMz);
                reNodeList.add(iterNode);
            }
        }
        return reNodeList;
    }

    /*
     * count String's ASCII value
     */
    private static int countStrAsciiValue(String charStr) {
        int reValue = 0;
        for(char iterChar : charStr.toCharArray()) {
            reValue = reValue + iterChar;
        }
        return reValue;
    }

    public static Peak[] filterIsoIons(Peak[] expSp, double WIN) {
        ArrayList<Peak> peakList = new ArrayList<Peak>();
        for(int i = 0; i < expSp.length; i++) {
            peakList.add(expSp[i]);
            int count = 4;
            while (count > 0 && (i + 1) < expSp.length && Math.abs(expSp[i].getMz() - expSp[i + 1].getMz() + 1) < WIN) {
                count--;
                i = i + 1;
            }
        }
        Peak[] reArray = new Peak[peakList.size()];
        for(int j = 0; j < peakList.size(); j++) {
            reArray[j] = peakList.get(j);
        }

        return reArray;
    }

    public static ArrayList<Double> getCommonPeak(ArrayList<ArrayList<Double>> candiSPList) {
        ArrayList<Double> commPeakList = new ArrayList<Double>();
        double WIN = 0.2;
        int commNum = 0;
        for(int i = 0; i < candiSPList.get(0).size(); i++) {
            double peakMzA = candiSPList.get(0).get(i).doubleValue();
            boolean detecter = true;
            for(int j = 1; j < candiSPList.size(); j++) {

                ArrayList<Double> peakListB = candiSPList.get(j);
                boolean detecterInner = false;
                for(Double peakMzB : peakListB) {
                    if(Math.abs(peakMzA - peakMzB) < WIN) {
                        detecterInner = true;
                        break;
                    }
                }

                if(!detecterInner) {
                    detecter = false;
                    break;
                }
            }
            if(detecter) {
                commNum++;
                commPeakList.add(peakMzA);
            }
        }
        return commPeakList;
    }

    public static void deleteCommonPeak(ArrayList<ArrayList<Double>> candiSPlist, ArrayList<Double> commPeakList) {
        double WIN = 0.2;
        for(int i = 0; i < commPeakList.size(); i++) {

            double peakA = commPeakList.get(i);
            for(int j = 0; j < candiSPlist.size(); j++) {
                ArrayList<Double> peakBList = candiSPlist.get(j);
                for(int k = 0; k < peakBList.size(); k++) {

                    double peakB = peakBList.get(k);
                    if(Math.abs(peakA - peakB) < WIN) {
                        candiSPlist.get(j).remove(peakB);
                        break;
                    }
                }
            }
        }
    }

    public static void filterNullIntensity(String inputFile, String outFile) {
        try {
            BufferedReader infile = new BufferedReader(new FileReader(inputFile));
            String rline = new String();
            infile.readLine();
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outFile));

            while ((rline = infile.readLine()) != null) {
                String[] splits = rline.split("\t");
                if(Double.parseDouble(splits[1]) > 0) {
                    outfile.write(rline);
                    outfile.newLine();
                }

            }
            infile.close();
            outfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputFile = "E:/Glycan/Glycan3/peakDeal/nga4-2151-ms2-2151-0001_raw.txt";
        String outFile = "E:/Glycan/Glycan3/peakDeal/nga4-2151-ms2-2151-0001_raw2.txt";
        DataFilter.filterNullIntensity(inputFile, outFile);
    }

}
