
package com.cachirulop.whereiparked.common;

import android.widget.Toast;

import com.cachirulop.whereiparked.manager.ContextManager;

public class Message
{

    public static void showMessage (String msg)
    {
        Toast toast;

        toast = Toast.makeText (ContextManager.getContext (),
                                msg,
                                Toast.LENGTH_LONG);
        toast.show ();
    }
}
