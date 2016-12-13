import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreeNode<T> {
  public static HashMap<String,Integer> attributeNameHash;
  String nodePrediction;
  String attributeName;
  T attributeValue;
  List<TreeNode<T>> children;

  public TreeNode() {
    this.nodePrediction = "";
    this.attributeName = "";
    this.attributeValue = null;
    this.children = new ArrayList<TreeNode<T>>();
  }

  public TreeNode(String attributeName, T attributeValue) {
    this.nodePrediction = "";
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
    this.children = new ArrayList<TreeNode<T>>();
  }

  public TreeNode buildTree(DataTable<T> data) {
    this.nodePrediction = data.targetMajority().toString();
    if (!data.attributesEmpty() && !data.allTargetsSame()) {
      Attribute<T> nextAttr = data.maxInfoGainAttribute();
      List<T> attrValues = nextAttr.getDistinctValues();

      for (T value : attrValues) {
        String attributeName = nextAttr.getAttributeName();
        TreeNode<T> newNode = new TreeNode(attributeName, value);
        DataTable<T> filteredData = data.filterAttributes(data.indexOfAttribute(attributeName), value);

        children.add(newNode);
        newNode.buildTree(filteredData);
      }
    }
    return this;
  }

  public String predict(List<T> attrList) {
    String prediction = nodePrediction;

    if (!attrList.isEmpty() && !children.isEmpty()) {
      for (TreeNode<T> node : children) {
        int attributeNumber = attributeNameHash.get(node.attributeName);
        if (attrList.get(attributeNumber).equals(node.attributeValue)) {
          prediction = node.predict(attrList);
        }
      }
    }

    return prediction;
  }

  public void preorder() {
    System.out.print(attributeName + ":" + attributeValue.toString() + " " + nodePrediction + " ");
    for (TreeNode<T> child : children) {
      child.preorder();
    }
  }

  public void print() {
    print("", true);
  }

  private void print(String prefix, boolean isTail) {
    String nodeLabel = "root";
    if (attributeValue != null) {
      nodeLabel = attributeName + ":" + attributeValue.toString();
    }
    if (children.isEmpty()) {
      nodeLabel += "--" + nodePrediction;
    }
    System.out.println(prefix + (isTail ? "└── " : "├── ") + nodeLabel);
    for (int i = 0; i < children.size() - 1; i++) {
      children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
    }
    if (children.size() > 0) {
      children.get(children.size() - 1)
              .print(prefix + (isTail ?"    " : "│   "), true);
    }
  }
}
