package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.eurocarbdb.MolecularFramework.io.CarbohydrateSequenceEncoding;
import org.eurocarbdb.MolecularFramework.io.SugarImporterFactory;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;

public class SugarFragment {
    Hashtable<String, String> carb_hash = new Hashtable<String, String>();

    ArrayList<String> num_list = new ArrayList<String>();
    ArrayList<FragNode> frag_tree_list = new ArrayList<FragNode>();

    public ArrayList<FragNode> getFragTreeList() {
        return this.frag_tree_list;
    }

    /*
     * read: read structure txt file put structure to hashtable
     * 
     * @Hashtable: structure_Id-->structure_str
     * 
     * @List:structure_Id
     */
    public void read(String carb_file) {
        try {
            BufferedReader infile = new BufferedReader(new FileReader(carb_file));
            String rline = new String();
            while ((rline = infile.readLine()) != null) {
                if(rline.contains("start")) {
                    String num = infile.readLine();
                    String mass = infile.readLine();
                    String stru_str = infile.readLine();
                    // while(!(rline=infile.readLine()).contains(mass))

                    while (!(rline = infile.readLine()).contains("end")) {
                        stru_str = stru_str + "\n" + rline;
                    }
                    carb_hash.put(num, stru_str);
                    num_list.add(num);
                    // System.out.println(num+"\t"+stru_str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * readSugar:convert
     * structure_str-->sugar_data-->GlycanNode-->SugarNode-->FragmentTree
     */
    public void readSugar() {
        CarbohydrateSequenceEncoding encode = CarbohydrateSequenceEncoding.carbbank;
        for(String iter : num_list) {
            String stru_str = carb_hash.get(iter);
            try {
                Sugar su = SugarImporterFactory.importSugar(stru_str, encode);
                ConvertSugar test = new ConvertSugar();
                frag_tree_list.add(test.convert(su));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public StructureLib loadStrucLib(String strucFile) {
        StructureLib strucLib = null;
        ArrayList<Double> massIndexList = new ArrayList<Double>();
        Hashtable<Double, ArrayList<String>> candiStrucHash = new Hashtable<Double, ArrayList<String>>();
        
        // a example of structure
//        start
//        798
//        1579.0
//
//
//         a-D-Manp-(1-6)+
//                       |
//                  a-D-Manp-(1-6)+
//                       |        |
//         a-D-Manp-(1-3)+   b-D-Manp-(1-4)-b-D-GlcpNAc-(1-4)-D-GlcNAc
//                                |
//                  a-D-Manp-(1-3)+

        try {
            BufferedReader infile = new BufferedReader(new FileReader(strucFile));
            String rline = new String(); // store a line of input
            while ((rline = infile.readLine()) != null) {
                if(rline.contains("start")) {
                    rline = infile.readLine();
                    while (rline.length() < 1) {
                        rline = infile.readLine();
                    }
                    // Thenumber of this structure record in carbbank.
                    // currently not used
                    // TODO
                    String num = rline; 
                    
                    // read mass
                    rline = infile.readLine();
                    while (rline.length() < 2) {
                        rline = infile.readLine();
                    }
                    Double mass = Double.parseDouble(rline);
                    String stru_str = infile.readLine();

                    while (!(rline = infile.readLine()).contains("end")) {
                        stru_str = stru_str + "\n" + rline;
                    }

                    if(massIndexList.contains(mass)) {
                        candiStrucHash.get(mass).add(stru_str);
                    } else {
                        massIndexList.add(mass);
                        ArrayList<String> strucList = new ArrayList<String>();
                        strucList.add(stru_str);
                        candiStrucHash.put(mass, strucList);

                    }
//                    System.out.println("In SugarFragment Class: "+ num+"\t"+stru_str);
                }
            }
            strucLib = new StructureLib(massIndexList, candiStrucHash);
            infile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("In SugarFragment Class: massIndexList size" 
//                + massIndexList.size());
        return strucLib;
    }

}
