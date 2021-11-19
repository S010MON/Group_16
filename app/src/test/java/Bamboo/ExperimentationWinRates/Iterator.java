package Bamboo.ExperimentationWinRates;

import Bamboo.controller.Mutable;

import java.util.Objects;

public class Iterator<T> {
    private float min;
    private float max;
    private float step;
    private int iterations;
    private Mutable<T> reference;
    private boolean empty = false;

    public Iterator(float min, float max, float step){
        this.min = min;
        this.max = max;
        this.step = step;
        this.iterations = (int)Math.ceil((max-min)/step);
    }

    public Iterator(String command){
        if(Objects.equals(command, "empty")){
            this.empty = true;
            this.min = 0;
            this.max = 0;
            this.step = 1;
            iterations = 1;
        }
        else{
            throw new IllegalArgumentException("Illegal command");
        }
    }

    public Iterator(Mutable<T> reference, float min, float max, float step){
        this.min = min;
        this.max = max;
        this.step = step;
        this.reference = reference;
        this.iterations = (int)Math.ceil((max-min)/step);
        System.out.println(this.reference + " is reference on Iterator construction");
        System.out.println("Iterations: " + this.iterations);
    }

    public float getStart(){return min;}
    public float getEnd(){return max;}
    public float getStep(){return step;}
    public int getArrayBounds(){
        return this.iterations;
    }

    public boolean isEmpty(){return empty;}
    public Mutable<T> getReference(){return this.reference;}
    public void setReference(Mutable<T> ref){
        this.reference = ref;
    }
    public void set(T val){
        this.reference.set(val);
    }
}
