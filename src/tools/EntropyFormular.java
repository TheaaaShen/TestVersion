package tools;
import java.util.*;
/*
 * the class used to count peak's Entropy Expectation
 * main method:countExpEntropy
 * @CandiNum: the candidate glycan structure num
 * @FeatureNumIndex: an array stored the feature peak num correspond to the candidate structure
 * array subscript correspond to the structure ID
 * @priorProbArray:prior probability
 */

public class EntropyFormular {
    double p=0.7;
    int[] FeatureNumIndex;
    public void coutExpEntropy(int CandiNum,int[] FeatureNumIndex,double[] priorProbArray)
    {
        this.FeatureNumIndex=FeatureNumIndex;
        double allCombiNumSum=0;
        double allEntropySum=0;
        ArrayList<Double> entropyList=new ArrayList<Double>();
        ArrayList<Double> allCombiNumList=new ArrayList<Double>();
        for(int i=1;i<=CandiNum;i++)
        {
            int CandiID=i;
            int FeatureNum=FeatureNumIndex[CandiID];
            
            for(int j=0;j<=FeatureNum;j++)
            {
                int FeatureExistNum=j;
                double[] probArray=coutProb(FeatureExistNum,CandiID,CandiNum,priorProbArray);
                
                double entropy=this.countEntropy(probArray);
                int combiNum=this.recursion(FeatureNum)/(this.recursion(j)*this.recursion(FeatureNum-j));
                double entropySum=combiNum*entropy;
                entropyList.add(entropySum);
                allEntropySum=allEntropySum+entropySum;
            }
            
            double allCombiNum=Math.pow(2, FeatureNum);
            allCombiNumSum=allCombiNumSum+allCombiNum;
            allCombiNumList.add(allCombiNum);
            
        }
        double FinalEntropy=allEntropySum/allCombiNumSum;
        System.out.println(FinalEntropy);
        
    }
    public int recursion(int Numb)
    {
        if(Numb<0)
        {
            return -1;
        }else if(Numb==0||Numb==1)
        {
            return 1;
        }else
        {
            return Numb*recursion(--Numb);
        }
    }
    public double countEntropy(double[] probArray)
    {
        double entropy=0.0;
        for(int i=1;i<probArray.length;i++)
        {
            entropy=entropy+probArray[i]*Math.log(probArray[i])/Math.log(2);
        }
        entropy=-entropy;
//        System.out.println(entropy);
        return entropy;
                
    }
    public double[] coutProb(int FeatureExistNum,int CandiID ,int CandiNum,double[] priorProbArray)
    {
        double[] probArray=new double[CandiNum+1];
        int FeatureNum=FeatureNumIndex[CandiID];
        double formularUp=priorProbArray[CandiID]*Math.pow(p, FeatureExistNum)*Math.pow((1-p), FeatureNum-FeatureExistNum);
        double formularDown=formularUp;
        for(int i=1;i<=CandiNum;i++)
        {
            if(i!=CandiID)
            {
                formularDown=formularDown+priorProbArray[i]*Math.pow((1-p), FeatureNumIndex[i]);
            }
        }
        
        double CandiIDProb=formularUp/formularDown;
        probArray[CandiID]=CandiIDProb;
        for(int j=1;j<=CandiNum;j++)
        {
            if(j!=CandiID)
            {
                double tmpProb=priorProbArray[j]*Math.pow((1-p), FeatureNumIndex[j])/formularDown;
                probArray[j]=tmpProb;
            }
            
        }
        
        return probArray;
//        System.out.println(prob);
    }
    
    
    public static void main(String[] args)
    {
        EntropyFormular test=new EntropyFormular();
        int CandiNum=2;
        int[] FeatureNumIndex={0,2,5};
        double[] priorProbArray={0,0.7,0.3};
        test.coutExpEntropy(CandiNum, FeatureNumIndex, priorProbArray);
        double[] probArray={0,0.9999,0.0001};
        
//      test.countEntropy(probArray);
    }
}
