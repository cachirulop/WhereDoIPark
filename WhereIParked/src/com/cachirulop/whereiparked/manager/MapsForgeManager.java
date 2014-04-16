
package com.cachirulop.whereiparked.manager;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import org.mapsforge.android.maps.DebugSettings;
import org.mapsforge.android.maps.mapgenerator.JobParameters;
import org.mapsforge.android.maps.mapgenerator.JobTheme;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.DatabaseRenderer;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.header.FileOpenResult;
import org.mapsforge.map.reader.header.MapFileInfo;

import com.cachirulop.whereiparked.common.exception.MapsForgeException;
import com.cachirulop.whereiparked.entity.MapFile;

public class MapsForgeManager
{
    private DatabaseRenderer        _mapGenerator;
    private MapDatabase             _mapDatabase;
    private JobParameters           _jobParameters;
    private DebugSettings           _debugSettings;

    private static MapsForgeManager _instance;

    private static final int        BUFFER_SIZE = 16 * 1024;

    /**
     * Private constructor, it's a singleton class
     */
    private MapsForgeManager ()
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

    /**
     * Return the singleton instance of the class
     * 
     * @return Unique instance allowed of the class
     */
    public static synchronized MapsForgeManager getInstance ()
    {
        if (_instance == null) {
            _instance = new MapsForgeManager ();
        }

        return _instance;
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
    public MapFile getMapFile (String path)
        throws MapsForgeException
    {
        MapFile result = null;
        FileOpenResult fileOpenResult;
        File f;

        f = new File (path);

        _mapDatabase.closeFile ();
        fileOpenResult = _mapDatabase.openFile (f);

        if (fileOpenResult.isSuccess ()) {
            MapFileInfo info;

            info = _mapDatabase.getMapFileInfo ();

            result = new MapFile ();
            result.setFileName (f.getAbsolutePath ());
            result.setCreationDate (new Date (f.lastModified ()));
            result.setBoundsNorth (MapsForgeManager.lat2YTile (info.boundingBox.getMaxLatitude (),
                                                               info.startZoomLevel));
            result.setBoundsSouth (MapsForgeManager.lat2YTile (info.boundingBox.getMinLatitude (),
                                                               info.startZoomLevel));
            result.setBoundsEast (MapsForgeManager.lon2XTile (info.boundingBox.getMaxLongitude (),
                                                              info.startZoomLevel));
            result.setBoundsWest (MapsForgeManager.lon2XTile (info.boundingBox.getMinLongitude (),
                                                              info.startZoomLevel));
            result.setStartZoom (info.startZoomLevel);
        }
        else {
            throw new MapsForgeException (fileOpenResult.getErrorMessage ());
        }

        return result;
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
