
package com.cachirulop.whereiparked.providers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import android.os.Environment;
import android.util.Log;

import com.cachirulop.whereiparked.manager.MapsForgeManager;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

public class MapsForgeTileProvider
        implements TileProvider
{
    @Override
    public synchronized Tile getTile (int x,
                                      int y,
                                      int zoom)
    {
        Bitmap tileBitmap;
        ByteArrayOutputStream buffer = null;

        try {
            buffer = new ByteArrayOutputStream ();

            tileBitmap = MapsForgeManager.getBitmap (x, y, zoom);
            tileBitmap.compress (Bitmap.CompressFormat.PNG,
                                 100,
                                 buffer);

            return new Tile (tileBitmap.getWidth (),
                             tileBitmap.getHeight (),
                             buffer.toByteArray ());
        }
        catch (Exception e) {
            // do nothing
            return null;
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
