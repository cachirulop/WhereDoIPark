package com.cachirulop.whereiparked.common;

import android.content.Context;
import android.widget.Toast;

public class Message
{

    public static void showMessage (Context ctx, String msg) 
    {
        Toast toast;
        
        toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        toast.show();
    }
}
