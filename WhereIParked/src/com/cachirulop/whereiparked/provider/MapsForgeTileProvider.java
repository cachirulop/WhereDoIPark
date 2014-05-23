
package com.cachirulop.whereiparked.provider;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

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

            tileBitmap = MapsForgeManager.getBitmap (x,
                                                     y,
                                                     zoom);
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
