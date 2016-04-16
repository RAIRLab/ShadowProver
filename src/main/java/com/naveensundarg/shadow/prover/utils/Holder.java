package com.naveensundarg.shadow.prover.utils;

/**
 * Created by naveensundarg on 4/15/16.
 */
public class Holder<T>{


    private Holder(T value){
        this.value = value;
    }

    public static <T> Holder<T> holderWith(T t){
        return new Holder<>(t);
    }


    private T value;

    public T get(){
        return value;
    }

    public void set(T value){
        this.value = value;
    }
}
