import java.util.ArrayList;
import java.util.List;

public class Attribute<T> {
  public int attributeNumber;
  public List<T> values;

  public Attribute(int attributeNumber) {
    this.attributeNumber = attributeNumber;
    values = new ArrayList<T>();
  }

  public Attribute(int attributeNumber, List<T> values) {
    this.attributeNumber = attributeNumber;
    this.values = values;
  }

  public boolean allEqual() {
    boolean allEqual = true;
    for (int i = 0; i < values.size() && allEqual; i++) {
      allEqual = allEqual && values.get(0).equals(values.get(i));
    }
    return allEqual;
  }

  public List<T> getValues() {
    return this.values;
  }

  public int getAttributeNumber() {
    return this.attributeNumber;
  }

  public T get(int i) {
    return values.get(i);
  }

  public List<T> getDistinctValues() {
    List<T> distinctValues = new ArrayList<T>();
    for (T val : values) {
      if (!distinctValues.contains(val)) {
        distinctValues.add(val);
      }
    }

    return distinctValues;
  }

  public int size() {
    return values.size();
  }

  public void add(T item) {
    values.add(item);
  }

  public void print() {
    for (T item : values) {
      System.out.print(item.toString() + " ");
    }
    System.out.println();
  }
}
