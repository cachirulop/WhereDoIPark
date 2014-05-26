
package com.cachirulop.whereiparked.common;

import android.os.Handler;

public class MessageHandler
        extends Handler
{
    public void postMessage (final String msg)
    {
        post (new Runnable ()
        {
            public void run ()
            {
                Message.showMessage (msg);
            }
        });
    }
}
