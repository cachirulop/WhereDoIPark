package com.cachirulop.whereiparked.manager;

import android.app.ProgressDialog;

public class ProgressDialogListener
    implements IProgressListener
{
    private ProgressDialog _dialog;
    
    public ProgressDialogListener (ProgressDialog dialog) 
    {
        _dialog = dialog;
    }

    @Override
    public int getMax ()
    {
        return _dialog.getMax ();
    }

    @Override
    public void setMax (int max)
    {
        _dialog.setMax (max);
    }

    @Override
    public void setMessage (String msg)
    {
        _dialog.setMessage (msg);
    }

    @Override
    public void increment ()
    {
        incrementBy (1);
    }

    @Override
    public void incrementBy (int count)
    {
        _dialog.incrementProgressBy (count);
    }

    @Override
    public void reset ()
    {
        _dialog.setProgress (0);
    }
}
