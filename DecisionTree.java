import java.util.*;
import java.io.*;

public class DecisionTree<T> {
  TreeNode<T> root;

  public static void main(String[] args) {
    DecisionTree<String> dTree = new DecisionTree<String>();
    DataTable<String> dTable = dTree.parseFileToDataTable(14,"data\\processed.cleveland.data",",",13);
    dTree.root = new TreeNode<String>();
    dTree.root = dTree.root.buildTree(dTable);

    int total = 0;
    int correct = 0;
    int wrong = 0;
    int falsePositives = 0;
    double accuracy = 0.0;
    double falsePositivePercent = 0.0;

    DataTable<String> resultTable = dTree.parseFileToDataTable(14,"data\\reprocessed.hungarian.data",",",13);
    resultTable.printTable();
    List<Attribute<String>> testAttributes = resultTable.getAttributes();
    Attribute<String> targetAttribute = resultTable.getTargetAttribute();

    List<List<String>> lists = dTree.transpose(testAttributes);
    for (List<String> list : lists) {
      String prediction = dTree.root.predict(list);
      System.out.println(prediction);
      if(prediction.equals(targetAttribute.get(lists.indexOf(list)))) {
        correct++;
      } else {
        wrong++;
        if (prediction.equals("present")) {
          falsePositives++;
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
  }

  public List<List<T>> transpose(List<Attribute<T>> attrList) {
    List<List<T>> finalList = new ArrayList<List<T>>();
    for (int i = 0; i < attrList.get(0).size(); i++) {
      ArrayList<T> currList = new ArrayList<T>();
      for (int j = 0; j < attrList.size(); j++) {
        currList.add(attrList.get(j).get(i));
      }
      finalList.add(currList);
    }
    return finalList;
  }

  public DataTable<String> parseFileToDataTable(int attrCount, String fileName, String splitString, int targetColNumber) {
    List<Attribute<String>> attrList = parseFileToAttributeList(attrCount, fileName, splitString);
    Attribute<String> targetAttribute = attrList.remove(targetColNumber);
    for (int i = targetColNumber; i < attrList.size(); i++) {
      attrList.get(i).attributeNumber--;
    }

    return new DataTable<String>(attrList, targetAttribute);
  }

  public List<Attribute<String>> parseFileToAttributeList(int attrCount, String fileName, String splitString) {
    List<Attribute<String>> attrList = new ArrayList<Attribute<String>>();
    for (int i = 0; i < attrCount; i++) {
      attrList.add(new Attribute<String>(i));
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
        parseLine(attrList, reader.readLine().split(splitString), unknownCharacter, i++, unknownReplacement);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }

  }//ingestData(String, String, String)

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
