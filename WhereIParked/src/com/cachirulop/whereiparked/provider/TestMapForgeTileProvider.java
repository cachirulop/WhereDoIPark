
package com.cachirulop.whereiparked.provider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.mapsforge.android.maps.DebugSettings;
import org.mapsforge.android.maps.mapgenerator.JobParameters;
import org.mapsforge.android.maps.mapgenerator.JobTheme;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorJob;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.DatabaseRenderer;
import org.mapsforge.core.BoundingBox;
import org.mapsforge.core.MercatorProjection;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.header.FileOpenResult;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

public class TestMapForgeTileProvider
        implements TileProvider
{
    private DatabaseRenderer _mapGenerator;
    private MapDatabase      _mapDatabase;
    private JobParameters    _jobParameters;
    private DebugSettings    _debugSettings;

    // private static final int BUFFER_SIZE = 16 * 1024;

    public TestMapForgeTileProvider ()
    {
        _mapGenerator = new DatabaseRenderer ();
        _mapDatabase = new MapDatabase ();
        _mapGenerator.setMapDatabase (_mapDatabase);

        _jobParameters = new JobParameters (new RenderTheme (),
                                            1);
        _debugSettings = new DebugSettings (false,
                                            false,
                                            false);
    }

    @Override
    public synchronized Tile getTile (int x,
                                      int y,
                                      int zoom)
    {
        org.mapsforge.core.Tile mfTile;
        Bitmap tileBitmap;
        FileOpenResult fileOpenResult;

        _mapDatabase.closeFile ();

        // TODO: load the appropriate map file
        fileOpenResult = _mapDatabase.openFile (new File ("/storage/emulated/legacy/maps/spain.map"));
        // fileOpenResult = _mapDatabase.openFile (new File
        // ("/storage/emulated/legacy/maps/france/rhone-alpes.map"));
        // fileOpenResult = _mapDatabase.openFile (new File
        // ("/storage/emulated/legacy/maps/norway.map"));
        if (fileOpenResult.isSuccess ()) {
            mfTile = new org.mapsforge.core.Tile (x,
                                                  y,
                                                  (byte) zoom);

            MapGeneratorJob mapGeneratorJob = new MapGeneratorJob (mfTile,
                                                                   "wip",
                                                                   _jobParameters,
                                                                   _debugSettings);

            double startX;
            double startY;

            startX = MercatorProjection.longitudeToTileX (_mapDatabase.getMapFileInfo ().boundingBox.getMinLongitude (),
                                                          _mapDatabase.getMapFileInfo ().startZoomLevel);
            startY = MercatorProjection.latitudeToTileY (_mapDatabase.getMapFileInfo ().boundingBox.getMinLatitude (),
                                                         _mapDatabase.getMapFileInfo ().startZoomLevel);

            int west;
            int east;
            int south;
            int north;
            BoundingBox mapBounds;
            final boolean oob;

            mapBounds = _mapDatabase.getMapFileInfo ().boundingBox;

            west = lon2XTile (mapBounds.getMinLongitude (),
                              zoom);
            east = lon2XTile (mapBounds.getMaxLongitude (),
                              zoom);
            south = lat2YTile (mapBounds.getMinLatitude (),
                               zoom);
            north = lat2YTile (mapBounds.getMaxLatitude (),
                               zoom);

            oob = (x < west) || (x > east) || (y < north) || (y > south);

            Log.v ("MapsForgeTileProvider",
                   "Map: " + _mapDatabase.getMapFileInfo ().projectionName);
            Log.v ("MapsForgeTileProvider",
                   "Map: " + _mapDatabase.getMapFileInfo ().startZoomLevel);
            Log.v ("MapsForgeTileProvider",
                   "Map: " +
                           _mapDatabase.getMapFileInfo ().startPosition.getLatitude ());
            Log.v ("MapsForgeTileProvider",
                   "Map: " +
                           _mapDatabase.getMapFileInfo ().startPosition.getLongitude ());
            Log.v ("MapsForgeTileProvider",
                   "Map: " +
                           _mapDatabase.getMapFileInfo ().boundingBox.getMinLatitude ());
            Log.v ("MapsForgeTileProvider",
                   "Map: " + startX);
            Log.v ("MapsForgeTileProvider",
                   "Map: " + startY);
            Log.v ("MapsForgeTileProvider",
                   "Map: " +
                           _mapDatabase.getMapFileInfo ().boundingBox.getMaxLongitude ());
            Log.v ("MapsForgeTileProvider",
                   "Map x: " + x);
            Log.v ("MapsForgeTileProvider",
                   "Map y: " + y);
            Log.v ("MapsForgeTileProvider",
                   "Map zoom: " + zoom);
            Log.v ("MapsForgeTileProvider",
                   "Map bounds east: " + east);
            Log.v ("MapsForgeTileProvider",
                   "Map bounds north: " + north);
            Log.v ("MapsForgeTileProvider",
                   "Map bounds west: " + west);
            Log.v ("MapsForgeTileProvider",
                   "Map bounds south: " + south);
            Log.v ("MapsForgeTileProvider",
                   "Map oob: " + oob);
            Log.v ("MapsForgeTileProvider",
                   "***********************************************");

            tileBitmap = Bitmap.createBitmap (org.mapsforge.core.Tile.TILE_SIZE,
                                              org.mapsforge.core.Tile.TILE_SIZE,
                                              Bitmap.Config.RGB_565);

            if (_mapGenerator.executeJob (mapGeneratorJob,
                                          tileBitmap)) {
                ByteArrayOutputStream buffer = null;

                try {
                    buffer = new ByteArrayOutputStream ();
                    tileBitmap.compress (Bitmap.CompressFormat.PNG,
                                         90,
                                         buffer);

                    /**********************************************/
                    /*
                     * String fileName; fileName = String.format
                     * ("/storage/emulated/legacy/maps/%s_%s_%s.png", new
                     * Date().getTime (), x, y); OutputStream outputStream = new
                     * FileOutputStream (fileName); buffer.writeTo
                     * (outputStream);
                     */
                    /**********************************************/

                    return new Tile (tileBitmap.getWidth (),
                                     tileBitmap.getHeight (),
                                     buffer.toByteArray ());
                }
                catch (Exception e) {
                    // do nothing
                    Log.v ("MapsForgeTileProvider",
                           e.getMessage ());
                }
                finally {
                    if (buffer != null) {
                        try {
                            buffer.close ();
                        }
                        catch (Throwable t) {
                        }
                    }
                }
            }
        }

        return null;
    }

    static private int lon2XTile (final double lon,
                                  final int zoom)
    {
        return (int) Math.floor ((lon + 180) / 360 * (1 << zoom));
    }

    static private int lat2YTile (final double lat,
                                  final int zoom)
    {
        return (int) Math.floor ((1 - Math.log (Math.tan (Math.toRadians (lat)) +
                                                1 /
                                                Math.cos (Math.toRadians (lat))) /
                                      Math.PI) /
                                 2 * (1 << zoom));
    }

    /*
     * private byte[] readTileImage (int x, int y, int zoom) { // InputStream in
     * = null; FileInputStream in = null; ByteArrayOutputStream buffer = null;
     * 
     * try {
     * 
     * in = new FileInputStream (getTileFile (x, y, zoom)); buffer = new
     * ByteArrayOutputStream ();
     * 
     * int nRead; byte[] data = new byte[BUFFER_SIZE];
     * 
     * while ((nRead = in.read (data, 0, BUFFER_SIZE)) != -1) { buffer.write
     * (data, 0, nRead); } buffer.flush ();
     * 
     * return buffer.toByteArray (); } catch (IOException e) { e.printStackTrace
     * (); return null; } catch (OutOfMemoryError e) { e.printStackTrace ();
     * return null; } finally { if (in != null) try { in.close (); } catch
     * (Exception ignored) { } if (buffer != null) try { buffer.close (); }
     * catch (Exception ignored) { } } }
     * 
     * private int fixYCoordinate (int y, int zoom) { int size = 1 << zoom; //
     * size = 2^zoom return size - 1 - y; }
     * 
     * private File getTileFile (int x, int y, int zoom) { File sdcard =
     * Environment.getExternalStorageDirectory (); String tileFile =
     * "/maps/parking.png"; File file = new File (sdcard, tileFile); return
     * file; }
     */
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
