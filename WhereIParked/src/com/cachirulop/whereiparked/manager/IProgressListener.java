package com.cachirulop.whereiparked.manager;

public interface IProgressListener
{
    public int getMax ();
    public void setMax (int max);
    
    public void setMessage (String msg);
    
    public void increment ();
    public void incrementBy (int count);
    public void reset ();
}
