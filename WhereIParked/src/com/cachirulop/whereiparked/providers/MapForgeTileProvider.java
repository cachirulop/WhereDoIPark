
package com.cachirulop.whereiparked.providers;

import java.io.File;
import java.nio.ByteBuffer;

import org.mapsforge.android.maps.DebugSettings;
import org.mapsforge.android.maps.mapgenerator.JobParameters;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorJob;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.DatabaseRenderer;
import org.mapsforge.android.maps.rendertheme.InternalRenderTheme;
import org.mapsforge.map.reader.MapDatabase;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;



public class MapForgeTileProvider
        implements TileProvider
{
    private DatabaseRenderer _mapGenerator;
    private MapDatabase _mapDatabase;
    private JobParameters _jobParameters;
    private DebugSettings _debugSettings;
    
    public MapForgeTileProvider()
    {        
        _mapGenerator = new DatabaseRenderer();
        _mapDatabase = new MapDatabase();
        _mapGenerator.setMapDatabase(_mapDatabase);
            
        _jobParameters = new JobParameters(InternalRenderTheme.OSMARENDER, 1);
        _debugSettings = new DebugSettings(false, false, false);
    }

    @Override
    public Tile getTile (int x,
                         int y,
                         int zoom)
    {
        org.mapsforge.core.Tile mfTile;
        Bitmap tileBitmap;
        
        _mapDatabase.closeFile ();
        
        // TODO: load the appropriate map file
        _mapDatabase.openFile(new File("/sdcard/maps/spain.map"));
        
        mfTile = new org.mapsforge.core.Tile((long) x, (long) y, (byte) zoom);
        
        MapGeneratorJob mapGeneratorJob = new MapGeneratorJob(mfTile, 
                                                              "wip",                                                           
                                                              _jobParameters,
                                                              _debugSettings);        

        tileBitmap = Bitmap.createBitmap(org.mapsforge.core.Tile.TILE_SIZE, org.mapsforge.core.Tile.TILE_SIZE, Bitmap.Config.RGB_565);

        if (_mapGenerator.executeJob(mapGeneratorJob, tileBitmap)) {
            int bytes;
            ByteBuffer buffer; 
            
            bytes = tileBitmap.getByteCount();

            buffer = ByteBuffer.allocate(bytes); 
            tileBitmap.copyPixelsToBuffer(buffer); 

            return new Tile (tileBitmap.getWidth (), tileBitmap.getHeight (), buffer.array());
        }
        else {
            return null;
        }
    }

}
