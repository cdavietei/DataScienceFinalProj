import java.io.*;

import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.AbstractClassifier;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.*;

public class Main {

    public static void main(String[] args) throws Exception {

        Instances trainingSet = getInstances("data/trainingSet.csv","?",",");
        Instances testingSet = getInstances("data/testingSet.csv","?",",");

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

        LMT lTree = new LMT();

        System.out.println("LMT Tree");
        lTree.buildClassifier(trainingSet);
        testTree(lTree, trainingSet);
        testTree(lTree, testingSet);

        /*
        Instances trainingSet = getInstances("data/processed.cleveland.data","?",",");
        Instances testingSet = getInstances("data/processed.va.data","?",",");
        Instances testingSet2 = getInstances("data/processed.switzerland.data","?",",");
        Instances testingSet3 = getInstances("data/reprocessed.hungarian.data","?",",");

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

    public static void testTree(AbstractClassifier tree, Instances testingSet) throws Exception {

        int correct = 0;
        int falsePos = 0;
        int i=0;
        for(Instance inst : testingSet) {
            double dec = tree.classifyInstance(inst);
            //System.out.printf("Instance: %d \tClass: %f\tDec:%f\n",i++,inst.classValue(),dec);

            if(dec == inst.classValue()) correct++;
            if(inst.classValue() == 0 && dec != 0) falsePos++;
        }

        System.out.printf("Total: %d\tCorrect: %d\tWrong: %d\t False Positives: %d\n",
            testingSet.size(), correct, testingSet.size()-correct, falsePos);
        System.out.printf("Accuracy: %.3f\t False Positives %%: %.3f\n\n",((double)correct)/testingSet.size()*100,
            ((double)falsePos)/testingSet.size()*100);
    }

    public static Instances getInstances(String fileName, String unknownChar, String separator) throws Exception {

        DataSource source = new DataSource(getLoader(fileName, unknownChar, separator));
        Instances instances = source.getDataSet();

        instances.setClassIndex(0);

        return instances;
    }

    public static CSVLoader getLoader(String fileName, String unknownChar, String separator) throws Exception {
        CSVLoader loader = new CSVLoader();

        loader.setSource(new File(fileName));
        loader.setMissingValue(unknownChar);
        loader.setFieldSeparator(separator);
        //loader.setEnclosureCharacters("'");

        return loader;
    }
}
