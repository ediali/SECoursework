/**
 * 
 */
package auctionhouse;

/**
 * @author pbj
 */
public class Money implements Comparable<Money> {
 
    private double value;
    
    private static long getNearestPence(double pounds) {
        return Math.round(pounds * 100.0);
    }
 
    private static double normalise(double pounds) {
        return getNearestPence(pounds)/100.0;
        
    }
 
    public Money(String pounds) {
        value = normalise(Double.parseDouble(pounds));
    }
    
    private Money(double pounds) {
        value = pounds;
    }
    
    /**
     * Returns a Money object that has the value equal to the sum of the value of the current Money object with the value of the input parameter Money object.
     * The input parameter must be a valid, non-null Money object.
     * @param m a valid, non-null Money object whose value is added to the current Money object.
     * @return a new Money object that contains the value of the sum.
     */
    public Money add(Money m) {
        return new Money(value + m.value);
    }
    
    /**
     * Returns a Money object that has the value equal to the value of input parameter Money object subtracted from the value of the current Money object.
     * The input parameter must be a valid non-null Money object.
     * @param m a valid, non-null Money object whose value is subtracted from the current Money object.
     * @return a new Money object that contains the value of the subtraction.
     */
    public Money subtract(Money m) {
        return new Money(value - m.value);
    }
 
    /**
     * Returns a Money object that has the value equal to an increase by a percentage dictated by the input parameter value.
     * @param percent a double whose value represents the percentage to which the current Money value is increased by.
     * @return a new Money object that contains the value of the percentage increase.
     */
    public Money addPercent(double percent) {
        return new Money(normalise(value * (1 + percent/100.0)));
    }
     
    @Override
    public String toString() {
        return String.format("%.2f", value);
        
    }
    
    /**
     * Compares the current Money object to the input parameter Money object returning an int value that has a fixed value for the cases where the value of both Money objects are equal or different.
     * @param m a valid, non-null Money object whose value is compared to the value of the current Money object.
     * @return an int that is equal to -1 when the value of the current Money object is less than the value of the input parameter Money object, 0 if the value of the current Money object is equal to the value of the input parameter Money object and 1 if the value of the current Money object is greater than the value of the input parameter Money object. 
     */
    public int compareTo(Money m) {
        return Long.compare(getNearestPence(value),  getNearestPence(m.value)); 
    }
    
    /**
     * Returns a boolean value representing whether the value of the current Money object is less than or equal to the value of the input parameter Money object. 
     * @param m a valid, non-null Money object whose value is compared to the value of the current Money object.
     * @return a boolean which is true if the value of the current Money object is less than or equal to the value of the input parameter Money object, and false if the value of the current Money object is greater than the value of the input parameter Money object.
     */
    public Boolean lessEqual(Money m) {
        return compareTo(m) <= 0;
    }
    
    /**
     * Returns a boolean value representing whether the value of the current Money object is equal to the value of a valid Money object input parameter.
     * If the input parameter is not an instance of the Money class, values are not compared.
     * @param o an object which is compared to the current Money object.
     * @return a boolean which is true if the input of the current Money object is equals to the value of the input parameter Money object,and false if the value of the current Money object is not equal to the value of the input parameter Money object or if the input parameter is not an instance of the Money class. 
     * @see Object
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money)) return false;
        Money oM = (Money) o;
        return compareTo(oM) == 0;       
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(getNearestPence(value));
    }
      

}
