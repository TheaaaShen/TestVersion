package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.eurocarbdb.MolecularFramework.io.CarbohydrateSequenceEncoding;
import org.eurocarbdb.MolecularFramework.io.SugarImporterFactory;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;

public class GenerateTheorySp_Test {

    public ArrayList<FragNode> generateSpForStrucFile(String struFile,
            int cutTime) {
        ArrayList<FragNode> fragTreeList = new ArrayList<FragNode>();
        String stru_str = struFile;

        try {

            CarbohydrateSequenceEncoding encode = CarbohydrateSequenceEncoding.carbbank;
            Sugar su = SugarImporterFactory.importSugar(stru_str, encode);

            ConvertSugar test = new ConvertSugar();
            FragNode fragTree = test.convert(su);
            fragTreeList.add(fragTree);
            for (int i = 0; i < cutTime; i++) {
                ArrayList<FragNode> tmpList = new ArrayList<FragNode>();

                for (FragNode iterNode : fragTreeList) {
                    tmpList.addAll(iterNode.getSubTreeNodeList());
                }
                fragTreeList = tmpList;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragTreeList;
    }

    public ArrayList<FragNode> generateSpForStrucStr(String stru_str,
            int cutTime) {
        ArrayList<FragNode> fragTreeList = new ArrayList<FragNode>();
        try {

            CarbohydrateSequenceEncoding encode = CarbohydrateSequenceEncoding.carbbank;
            Sugar su = SugarImporterFactory.importSugar(stru_str, encode);

            ConvertSugar test = new ConvertSugar();
            FragNode fragTree = test.convert(su);
            fragTreeList.add(fragTree);
            for (int i = 0; i < cutTime; i++) {
                ArrayList<FragNode> tmpList = new ArrayList<FragNode>();

                for (FragNode iterNode : fragTreeList) {
                    tmpList.addAll(iterNode.getSubTreeNodeList());
                }
                fragTreeList = tmpList;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragTreeList;
    }

    /*
     * filter the same mass have more than two type of explains 
     */
    public ArrayList<FragNode> countMultiStageCutSpNoceList(FragNode rootNode,
            int cutTime) {
        
        return DataFilter.filterTheorySpSameMassIon(this.countMultiStageCutTheoryIons(rootNode, cutTime));
    }

    public ArrayList<FragNode> countMultiStageCutTheoryIons(FragNode rootNode,
            int cutTime) {
        ArrayList<FragNode> returnList = new ArrayList<FragNode>();

        for (int k = 1; k <= cutTime; k++) {
            ArrayList<FragNode> generatedList = new ArrayList<FragNode>();
            generatedList.add(rootNode);
            for (int i = 0; i < k; i++) {
                ArrayList<FragNode> tmpList = new ArrayList<FragNode>();

                for (FragNode iterNode : generatedList) {
                    tmpList.addAll(iterNode.getSubTreeNodeList());
                }
                generatedList = tmpList;
            }
            returnList.addAll(generatedList);
        }
        /*
         * add C Z ions
         */

        // returnList=this.addCZIonMz(returnList);

        Collections.sort(returnList, new FragNodeMassComparator());

        /*
         * filter spectrum peak list
         */

        return returnList;
    }
    
    public ArrayList<FragNode> generateSpForStrucFile(String struFile,
            int cutTime, String ionType, boolean RightEndNGACut) {
        ArrayList<FragNode> returnList = new ArrayList<FragNode>();
        for (int i = 1; i <= cutTime; i++) {
            ArrayList<FragNode> generatedList = this.generateSpForStrucFile(
                    struFile, i);
            returnList.addAll(generatedList);
        }
        
        /*
         * add C Z ions
         */
        if (ionType.contains("cz"))
            returnList = this.addCZIonMz(returnList);
        Collections.sort(returnList, new FragNodeMassComparator());

        /*
         * 大于一次断裂时，断裂的基础是第一次为左端方块断裂 过滤理论谱，多次断裂和次多次断裂对应同样碎片时认为是最少断裂次数形成的
         */
        if (RightEndNGACut) {
            return DataFilter.filtRightEndNGAIon(DataFilter
                    .filterTheorySpSameMassIon(returnList));
        } else {
            return DataFilter.filterTheorySpSameMassIon(returnList);
        }
    }

    public void writeOut(String outFile, ArrayList<FragNode> fragNodeList) {
        ArrayList<Double> ionList = new ArrayList<Double>();
        for (FragNode iterNode : fragNodeList) {
            double tmpMass = iterNode.getSubtreeMass();
            // if(!ionList.contains(tmpMass))
            {
                tmpMass=FormatNum.DoubleFormat(tmpMass, 2);
                if(!ionList.contains(tmpMass))
                ionList.add(tmpMass);
                System.out.println(tmpMass + "\t" + iterNode.getIonTypeNote()
                        + "\t" + iterNode.getIonTypeNote().length()+ "\t"
                        + iterNode.getStrucID());
            }
        }
        // Collections.sort(ionList);
        try {
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outFile));
            for (Double iterIon : ionList) {
                outfile.write(String.valueOf(iterIon));
                // System.out.println(String.valueOf(iterIon));
                outfile.newLine();
            }
            outfile.flush();
            outfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<FragNode> addCZIonMz(ArrayList<FragNode> fragnode_list) {
        ArrayList<FragNode> returnList = new ArrayList<FragNode>();
        for (FragNode iterNode : fragnode_list) {
            char[] ionTypeCharArray = iterNode.getIonTypeNote().toCharArray();
            ArrayList<ArrayList<Integer>> switchList = this
                    .countCombination(ionTypeCharArray);
            for (ArrayList<Integer> iterList : switchList) {
                char[] tmpCharArray = iterNode.getIonTypeNote().toCharArray();
                
                FragNode addNode = new FragNode();
                double addMass = 0.0;
                for (Integer iterPos : iterList) {
                    if (tmpCharArray[iterPos] == 'B') {
                        tmpCharArray[iterPos] = 'C';
                        addMass = addMass + 18;
                    } else {
                        tmpCharArray[iterPos] = 'Z';
                        addMass = addMass - 18;
                    }
                }
                addNode.setSubtreeMass(iterNode.getSubtreeMass() + addMass);
                addNode.setIonTypeNote(String.valueOf(tmpCharArray));

                addNode.setSugarNode(iterNode.getSugarNode());
                addNode.setStrucID(iterNode.getStrucID());
                returnList.add(addNode);
            }
        }
        return returnList;
    }

    /*
     * count array elements' combination set
     */
    private ArrayList<ArrayList<Integer>> countCombination(char[] charArray) {

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
    public String loadStrucLib(String strucFile)
    {
    
        String stru_str="";
        try{
            BufferedReader infile=new BufferedReader(new FileReader(strucFile));
            String rline=new String();
            
                    while((rline=infile.readLine())!=null)
                    {
                        stru_str=stru_str+"\n"+rline;
                    }
                    
            infile.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return stru_str;
    }
    public static void main(String[] args) {
        // String base="E:/Glycan/spectra_data/Theory_Spectrum/";
        // String struFile=base+"test_stru2.txt";
        // String outFile=base+"the_sp.txt";
        String base = "E:/Glycan/Glycan2/NGA3/TheroySp/";
        String struFile = base + "2151_struc.txt";
        String outFile = base + "2151_theorySp2.txt";
        
        int cutTime = 2;
        String ionType = "bycz";
        boolean RightEndNGACut = false;
        GenerateTheorySp_Test test = new GenerateTheorySp_Test();
        test.writeOut(outFile, test.generateSpForStrucFile(test.loadStrucLib(struFile), cutTime,
                ionType, RightEndNGACut));

    }

}
