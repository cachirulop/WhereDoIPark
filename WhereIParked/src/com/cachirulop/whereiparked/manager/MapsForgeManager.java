
package com.cachirulop.whereiparked.manager;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

import org.mapsforge.android.maps.DebugSettings;
import org.mapsforge.android.maps.mapgenerator.JobParameters;
import org.mapsforge.android.maps.mapgenerator.JobTheme;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorJob;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.DatabaseRenderer;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.header.FileOpenResult;
import org.mapsforge.map.reader.header.MapFileHeader;
import org.mapsforge.map.reader.header.SubFileParameter;

import android.graphics.Bitmap;

import com.cachirulop.whereiparked.common.exception.MapsForgeException;
import com.cachirulop.whereiparked.entity.MapFile;
import com.cachirulop.whereiparked.entity.MapSubfile;

public class MapsForgeManager
{
    private static Object           _lock;
    private static DatabaseRenderer _mapGenerator;
    private static MapDatabase      _mapDatabase;
    private static JobParameters    _jobParameters;
    private static DebugSettings    _debugSettings;

    static {
        _lock = new Object ();
        _mapGenerator = new DatabaseRenderer ();
        _mapDatabase = new MapDatabase ();
        _mapGenerator.setMapDatabase (_mapDatabase);

        _jobParameters = new JobParameters (new RenderTheme (),
                                            1);
        _debugSettings = new DebugSettings (false,
                                            false,
                                            false);
    }

    /**
     * Returns the map file representation of the file in the local storage.
     * 
     * It's use the mapsforge classes to read the related map information.
     * 
     * @param path
     *            Absolute path to the file with the map data
     * @return New MapFile object with the map data of the file
     */
    public static MapFile getMapFile (String path)
        throws MapsForgeException
    {
        MapFile result = null;
        FileOpenResult fileOpenResult;
        File f;
        MapDatabase mapDB;

        f = new File (path);

        mapDB = new MapDatabase ();

        fileOpenResult = mapDB.openFile (f);
        if (fileOpenResult.isSuccess ()) {
            result = new MapFile ();
            result.setFileName (f.getAbsolutePath ());
            result.setCreationDate (new Date (f.lastModified ()));

            // Read the sufiles data
            MapFileHeader header;

            header = mapDB.getMapFileHeader ();
            for (byte i = header.getZoomLevelMinimum (); i < header.getZoomLevelMaximum (); i++) {
                MapSubfile current;
                SubFileParameter srcParam;

                srcParam = header.getSubFileParameter (header.getQueryZoomLevel (i));
                current = getMapSubfile (header.getQueryZoomLevel (i),
                                         srcParam);

                result.getSubFiles ().add (current);
            }
        }
        else {
            throw new MapsForgeException (fileOpenResult.getErrorMessage ());
        }

        return result;
    }

    /**
     * Creates a new MapSubfile object from the data of the SubFileParameter
     * read from the map file for a specified zoom level.
     * 
     * @param tileZoomLevel
     *            Zoom level of the data
     * @param subFileParameter
     *            Subfile map data of the file map
     * @return New MapSubfile object with the SubfileParameter values
     */
    private static MapSubfile getMapSubfile (byte tileZoomLevel,
                                             SubFileParameter subFileParameter)
    {
        MapSubfile result;

        result = new MapSubfile ();
        result.setZoomLevel (tileZoomLevel);

        if (tileZoomLevel > subFileParameter.baseZoomLevel) {
            int zoomLevelDifference = tileZoomLevel -
                                      subFileParameter.baseZoomLevel;

            result.setBoundsTileLeft (subFileParameter.boundaryTileLeft << zoomLevelDifference);
            result.setBoundsTileTop (subFileParameter.boundaryTileTop << zoomLevelDifference);
            result.setBoundsTileRight (subFileParameter.boundaryTileRight << zoomLevelDifference);
            result.setBoundsTileBottom (subFileParameter.boundaryTileBottom << zoomLevelDifference);
        }
        else if (tileZoomLevel < subFileParameter.baseZoomLevel) {
            int zoomLevelDifference = subFileParameter.baseZoomLevel -
                                      tileZoomLevel;

            result.setBoundsTileLeft (subFileParameter.boundaryTileLeft >>> zoomLevelDifference);
            result.setBoundsTileTop (subFileParameter.boundaryTileTop >>> zoomLevelDifference);
            result.setBoundsTileRight (subFileParameter.boundaryTileRight >>> zoomLevelDifference);
            result.setBoundsTileBottom (subFileParameter.boundaryTileBottom >>> zoomLevelDifference);
        }
        else {
            result.setBoundsTileLeft (subFileParameter.boundaryTileLeft);
            result.setBoundsTileTop (subFileParameter.boundaryTileTop);
            result.setBoundsTileRight (subFileParameter.boundaryTileRight);
            result.setBoundsTileBottom (subFileParameter.boundaryTileBottom);
        }

        return result;
    }

