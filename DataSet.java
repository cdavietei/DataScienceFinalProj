import java.util.*;
import java.io.*;

public class DataSet {

    protected Double[][] mDataSet;
    protected Double[] mEntropyTable;
    protected ArrayList<HashMap<Double, Integer>> mFrequencies;

    public DataSet() {}

    public DataSet(int instanceCount, int attributeNum) {
        mDataSet = new Double[instanceCount][attributeNum];
        mEntropyTable = new Double[attributeNum];
        mFrequencies = new ArrayList<HashMap<Double,Integer>>();
    }

    public static void main(String[] args) {
      DataSet ds = new DataSet();
      Double[] Y = new Double[] {0.0, 1.0, 1.0};
      Double[] X = new Double[] {0.0, 0.0, 1.0};
      System.out.println(ds.informationGain(Y,X));

        /*double ent = entropyOf(2.0/8)+entropyOf(2.0/8);
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

        printArray(conditionalEntropy(probs, 8, freq));*/
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


    /*public void calculateFrequencies() {

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

    }//calculateFrequencies()*/

    /***** Information Gain Calculations ******/

    /**
     * The information gain of Y given X is the following,
     * IG(Y|X) = H(Y) - H(Y|X)
     *
     * @param attributeY - the column in mDataSet containing the attribute to calculate
     * @param attributeX - the column in mDataSet containing the given attribute
     */
    public Double informationGain(Double[] attributeY, Double[] attributeX) {
      return entropyOf(attributeY) - conditionalEntropyOf(attributeY, attributeX);
    }

    /**
     * The entropy of an attribute is the following,
     * H(X) = -1 * sum( P(Xi) log_2 P(Xi) )
     * where each Xi is a particular value of X and P(Xi) is the probability of
     * that value
     */
    public Double entropyOf(Double[] attribute) {
      HashMap<Double,Double> probabilityHash = getProbabilities(attribute);

      Double sum = 0.0;
      for (Double key : probabilityHash.keySet()) {
        sum += entropyCalc(probabilityHash.get(key));
      }

      return sum;
    }

    /**
     * The conditional entropy of Y, given X, is the following,
     * H(Y|X) = -1 * sum(P(Xi) entropyOf(Yi))
     * where Yi is only entries of Y where X = Xi
     */
    public Double conditionalEntropyOf(Double[] attributeY, Double[] attributeX) {
      HashMap<Double,Double> xProbabilities = getProbabilities(attributeX);

      Double sum = 0.0;
      for (Double xValue : xProbabilities.keySet()) {
        Double[] conditionalYVals = filterAttribute(attributeY, attributeX, xValue);
        sum += xProbabilities.get(xValue) * entropyOf(conditionalYVals);
      }

      return sum;
    }

    public HashMap<Double,Double> getProbabilities(Double[] attribute) {
      HashMap<Double,Integer> supportHash = getSupports(attribute);
      HashMap<Double,Double> probabilityHash = new HashMap<Double,Double>();

      for (Double key : supportHash.keySet()) {
        Double probability = (double) supportHash.get(key) / attribute.length;
        probabilityHash.put(key, probability);
      }

      return probabilityHash;
    }

    public HashMap<Double,Integer> getSupports(Double[] attribute) {
      HashMap<Double,Integer> supportHash = new HashMap<Double,Integer>();

      for (Double value : attribute) {
        Integer count = supportHash.get(value);
        if (count == null) {
          supportHash.put(value, 1);
        } else {
          supportHash.put(value, count + 1);
        }
      }

      return supportHash;
    }

    public Double entropyCalc(Double probability) {
      return -1 * probability * (Math.log(probability) / Math.log(2));
    }

    // returns only values of attribute1 where attribute2 is equal to attr2Value
    public Double[] filterAttribute(Double[] attribute1, Double[] attribute2, Double attr2Value) {
      List<Double> filteredAttributeList = new ArrayList<Double>();

      for (int i = 0; i < attribute1.length; i++) {
        // if ith entry of attribute2 is equal to attr2Value, add ith entry of attribute1
        if (attribute2[i].equals(attr2Value)) {
          filteredAttributeList.add(attribute1[i]);
        }
      }

      return filteredAttributeList.toArray(new Double[0]);
    }


    public static void printArray(double[] array) {
        for(double i : array)
            System.out.print(i+" ");
        System.out.println();
    }
}
