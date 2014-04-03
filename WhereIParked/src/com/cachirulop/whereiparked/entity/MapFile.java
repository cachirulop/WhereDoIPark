
package com.cachirulop.whereiparked.entity;

import java.util.Date;

public class MapFile
{
    private int    _idMapFile;
    private String _fileName;
    private Date   _creationDate;
    private int    _boundsEast;
    private int    _boundsWest;
    private int    _boundsNorth;
    private int    _boundsSouth;
    private int    _startZoom;

    public int getIdMapFile ()
    {
        return _idMapFile;
    }

    public void setIdMapFile (int idMapFile)
    {
        this._idMapFile = idMapFile;
    }

    public String getFileName ()
    {
        return _fileName;
    }

    public void setFileName (String fileName)
    {
        this._fileName = fileName;
    }

    public Date getCreationDate ()
    {
        return _creationDate;
    }

    public void setCreationDate (Date creationDate)
    {
        this._creationDate = creationDate;
    }

    public int getBoundsEast ()
    {
        return _boundsEast;
    }

    public void setBoundsEast (int boundsEast)
    {
        this._boundsEast = boundsEast;
    }

    public int getBoundsWest ()
    {
        return _boundsWest;
    }

    public void setBoundsWest (int boundsWest)
    {
        this._boundsWest = boundsWest;
    }

    public int getBoundsNorth ()
    {
        return _boundsNorth;
    }

    public void setBoundsNorth (int boundsNorth)
    {
        this._boundsNorth = boundsNorth;
    }

    public int getBoundsSouth ()
    {
        return _boundsSouth;
    }

    public void setBoundsSouth (int boundsSouth)
    {
        this._boundsSouth = boundsSouth;
    }

    public int getStartZoom ()
    {
        return _startZoom;
    }

    public void setStartZoom (int startZoom)
    {
        this._startZoom = startZoom;
    }

}
