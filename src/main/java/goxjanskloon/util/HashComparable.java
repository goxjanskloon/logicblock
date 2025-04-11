package goxjanskloon.util;
/**
 * @author goxjanskloon
 */
public interface HashComparable extends Comparable<HashComparable>{
    @Override default int compareTo(HashComparable o){
        return Integer.compare(hashCode(),o.hashCode());
    }
}