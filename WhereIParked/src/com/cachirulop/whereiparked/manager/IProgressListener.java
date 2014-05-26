
package com.cachirulop.whereiparked.manager;

public interface IProgressListener
{
    int getMax ();

    void setMax (int max);

    void setMessage (String msg);

    void increment ();

    void incrementBy (int count);

    void reset ();

    void dismiss ();
}
