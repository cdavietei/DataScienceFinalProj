import java.util.*;
import java.io.*;

public class DecisionTree<T> {

    protected T[][] mDataSet;


    public void ingestData(String fileName, int rows, int cols, String splitString, String unknownCharacter) {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        mDataSet = (T[][]) new Object[rows][cols];


    }

    public boolean entropyOf(double probability) {
        return -probability*(Math.log(probability)/Math.log(2));
    }
}
