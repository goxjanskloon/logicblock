package io.goxjanskloon.util;
public interface HashComparable extends Comparable<HashComparable>{
    default int compareTo(HashComparable o){
        return Integer.compare(hashCode(),o.hashCode());
    }
}