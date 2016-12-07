import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
  String label;
  int attributeNumber;
  T attributeValue;
  List<TreeNode<T>> children;

  public TreeNode() {
    this.label = "";
    this.attributeNumber = 0;
    this.attributeValue = null;
    this.children = new ArrayList<TreeNode<T>>();
  }

  public TreeNode(int attributeNumber, T attributeValue) {
    this.label = "";
    this.attributeNumber = attributeNumber;
    this.attributeValue = attributeValue;
    this.children = new ArrayList<TreeNode<T>>();
  }

  public TreeNode buildTree(DataTable<T> data) {
    if (data.attributesEmpty() || data.allTargetsSame()) {
      this.label = data.targetMajority().toString();
    } else {
      Attribute<T> nextAttr = data.maxInfoGainAttribute();
      System.out.println("Building node with attribute " + nextAttr.getAttributeNumber());
      List<T> attrValues = nextAttr.getDistinctValues();

      for (T value : attrValues) {
        int attrNumber = nextAttr.getAttributeNumber();
        TreeNode<T> newNode = new TreeNode(attrNumber, value);
        DataTable<T> filteredData = data.filterAttributes(data.indexOfAttribute(attrNumber), value);
        System.out.println("Attribute " + attrNumber + ":" + value.toString());
        /*data.printTable();
        System.out.println("------");
        filteredData.printTable();
        System.out.println();*/
        children.add(newNode);
        newNode.buildTree(filteredData);
      }
    }
    return this;
  }

  public void preorder() {
    System.out.print(attributeNumber + ":" + attributeValue.toString() + " " + label + " ");
    for (TreeNode<T> child : children) {
      child.preorder();
    }
  }
}
