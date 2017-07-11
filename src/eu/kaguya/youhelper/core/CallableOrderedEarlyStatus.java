package eu.kaguya.youhelper.core;

import java.util.concurrent.Callable;

public interface CallableOrderedEarlyStatus<V> extends Callable<V>, Comparable<V>, EarlyStatus<V>, Cancellable{

}
