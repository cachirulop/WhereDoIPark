
package com.cachirulop.whereiparked.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.cachirulop.whereiparked.R;
import com.cachirulop.whereiparked.common.Message;
import com.cachirulop.whereiparked.common.MessageHandler;
import com.cachirulop.whereiparked.common.exception.ConfigurationException;
import com.cachirulop.whereiparked.common.exception.WhereIParkedException;
import com.cachirulop.whereiparked.data.WhereIParkedDataHelper;
import com.cachirulop.whereiparked.entity.MapFile;

public class MapFilesManager
{
    private static final String CONST_MAP_FILES_TABLE_NAME = "map_files";
    
    /**
     * Update the map database.
     * 
     * Synchronize the map files in then local storage with the data in the
     * database.
     * 
     * Delete all the files in the database and then read the content of the
     * configured directory an save the data of the local files in the database.
     * 
     * The work is done in a new thread.
     */
    public static void updateMapDatabase (final IProgressListener listener)
    {
        Thread th;
        final MessageHandler h;

        h = new MessageHandler ();

        th = new Thread (new Runnable ()
        {
            @Override
            public void run ()
            {
                try {
                    deleteAllDatabaseMapFiles ();
                    readFilesToDatabase (listener,
                                         h);

                    listener.dismiss ();
                }
                catch (final Exception e) {
                    listener.dismiss ();

                    h.postMessage (e.getMessage ());
                }
            }
        });

        th.start ();
    }

    /**
     * Read the files of the local storage and save its data in the database.
     * 
     * @param listener
     *            Object to receive the progress of the operation
     * @param handler
     *            Handler to write messages to the UI in the main thread
     */
    private static void readFilesToDatabase (IProgressListener listener,
                                             MessageHandler handler)
        throws ConfigurationException
    {
        File directory;

        listener.setMessage (ContextManager.getString (R.string.mfm_updatingFiles));

        directory = new File (SettingsManager.getMapFilesPath ());
        if (!directory.exists ()) {
            throw new ConfigurationException (ContextManager.getString (R.string.mfm_directoryNotFound,
                                                                        directory.getAbsolutePath ()));
        }
        else if (!directory.canRead ()) {
            throw new ConfigurationException (ContextManager.getString (R.string.mfm_directoryCantRead,
                                                                        directory.getAbsolutePath ()));
        }
        else {
            listener.reset ();

            processDirectory (listener,
                              handler,
                              directory);
        }
    }

    /**
     * Process the files and directories of the specified directory calling to
     * {@link processFile} method.
     * 
     * @param listener
     *            Listener to show the progress of the operation
     * @param dir
     *            Directory with the files and directories to process
     */
    private static void processDirectory (IProgressListener listener,
                                          MessageHandler handler,
                                          File dir)
    {
        File[] dirContent;

        dirContent = dir.listFiles ();
        listener.setMax (dirContent.length);

        for (File f : dirContent) {
            Log.v ("MapFilesManager",
                   "File to process: " + f.getAbsolutePath ());

            listener.increment ();

            try {
                if (f.isFile ()) {
                    processFile (f);
                }
                else if (f.isDirectory () &&
                         !(f.getName ().equals (".") || f.getName ().equals (".."))) {
                    processDirectory (listener,
                                      handler,
                                      f);
                }
            }
            catch (WhereIParkedException e) {
                handler.postMessage (e.getMessage ());
            }
        }
    }

    /**
     * Process the content of a local file with mapsforge library and save its data in
     * the database.
     * 
     * @param f
     *            File to process
     */
    private static void processFile (File f)
        throws WhereIParkedException
    {
        MapFile dbFile;
        MapFile sdFile;

        String path;

        path = f.getAbsolutePath ();

        try {
            dbFile = MapFilesManager.getMapFile (path);
            sdFile = MapsForgeManager.getInstance ().getMapFile (path);
            if (dbFile == null) {
                insertMapFile (sdFile);
            }
        }
        catch (Exception e) {
            throw new WhereIParkedException (ContextManager.getString (R.string.mfm_errorProcessingFile,
                                                                       path,
                                                                       e.getMessage ()));
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

            c = db.query (MapFilesManager.CONST_MAP_FILES_TABLE_NAME,
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
     * @return List of objects of the MapFile class
     */
    public static ArrayList<MapFile> getAllMapFiles ()
    {
        Cursor c = null;
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getReadableDatabase ();

            c = db.query (MapFilesManager.CONST_MAP_FILES_TABLE_NAME,
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

            db.insert (MapFilesManager.CONST_MAP_FILES_TABLE_NAME,
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
    public static void deleteDatabaseMapFile (MapFile f)
    {
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getWritableDatabase ();

            db.delete (MapFilesManager.CONST_MAP_FILES_TABLE_NAME,
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
     * Delete all the map files from the database
     * 
     * @param m
     *            Map file data to be deleted
     */
    public static void deleteAllDatabaseMapFiles ()
    {
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getWritableDatabase ();

            db.delete (MapFilesManager.CONST_MAP_FILES_TABLE_NAME,
                       null,
                       null);
            
            // Reset the sequence to stop growing
            db.delete ("sqlite_sequence",
                       "name = ?",
                       new String[] { MapFilesManager.CONST_MAP_FILES_TABLE_NAME });
            
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
        return new WhereIParkedDataHelper ().getLastId (MapFilesManager.CONST_MAP_FILES_TABLE_NAME);
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
