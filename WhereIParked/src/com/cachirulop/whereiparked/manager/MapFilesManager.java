
package com.cachirulop.whereiparked.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cachirulop.whereiparked.common.Message;
import com.cachirulop.whereiparked.data.WhereIParkedDataHelper;
import com.cachirulop.whereiparked.entity.MapFile;

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
     * Delete the files of the database that doesn't exist in the local storage.
     * 
     * Read the list of files in the database table and remove those that
     * doesn't exist in the local storage.
     */
    private static void deleteFiles ()
    {
        ArrayList<MapFile> lstFiles;
        
        lstFiles = getAllMapFiles ();
        
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
        MapFile dbFile;
        String path;

        path = f.getAbsolutePath ();

        dbFile = MapFilesManager.getMapFile (path);
        if (dbFile == null) {
            insertMapFile (MapsForgeManager.getInstance ().getMapFile (path));
        }
        else if (!dbFile.getCreationDate ().equals (new Date (f.lastModified ()))) {
            deleteMapFile (dbFile);
            insertMapFile (MapsForgeManager.getInstance ().getMapFile (path));
        }
    }

    /**
     * Returns the file from the database searching by file name.
     * 
     * @param path
     *            Full path to the searched file
     */
    private static MapFile getMapFile (String path)
    {
        SQLiteDatabase db = null;
        Cursor c = null;

        try {
            db = new WhereIParkedDataHelper ().getReadableDatabase ();

            c = db.query ("map_files",
                          null,
                          "file_name = ?",
                          new String[] { path },
                          null,
                          null,
                          null);

            if (c != null && c.moveToFirst ()) {
                return createMapFile (c);
            }
            else {
                return null;
            }
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
            }
        }
    }
    
    /**
     * Returns all the map files of the database
     * 
     * @return List of objects of the Movement class
     */
    public static ArrayList<MapFile> getAllMapFiles ()
    {
        Cursor c = null;
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getReadableDatabase ();

            c = db.query ("map_files",
                          null,
                          null,
                          null,
                          null,
                          null,
                          null);

            return createMapFileList (c);
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
            }
        }
    }    

    /**
     * Insert new map file data into the table map_files in the database
     * 
     * @param f
     *            Data with the new map file
     * @return The object received as parameter with the field MapFileId filled
     *         with the database identifier
     */
    private static MapFile insertMapFile (MapFile f)
    {
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getWritableDatabase ();

            ContentValues values;

            values = new ContentValues ();
            values.put ("id_map_file",
                        f.getIdMapFile ());
            values.put ("file_name",
                        f.getFileName ());
            values.put ("creation_date",
                        f.getCreationDate ().getTime ());
            values.put ("bounds_north",
                        f.getBoundsNorth ());
            values.put ("bounds_south",
                        f.getBoundsSouth ());
            values.put ("bounds_east",
                        f.getBoundsEast ());
            values.put ("bounds_west",
                        f.getBoundsWest ());
            values.put ("start_zoom",
                        f.getStartZoom ());

            db.insert ("map_files",
                       null,
                       values);

            f.setIdMapFile (getLastId ());

            return f;
        }
        finally {
            if (db != null) {
                db.close ();
            }
        }
    }
    
    /**
     * Delete a map file data from the database
     * 
     * @param m
     *            Map file data to be deleted
     */
    public static void deleteMapFile (MapFile f)
    {
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getWritableDatabase ();

            db.delete ("map_files",
                       "id_map_file = ?",
                       new String[] { Long.toString (f.getIdMapFile ()) });
        }
        finally {
            if (db != null) {
                db.close ();
            }
        }
    }
    
    /**
     * Gets the maximum identifier of the map files table
     * 
     * @return Last id in the map files table
     */
    private static long getLastId ()
    {
        return new WhereIParkedDataHelper ().getLastId ("map_files");
    }

    /**
     * Read the data from the database (Cursor) and creates a list of map files
     * 
     * @param c
     *            Cursor with the database data
     * @return New list of object of MapFile class.
     */
    private static ArrayList<MapFile> createMapFileList (Cursor c)
    {
        ArrayList<MapFile> result;

        result = new ArrayList<MapFile> ();

        if (c != null) {
            if (c.moveToFirst ()) {
                do {
                    result.add (createMapFile (c));
                }
                while (c.moveToNext ());
            }
        }

        return result;
    }
    
    /**
     * Create a new object of the MapFile class from a row of the database
     * filled in the cursor.
     * 
     * @param c
     *            Data of the map file readed from the database
     * @return New MapFile object with the database data.
     */
    private static MapFile createMapFile (Cursor c)
    {
        MapFile result;

        result = new MapFile ();
        result.setIdMapFile (c.getLong (c.getColumnIndex ("id_map_file")));
        result.setFileName (c.getString (c.getColumnIndex ("file_name")));
        result.setCreationDate (new Date (c.getLong (c.getColumnIndex ("creation_date"))));
        result.setBoundsEast (c.getInt (c.getColumnIndex ("bounds_east")));
        result.setBoundsWest (c.getInt (c.getColumnIndex ("bounds_west")));
        result.setBoundsNorth (c.getInt (c.getColumnIndex ("bounds_north")));
        result.setBoundsSouth (c.getInt (c.getColumnIndex ("bounds_south")));
        result.setStartZoom (c.getInt (c.getColumnIndex ("start_zoom")));

        return result;
    }
}
