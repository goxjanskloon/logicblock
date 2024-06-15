package github.goxjanskloon.util;
public interface Comparable<T extends Comparable<T>> extends java.lang.Comparable<T>{
    default int compareTo(T o){
        return Integer.compare(hashCode(),o.hashCode());
    }
}