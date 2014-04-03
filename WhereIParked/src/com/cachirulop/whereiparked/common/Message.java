package com.cachirulop.whereiparked.common;

import com.cachirulop.whereiparked.manager.ContextManager;

import android.content.Context;
import android.widget.Toast;

public class Message
{

    public static void showMessage (String msg) 
    {
        Toast toast;
        
        toast = Toast.makeText(ContextManager.getContext (), msg, Toast.LENGTH_LONG);
        toast.show();
    }
}
