
package com.cachirulop.whereiparked.activity;

import android.app.Activity;
import android.os.Bundle;

import com.cachirulop.whereiparked.fragment.SettingsFragment;

public class SettingsActivity
        extends Activity
{
    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager ().beginTransaction ().replace (android.R.id.content,
                                                           new SettingsFragment ()).commit ();
    }
}
