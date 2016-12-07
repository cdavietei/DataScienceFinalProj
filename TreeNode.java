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
    this.label = data.targetMajority().toString();
    if (!data.attributesEmpty() && !data.allTargetsSame()) {
      Attribute<T> nextAttr = data.maxInfoGainAttribute();
      List<T> attrValues = nextAttr.getDistinctValues();

      for (T value : attrValues) {
        int attrNumber = nextAttr.getAttributeNumber();
        TreeNode<T> newNode = new TreeNode(attrNumber, value);
        DataTable<T> filteredData = data.filterAttributes(data.indexOfAttribute(attrNumber), value);

        children.add(newNode);
        newNode.buildTree(filteredData);
      }
    }
    return this;
  }

  public String predict(List<T> attrList) {
    String prediction = label;

    if (!attrList.isEmpty() && !children.isEmpty()) {
      for (TreeNode<T> node : children) {
        if (attrList.get(node.attributeNumber).equals(node.attributeValue)) {
          prediction = node.predict(attrList);
        }
      }
    }
    
    return prediction;
  }

  public void preorder() {
    System.out.print(attributeNumber + ":" + attributeValue.toString() + " " + label + " ");
    for (TreeNode<T> child : children) {
      child.preorder();
    }
  }
}
