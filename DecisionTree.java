import java.util.*;
import java.io.*;

public class DecisionTree<T> {
  TreeNode<T> root;

  public static void main(String[] args) {
    DecisionTree<String> dTree = new DecisionTree<String>();
    DataTable<String> dTable = dTree.parseFileToDataSet(4,"data\\sampleData.csv",",",3);
    dTree.root = new TreeNode<String>();
    dTree.root = dTree.root.buildTree(dTable);
    //dTree.root.preorder();
    //dTable.printTable();
  }

  public DataTable<String> parseFileToDataSet(int attrCount, String fileName, String splitString, int targetColNumber) {
    List<Attribute<String>> attrList = new ArrayList<Attribute<String>>();
    for (int i = 0; i < attrCount; i++) {
      attrList.add(new Attribute<String>(i));
    }
    ingestData(attrList, fileName, splitString,"?","unknown");
    Attribute<String> targetAttribute = attrList.remove(targetColNumber);
    for (int i = targetColNumber; i < attrList.size(); i++) {
      attrList.get(i).attributeNumber--;
    }

    return new DataTable<String>(attrList, targetAttribute);
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
