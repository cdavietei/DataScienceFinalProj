import java.util.*;
import java.io.*;

public class DecisionTree<T> {
  TreeNode<T> root;

  public static void main(String[] args) {
    DecisionTree<String> dTree = new DecisionTree<String>();
    DataTable<String> dTable = dTree.parseFileToDataTable(14,"data/threshold/processed.cleveland.csv",",",13);
    dTree.root = new TreeNode<String>();
    dTree.root.attributeNameHash = dTable.getAttributeNameHash();
    dTree.root = dTree.root.buildTree(dTable);

    int total = 0;
    int correct = 0;
    int wrong = 0;
    int falsePositives = 0;
    double accuracy = 0.0;
    double falsePositivePercent = 0.0;

    DataTable<String> resultTable = dTree.parseFileToDataTable(14,"data/threshold/processed.va.csv",",",13);
    //resultTable.printTable();
    List<Attribute<String>> testAttributes = resultTable.getAttributes();
    Attribute<String> targetAttribute = resultTable.getTargetAttribute();

    List<List<String>> lists = dTree.asInstanceList(testAttributes);
    for (List<String> list : lists) {
      String prediction = dTree.root.predict(list);
      System.out.print("Prediction: "+prediction+"\t Target Value: "+targetAttribute.get(lists.indexOf(list))+" ");
      if(prediction.equals(targetAttribute.get(lists.indexOf(list)))) {
        System.out.println("correct");
        correct++;
      } else {
        wrong++;
        if (prediction.equals("'present'")) {
          System.out.println("false positive");
          falsePositives++;
        } else {
          System.out.println("incorrect");
        }
      }
      total++;
    }
    accuracy = (double) correct / total;
    falsePositivePercent = (double) falsePositives / total;

    System.out.println("Total: " + total);
    System.out.println("Correct: " + correct);
    System.out.println("Wrong: " + wrong);
    System.out.println("False positives: " + falsePositives);
    System.out.println("Accuracy: " + (accuracy * 100));
    System.out.println("False positive %: " + (falsePositivePercent * 100));

    dTree.root.print();
  }

  // Converts format of a list of columns to a list of rows
  // Allows easy iteration through each individual instance during prediction phase
  public List<List<T>> asInstanceList(List<Attribute<T>> attrList) {
    List<List<T>> instanceList = new ArrayList<List<T>>();
    for (int i = 0; i < attrList.get(0).size(); i++) {
      // Create a new instance (row) and populate it with the corresponding
      // ith attribute value of each attribute column
      ArrayList<T> currentInstance = new ArrayList<T>();
      for (int j = 0; j < attrList.size(); j++) {
        currentInstance.add(attrList.get(j).get(i));
      }
      instanceList.add(currentInstance);
    }
    return instanceList;
  }

  public DataTable<String> parseFileToDataTable(int attrCount, String fileName, String splitString, int targetColNumber) {
    List<Attribute<String>> attrList = parseFileToAttributeList(attrCount, fileName, splitString);
    Attribute<String> targetAttribute = attrList.remove(targetColNumber);

    return new DataTable<String>(attrList, targetAttribute);
  }

  public List<Attribute<String>> parseFileToAttributeList(int attrCount, String fileName, String splitString) {
    List<Attribute<String>> attrList = new ArrayList<Attribute<String>>();
    for (int i = 0; i < attrCount; i++) {
      attrList.add(new Attribute<String>());
    }
    ingestData(attrList, fileName, splitString,"?","unknown");

    return attrList;
  }

  @SuppressWarnings("unchecked")
  public void ingestData(List<Attribute<String>> attrList, String fileName, String splitString, String unknownCharacter, String unknownReplacement) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));

      int i=0;
      while(reader.ready()) {
        if (i == 0) {
          parseFirstLine(attrList, reader.readLine().split(splitString), unknownCharacter, i++, unknownReplacement);
        }
        parseLine(attrList, reader.readLine().split(splitString), unknownCharacter, i++, unknownReplacement);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }

  }//ingestData(String, String, String)

  public void parseFirstLine(List<Attribute<String>> attrList, String[] line, String unknownCharacter, int i, String unknownReplacement) {
      if(line.length != attrList.size())
          throw new Error("Invalid line at: "+i);

      for(int j=0; j < attrList.size(); j++) {
        Attribute<String> attr = attrList.get(j);
        if(line[j].equals(unknownCharacter)) {
          attr.attributeName = unknownReplacement;
        } else {
          attr.attributeName = line[j];
        }
      }
  }//parseLine(String[], String, int)

  public void parseLine(List<Attribute<String>> attrList, String[] line, String unknownCharacter, int i, String unknownReplacement) {
      if(line.length != attrList.size())
          throw new Error("Invalid line at: "+i);

      for(int j=0; j < attrList.size(); j++) {
        Attribute<String> attr = attrList.get(j);
        if(line[j].equals(unknownCharacter)) {
          attr.add(unknownReplacement);
        } else {
          attr.add(line[j]);
        }
      }
  }//parseLine(String[], String, int)
}
