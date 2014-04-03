
package com.cachirulop.whereiparked.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cachirulop.whereiparked.common.Message;

public class BluetoothBroadcastReceiver
        extends BroadcastReceiver
{

    @Override
    public void onReceive (Context context,
                           Intent intent)
    {
        String action;
        BluetoothDevice device;

        action = intent.getAction ();
        device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

        Message.showMessage ("Bluetooth broadcast receive: action: " + action +
                                     ", device: " + device.getAddress () +
                                     " - " + device.getName ());

        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals (action)) {
            Message.showMessage ("Device disconnected");
        }
    }

}
