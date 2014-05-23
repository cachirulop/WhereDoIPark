
package com.cachirulop.whereiparked.manager;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cachirulop.whereiparked.data.WhereIParkedDataHelper;
import com.cachirulop.whereiparked.entity.MapSubfile;

public class MapSubfilesManager
{
    private static final String CONST_MAP_SUBFILES_TABLE_NAME = "map_subfiles";

    /**
     * Insert new map subfile data into the table map_subfiles in the database
     * 
     * @param f
     *            Data with the new map subfile
     * @return The object received as parameter with the field MapSubfileId
     *         filled with the database identifier
     */
    public static MapSubfile insertMapSubfile (MapSubfile f)
    {
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getWritableDatabase ();

            return insertMapSubfile (db,
                                     f);
        }
        finally {
            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Insert new map subfile data into the table map_subfiles in the database
     * 
     * @param f
     *            Data with the new map subfile
     * @return The object received as parameter with the field MapSubfileId
     *         filled with the database identifier
     */
    public static MapSubfile insertMapSubfile (SQLiteDatabase db,
                                               MapSubfile f)
    {
        ContentValues values;

        values = new ContentValues ();
        values.put ("id_map_file",
                    f.getIdMapFile ());
        values.put ("bounds_tile_left",
                    f.getBoundsTileLeft ());
        values.put ("bounds_tile_right",
                    f.getBoundsTileRight ());
        values.put ("bounds_tile_top",
                    f.getBoundsTileTop ());
        values.put ("bounds_tile_bottom",
                    f.getBoundsTileBottom ());
        values.put ("zoom_level",
                    f.getZoomLevel ());

        db.insert (MapSubfilesManager.CONST_MAP_SUBFILES_TABLE_NAME,
                   null,
                   values);

        f.setIdMapFile (getLastId ());

        return f;
    }

    /**
     * Delete a map subfile data from the database
     * 
     * @param m
     *            Map subfile data to be deleted
     */
    public static void deleteMapSubfile (MapSubfile f)
    {
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getWritableDatabase ();

            deleteMapSubfile (db,
                              f);
        }
        finally {
            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Delete a map subfile data from the database
     * 
     * @param m
     *            Map subfile data to be deleted
     */
    public static void deleteMapSubfile (SQLiteDatabase db,
                                         MapSubfile f)
    {
        db.delete (MapSubfilesManager.CONST_MAP_SUBFILES_TABLE_NAME,
                   "id_map_subfile = ?",
                   new String[] { Long.toString (f.getIdMapSubfile ()) });
    }

    /**
     * Delete all the map subfiles from the database
     * 
     * @param m
     *            Map subfile data to be deleted
     */
    public static void deleteAllMapSubfiles ()
    {
        SQLiteDatabase db = null;

        try {
            db = new WhereIParkedDataHelper ().getWritableDatabase ();

            deleteAllMapSubfiles (db);
        }
        finally {
            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Delete all the map subfiles from the database
     * 
     * @param m
     *            Map subfile data to be deleted
     */
    public static void deleteAllMapSubfiles (SQLiteDatabase db)
    {
        db.delete (MapSubfilesManager.CONST_MAP_SUBFILES_TABLE_NAME,
                   null,
                   null);

        // Reset the sequence to stop growing
        db.delete ("sqlite_sequence",
                   "name = ?",
                   new String[] { MapSubfilesManager.CONST_MAP_SUBFILES_TABLE_NAME });
    }

    public static ArrayList<MapSubfile> getMapSubfilesFromFile (long idFile)
    {
        SQLiteDatabase db = null;
        Cursor c = null;

        try {
            db = new WhereIParkedDataHelper ().getReadableDatabase ();

            c = db.query (MapSubfilesManager.CONST_MAP_SUBFILES_TABLE_NAME,
                          null,
                          "id_map_file = ?",
                          new String[] { Long.toString (idFile) },
                          null,
                          null,
                          null);

            return createMapSubfileList (c);
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
     * Gets the maximum identifier of the map subfiles table
     * 
     * @return Last id in the map subfiles table
     */
    private static long getLastId ()
    {
        return new WhereIParkedDataHelper ().getLastId (MapSubfilesManager.CONST_MAP_SUBFILES_TABLE_NAME);
    }

    /**
     * Read the data from the database (Cursor) and creates a list of map
     * subfiles
     * 
     * @param c
     *            Cursor with the database data
     * @return New list of object of MapSubfile class.
     */
    private static ArrayList<MapSubfile> createMapSubfileList (Cursor c)
    {
        ArrayList<MapSubfile> result;

        result = new ArrayList<MapSubfile> ();

        if (c != null) {
            if (c.moveToFirst ()) {
                do {
                    result.add (createMapSubfile (c));
                }
                while (c.moveToNext ());
            }
        }

        return result;
    }

    /**
     * Create a new object of the MapSubfile class from a row of the database
     * filled in the cursor.
     * 
     * @param c
     *            Data of the map subfile read from the database
     * @return New MapSubfile object with the database data.
     */
    private static MapSubfile createMapSubfile (Cursor c)
    {
        MapSubfile result;

        result = new MapSubfile ();
        result.setIdMapSubfile (c.getLong (c.getColumnIndex ("id_map_subfile")));
        result.setIdMapFile (c.getLong (c.getColumnIndex ("id_map_file")));
        result.setBoundsTileLeft (c.getInt (c.getColumnIndex ("bounds_tile_left")));
        result.setBoundsTileRight (c.getInt (c.getColumnIndex ("bounds_tile_right")));
        result.setBoundsTileTop (c.getInt (c.getColumnIndex ("bounds_tile_top")));
        result.setBoundsTileBottom (c.getInt (c.getColumnIndex ("bounds_tile_bottom")));
        result.setZoomLevel (c.getInt (c.getColumnIndex ("zoom_level")));

        return result;
    }

}
