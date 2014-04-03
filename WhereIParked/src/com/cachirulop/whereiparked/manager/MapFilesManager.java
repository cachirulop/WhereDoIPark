
package com.cachirulop.whereiparked.manager;

import java.io.File;

import com.cachirulop.whereiparked.common.Message;

public class MapFilesManager
{
    /**
     * Update the map database.
     * 
     * Synchronize the map files in then local storage with the data in the
     * database.
     * 
     * First read the new files of the local storage and add or update the of
     * them.
     * 
     * Afterwards delete of the database the non existent files on the local
     * storage.
     */
    public static void updateMapDatabase ()
    {
        updateFiles ();
        deleteFiles ();
    }

    /**
     * Update the database with the content of the path in the local storage.
     * 
     * Read the files in the configured path and if the file exist, check its
     * creation date. If it has the same date as the register in the database do
     * nothing, else update its data with the new file. If the file doesn't
     * exist insert new record in the table.
     * 
     */
    private static void updateFiles ()
    {
        File directory;

        directory = new File (SettingsManager.getMapFilesPath ());
        if (!directory.exists ()) {
            Message.showMessage ("Directory not found");
        }
        else if (!directory.canRead ()) {
            Message.showMessage ("Directory can't read");
        }
        else {
            File[] dirContent;

            dirContent = directory.listFiles ();
            for (File f : dirContent) {
                processFile (f);
            }
        }
    }

    /**
     * Process map file comparing its content with the saved data in the
     * database.
     * 
     * If the file exists in the database with the same date of the local
     * storage, then do nothing. If the file doesn't exist in the database the
     * method add it to the table with its data. If the file has different date
     * in the disk and in the database then delete it from database and add it
     * again to update its data.
     * 
     * @param f
     *            File to process
     */
    private static void processFile (File f)
    {
        
    }

    /**
     * Delete the files of the database that doesn't exist in the local storage.
     * 
     * Read the list of files in the database table and remove those that
     * doesn't exist in the local storage.
     */
    private static void deleteFiles ()
    {

    }
}
