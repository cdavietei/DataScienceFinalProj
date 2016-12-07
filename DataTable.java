import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DataTable<T> {
  protected List<Attribute<T>> attributes;
  protected Attribute<T> targetAttribute;

  public DataTable(List<Attribute<T>> attributes, Attribute<T> targetAttribute) {
    this.attributes = attributes;
    this.targetAttribute = targetAttribute;
  }

  public boolean attributesEmpty() {
    return attributes.isEmpty();
  }

  public boolean allTargetsSame() {
    return targetAttribute.allEqual();
  }

  // returns the most commonly occurring value of the targetAttribute
  public T targetMajority() {
    HashMap<T,Integer> supportHash = getSupportHash(targetAttribute);

    T majorityValue = null;

    for (T key : supportHash.keySet()) {
      if (majorityValue == null || (supportHash.get(key) > supportHash.get(majorityValue))) {
        majorityValue = key;
      }
    }

    return majorityValue;
  }

  public HashMap<T,Integer> getSupportHash(Attribute<T> attribute) {
    HashMap<T,Integer> supportHash = new HashMap<T,Integer>();

    for (T val : attribute.getValues()) {
      Integer count = supportHash.get(val);
      if (count == null) {
        supportHash.put(val, 1);
      } else {
        supportHash.put(val, count + 1);
      }
    }

    return supportHash;
  }

  // returns the attribute x which maximizes information gain of result given x
  public Attribute<T> maxInfoGainAttribute() {
    Attribute<T> maxInfoGainAttribute = attributes.get(0);
    Double maxInfoGain = informationGain(targetAttribute, attributes.get(0));

    for (Attribute<T> attribute : attributes) {
      Double currentInfoGain = informationGain(targetAttribute, attribute);
      if (currentInfoGain > maxInfoGain) {
        maxInfoGainAttribute = attribute;
        maxInfoGain = currentInfoGain;
      }
    }

    return maxInfoGainAttribute;
  }

  /**
   * The information gain of Y given X is the following,
   * IG(Y|X) = H(Y) - H(Y|X)
   *
   * @param attributeY - the attribute for which IG is being calculated
   * @param attributeX - the given attribute
   */
  public Double informationGain(Attribute<T> attributeY, Attribute<T> attributeX) {
    return entropyOf(attributeY) - conditionalEntropyOf(attributeY, attributeX);
  }

  /**
   * The entropy of an attribute is the following,
   * H(X) = -1 * sum( P(Xi) log_2 P(Xi) )
   * where each Xi is a particular value of X and P(Xi) is the probability of
   * that value
   */
  public Double entropyOf(Attribute<T> attribute) {
    HashMap<T,Double> probabilityHash = getProbabilities(attribute);

    Double sum = 0.0;
    for (T key : probabilityHash.keySet()) {
      sum += entropyCalc(probabilityHash.get(key));
    }

    return sum;
  }

  /**
   * The conditional entropy of Y, given X, is the following,
   * H(Y|X) = -1 * sum(P(Xi) entropyOf(Yi))
   * where Yi is only entries of Y where X = Xi
   */
  public Double conditionalEntropyOf(Attribute<T> attributeY, Attribute<T> attributeX) {
    HashMap<T,Double> xProbabilities = getProbabilities(attributeX);

    Double sum = 0.0;
    for (T xValue : xProbabilities.keySet()) {
      Attribute<T> conditionalYVals = filterAttribute(attributeY, attributeX, xValue);
      sum += xProbabilities.get(xValue) * entropyOf(conditionalYVals);
    }

    return sum;
  }

  public HashMap<T,Double> getProbabilities(Attribute<T> attribute) {
    HashMap<T,Integer> supportHash = getSupports(attribute);
    HashMap<T,Double> probabilityHash = new HashMap<T,Double>();

    for (T key : supportHash.keySet()) {
      Double probability = (double) supportHash.get(key) / attribute.size();
      probabilityHash.put(key, probability);
    }

    return probabilityHash;
  }

  public HashMap<T,Integer> getSupports(Attribute<T> attribute) {
    HashMap<T,Integer> supportHash = new HashMap<T,Integer>();

    for (T value : attribute.getValues()) {
      Integer count = supportHash.get(value);
      if (count == null) {
        supportHash.put(value, 1);
      } else {
        supportHash.put(value, count + 1);
      }
    }

    return supportHash;
  }

  public Double entropyCalc(Double probability) {
    return -1 * probability * (Math.log(probability) / Math.log(2));
  }

  // returns only values of attribute1 where attribute2 is equal to attr2Value
  @SuppressWarnings("unchecked")
  public Attribute<T> filterAttribute(Attribute<T> attribute1,
                                      Attribute<T> attribute2,
                                      T attr2Value) {
    List<T> filteredAttributeList = new ArrayList<T>();

    for (int i = 0; i < attribute1.size(); i++) {
      // if ith entry of attribute2 is equal to attr2Value, add ith entry of attribute1
      if (attribute2.get(i).equals(attr2Value)) {
        filteredAttributeList.add(attribute1.get(i));
      }
    }

    return new Attribute(attribute1.getAttributeNumber(), filteredAttributeList);
  }

  /**
   * Returns a subset of the data set only containing rows where column
   * filterAttributeColNum equals filterVal. The column on which the table is
   * filtered is removed from the returned DataTable
   */
   @SuppressWarnings("unchecked")
  public DataTable<T> filterAttributes(int attrNumber, T attrValue) {
    // initialize the list of filtered attributes to be returned
    List<Attribute<T>> filteredAttributeList = new ArrayList<Attribute<T>>();
    for (int i = 0; i < attributes.size(); i++) {
      filteredAttributeList.add(new Attribute<T>(attributes.get(i).getAttributeNumber()));
    }
    // create the new attribute to contain the filtered targetAttribute
    Attribute<T> filteredTargetAttribute = new Attribute<T>(targetAttribute.getAttributeNumber());

    Attribute<T> filterAttribute = attributes.get(attrNumber);
    for (int i = 0; i < filterAttribute.size(); i++) {
      if (filterAttribute.get(i).equals(attrValue)) {
        // copy the values from the original attributes to the new filtered list
        for (int j = 0; j < attributes.size(); j++) {
          Attribute<T> originalAttribute = attributes.get(j);
          Attribute<T> newAttribute = filteredAttributeList.get(j);
          newAttribute.add(originalAttribute.get(i));
        }
        // add the corresponding targetAttribute value to the new targetAttribute
        filteredTargetAttribute.add(targetAttribute.get(i));
      }
    }

    // remove the attribute use to filter
    filteredAttributeList.remove(attrNumber);

    return new DataTable(filteredAttributeList, filteredTargetAttribute);
  }

  public void printTable() {
    for (int i = 0; i < targetAttribute.size(); i++) {
      for (Attribute<T> attr: attributes) {
        System.out.print(attr.getValues().get(i).toString() + " ");
      }
      System.out.println(targetAttribute.getValues().get(i).toString());
    }
  }

  public int indexOfAttribute(int attrNumber) {
    for (int i = 0; i < attributes.size(); i++) {
      if (attributes.get(i).getAttributeNumber() == attrNumber) {
        return i;
      }
    }
    return 0;
  }
}