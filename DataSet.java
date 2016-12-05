import java.util.*;
import java.io.*;

public class DataSet {

    protected Double[][] mDataSet;
    protected Double[] mEntropyTable;
    protected ArrayList<HashMap<Double, Integer>> mFrequencies;

    public DataSet(int instanceCount, int attributeNum) {
        mDataSet = new Double[instanceCount][attributeNum];
        mEntropyTable = new Double[attributeNum];
        mFrequencies = new ArrayList<HashMap<Double,Integer>>();
    }

    @SuppressWarnings("unchecked")
    public void ingestData(String fileName, String splitString, String unknownCharacter) {
        try {

            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            int i=0;

            while(reader.ready())
                parseLine(reader.readLine().split(splitString), unknownCharacter, i++);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }//ingestData(String, String, String)

    public void parseLine(String[] line, String unknownCharacter, int i) {

        if(line.length != mDataSet[i].length)
            throw new Error("Invalid line at: "+i);

        for(int j=0; j<mDataSet[i].length; j++) {
            if(line[j].equals(unknownCharacter))
                mDataSet[i][j] = Double.MIN_VALUE;
            else
                mDataSet[i][j] = Double.parseDouble(line[j]);
        }
    }//parseLine(String[], String, int)

    public void calculateIG(int targetAttrCol) {
        calculateFrequencies();

        for(int i=0; i<mEntropyTable.length; i++) {
            HashMap<Double, Integer> attrMap = mFrequencies.get(i);
            for(Double key : attrMap.keySet()) {
                int x = attrMap.get(key);
                mEntropyTable[i] += entropyOf(((double)x) / mDataSet.length);
            }
        }//for i
    }//calculateIG(int)

    public void calculateFrequencies() {

        for(int j=0; j<mDataSet[0].length; j++) {
            HashMap<Double, Integer> attrMap = new HashMap<Double, Integer>();

            for(int i=0; i<mDataSet.length; i++) {
                int value = 0;
                double key = mDataSet[i][j];
                if(key !=  Double.MIN_VALUE && attrMap.containsKey(key))
                    value = attrMap.get(key);

                attrMap.put(key, value+1);
            }//for i
            mFrequencies.add(attrMap);
        }//for j

    }//calculateFrequencies()

    public double infoGainOf(double given, double probability) {

        return 1;
    }

    public static double entropyOf(double probability) {
        return -probability*(Math.log(probability)/Math.log(2));
    }

    public static double[] conditionalEntropy(int[][] probs, int total, int[] freq) {

        double[] ents = {0,0,0};


        for(int i=0; i<probs.length; i++) {
            int index = probs[i][0];
            double prob = (double)freq[index] / total;

            double ent = entropyOf(condProb( / total), condProb( / total));

            //System.out.println(prob);
            //System.out.println(ent);
            ents[index] += prob*ent;
        }

        return ents;
    }

    public static double condProb(double x, double y) {
        return x*y/x;
    }

    public static void printArray(double[] array) {
        for(double i : array)
            System.out.print(i+" ");
        System.out.println();
    }

    public static void main(String[] args) {

        double ent = entropyOf(2.0/8)+entropyOf(2.0/8);
        System.out.println(ent);

        System.out.println((2.0/8)*entropyOf(2.0/8) + (2.0/8)*entropyOf(2.0/8));

        int[] freq = {4,2,2};
        int[][] probs = {
            {0,1},
            {1,0},
            {2,1},
            {0,0},
            {0,0},
            {2,1},
            {1,0},
            {0,1}
        };

        printArray(conditionalEntropy(probs, 8, freq));
    }


}
