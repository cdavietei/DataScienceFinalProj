import java.io.*;

import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.AbstractClassifier;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.*;

/**
 * A Class that tests two different Data Sets using various
 * Decision Tree Implementations within Weka's Machine Learning suite
 *
 * @author Chris
 */
public class Main {

    public static void main(String[] args) throws Exception {

        //Used to test the Congressional voting Data Set

        Instances trainingSet = getInstances("data/trainingSet.csv","?",",",0);
        Instances testingSet = getInstances("data/testingSet.csv","?",",",0);

        //Creates a new C4.5 tree
        J48 dTree = new J48();

        System.out.println("Pruned C4.5 Tree");
        dTree.buildClassifier(trainingSet);
        testTree(dTree, trainingSet);
        testTree(dTree, testingSet);

        dTree = new J48();
        dTree.setUnpruned(true);

        System.out.println("Unpruned C4.5 Tree");
        dTree.buildClassifier(trainingSet);
        testTree(dTree, trainingSet);
        testTree(dTree, testingSet);

        //Builds a new Logistical Model Tree
        LMT lTree = new LMT();

        System.out.println("LMT Tree");
        lTree.buildClassifier(trainingSet);
        testTree(lTree, trainingSet);
        testTree(lTree, testingSet);

        //Uncomment block below to test Heart Disease Data set

        /*
        Instances trainingSet = getInstances("data/processed.cleveland.data","?",",",13);
        Instances testingSet = getInstances("data/processed.va.data","?",",");
        Instances testingSet2 = getInstances("data/processed.switzerland.data","?",",",13);
        Instances testingSet3 = getInstances("data/reprocessed.hungarian.data","?",",",13);

        System.out.println(trainingSet.equalHeaders(testingSet));
        System.out.println(trainingSet.equalHeaders(testingSet2));
        System.out.println(trainingSet.equalHeaders(testingSet3));

        System.out.println(trainingSet.classAttribute());
        System.out.println(testingSet.classAttribute());

        J48 dTree = new J48();

        System.out.println("Pruned C4.5 Tree");
        dTree.buildClassifier(trainingSet);
        testTree(dTree, trainingSet);
        testTree(dTree, testingSet);
        testTree(dTree, testingSet2);
        testTree(dTree, testingSet3);

        dTree = new J48();
        dTree.setUnpruned(true);

        System.out.println("Unpruned C4.5 Tree");
        dTree.buildClassifier(trainingSet);
        testTree(dTree, trainingSet);
        testTree(dTree, testingSet);
        testTree(dTree, testingSet2);
        testTree(dTree, testingSet3);

        LMT lTree = new LMT();

        System.out.println("LMT Tree");
        lTree.buildClassifier(trainingSet);
        testTree(lTree, trainingSet);
        testTree(lTree, testingSet);
        testTree(lTree, testingSet2);
        testTree(lTree, testingSet3);
        */
    }

    /**
     * Tests a given decision tree against a given dataset
     * @param   tree        The tree to be used with testing
     * @param   testingSet  The dataset used to test the accuracy of the tree
     * @throws  Exception   Generic Exception thrown by Weka if the classifyInstance() method fails
     */
    public static void testTree(AbstractClassifier tree, Instances testingSet) throws Exception {

        int correct = 0;
        int falsePos = 0;
        int i=0;
        for(Instance inst : testingSet) {
            double dec = tree.classifyInstance(inst);

            if(dec == inst.classValue()) correct++;
            if(inst.classValue() == 0 && dec != 0) falsePos++;
        }

        System.out.printf("Total: %d\tCorrect: %d\tWrong: %d\t False Positives: %d\n",
            testingSet.size(), correct, testingSet.size()-correct, falsePos);
        System.out.printf("Accuracy: %.3f\t False Positives %%: %.3f\n\n",((double)correct)/testingSet.size()*100,
            ((double)falsePos)/testingSet.size()*100);
    }//testTree(AbstractClassifier, Instances)


    /**
     * Creates a dataset from a CSV File
     * @param   fileName    The file containing the dataset
     * @param   unknownChar The character representing a missing value in the dataset
     * @param   separator   The field separator for each line
     * @return  An Instances object representing the dataset
     * @throws  Exception  Generic Exception thrown by Weka if the classifyInstance() method fails
     */
    public static Instances getInstances(String fileName, String unknownChar, String separator, int classIndex) throws Exception {

        DataSource source = new DataSource(getLoader(fileName, unknownChar, separator));
        Instances instances = source.getDataSet();

        instances.setClassIndex(classIndex);

        return instances;
    }//getInstances(String, String, String)

    /**
     * Returns a CSV loader used by the getInstances() method
     * @param   fileName    The file containing the dataset
     * @param   unknownChar The character representing a missing value in the dataset
     * @param   separator   The field separator for each line
     * @return  A Weka CSVLoader that parses the CSV file
     * @throws  Exception  Generic Exception thrown by Weka if the classifyInstance() method fails
     */
    public static CSVLoader getLoader(String fileName, String unknownChar, String separator) throws Exception {
        CSVLoader loader = new CSVLoader();

        loader.setSource(new File(fileName));
        loader.setMissingValue(unknownChar);
        loader.setFieldSeparator(separator);

        return loader;
    }
}