    /**
     * Returns the tile bitmap of a specified coordinates.
     * 
     * To get the bitmap query the database to get the appropiate map files and
     * generates the bitmap with its data.
     * 
     * If the tile position has more than one file merge the contents of the
     * different bitmap to create the appropiate map.
     * 
     * @param x
     *            X coordinate of the tile to generate the bitmap
     * @param y
     *            Y coordinate of the tile to generate the bitmap
     * @param zoom
     *            Zoom level of the tile
     * @return Bitmap representing the tile of the position indicates with the
     *         zoom level specified
     */
    public static Bitmap getBitmap (int x,
                                    int y,
                                    int zoom)
        throws MapsForgeException
    {
        Bitmap result = null;
        ArrayList<MapFile> lstFile;

        lstFile = MapFilesManager.getMapFilesFromTileCoords (x,
                                                             y,
                                                             zoom);

        for (MapFile f : lstFile) {
            Bitmap tmp;

            tmp = getBitmapFromFile (f,
                                     x,
                                     y,
                                     zoom);

            result = mergeBitmaps (tmp,
                                   result);
        }

        return result;
    }

    /**
     * Construct the bitmap representation of the specified coordinates from the
     * file idicated.
     * 
     * @param f
     *            Map file uses to render the bitmap
     * @param x
     *            X coordinate to generate the bitmap
     * @param y
     *            Y coordinate to generate the bitmap
     * @param zoom
     *            Zoom level to generate the bitmap
     * @return New bitmap with the map portion of the coordinates
     * @throws MapsForgeException
     *             If there is any problem with the map generation
     */
    private static Bitmap getBitmapFromFile (MapFile f,
                                             int x,
                                             int y,
                                             int zoom)
        throws MapsForgeException
    {
        Bitmap result;
        FileOpenResult openResult;

        synchronized (_lock) {
            _mapDatabase.closeFile ();

            openResult = _mapDatabase.openFile (new File (f.getFileName ()));

            if (openResult.isSuccess ()) {
                MapGeneratorJob job;
                org.mapsforge.core.Tile mfTile;

                mfTile = new org.mapsforge.core.Tile (x,
                                                      y,
                                                      (byte) zoom);
                job = new MapGeneratorJob (mfTile,
                                           "wip",
                                           _jobParameters,
                                           _debugSettings);

                result = Bitmap.createBitmap (org.mapsforge.core.Tile.TILE_SIZE,
                                              org.mapsforge.core.Tile.TILE_SIZE,
                                              Bitmap.Config.RGB_565);

                if (_mapGenerator.executeJob (job,
                                              result)) {
                    return result;
                }
                else {
                    return null;
                }
            }
            else {
                throw new MapsForgeException (openResult.getErrorMessage ());
            }
        }
    }

    /**
     * Merge two bitmaps in one.
     * 
     * To merge the bitmaps exclude the pixels with the values -33 and -1. This
     * values are knewn by debug, so it can be an error.
     * 
     * @param img1
     *            First image to merge
     * @param img2
     *            Second image to merge
     * @return New bitmap with the representative image parts of the two
     *         received parameters
     */
    private static Bitmap mergeBitmaps (Bitmap img1,
                                        Bitmap img2)
    {
        if ((img1 == null) && (img2 == null)) {
            return null;
        }
        else if (img1 == null) {
            return img2;
        }
        else if (img2 == null) {
            return img1;
        }
        else {
            ByteBuffer b = null;
            byte[] buffer1;
            byte[] buffer2;

            b = ByteBuffer.allocate (img1.getByteCount ());

            img1.copyPixelsToBuffer (b);
            buffer1 = b.array ();

            b = ByteBuffer.allocate (img2.getByteCount ());

            img2.copyPixelsToBuffer (b);
            buffer2 = b.array ();

            for (int i = 0; i < buffer2.length; i++) {
                if (buffer2 [i] != -33 && buffer2 [i] != -1) {
                    buffer1 [i] = buffer2 [i];
                }
            }

            Bitmap result;

            result = Bitmap.createBitmap (org.mapsforge.core.Tile.TILE_SIZE,
                                          org.mapsforge.core.Tile.TILE_SIZE,
                                          Bitmap.Config.RGB_565);

            b = ByteBuffer.wrap (buffer1);
            result.copyPixelsFromBuffer (b);

            return result;
        }
    }

    /**
     * For some reason, if we just use InternalRenderTheme.OSMARENDER, it fails
     * to work (the XML parser gets a NULL as the input stream). It must be
     * something to do with class loading in dependent libraries. Anyway, this
     * code properly opens the resource in the MapsForge jar as an input stream.
     */
    static private class RenderTheme
            implements JobTheme
    {
        private static final long   serialVersionUID = 1L;
        static private final String path             = "/org/mapsforge/android/maps/rendertheme/osmarender/osmarender.xml";

        @Override
        public InputStream getRenderThemeAsStream ()
        {
            return getClass ().getResourceAsStream (path);
        }
    }
}
