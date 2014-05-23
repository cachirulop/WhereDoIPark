
package com.cachirulop.whereiparked.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cachirulop.whereiparked.activity.MainActivity;
import com.cachirulop.whereiparked.common.Message;

public class ConnectivityBroadcastReceiver
        extends BroadcastReceiver
{
    MainActivity _parent;

    public ConnectivityBroadcastReceiver (MainActivity parent)
    {
        _parent = parent;
    }

    @Override
    public void onReceive (Context context,
                           Intent intent)
    {
        String action;

        action = intent.getAction ();

        Message.showMessage ("Connection state change: action: " + action);

        _parent.updateMapMode ();
    }
}
