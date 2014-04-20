package com.cachirulop.whereiparked.entity;

public class MapSubfile
{
    private long _idMapSubfile;
    private long _idMapFile;
    private long _boundsTileLeft;
    private long _boundsTileRight;
    private long _boundsTileTop;
    private long _boundsTileBottom;
    private int _zoomLevel;
    
    public long getIdMapSubfile ()
    {
        return _idMapSubfile;
    }
    public void setIdMapSubfile (long idMapSubfile)
    {
        this._idMapSubfile = idMapSubfile;
    }
    public long getIdMapFile ()
    {
        return _idMapFile;
    }
    public void setIdMapFile (long idMapFile)
    {
        this._idMapFile = idMapFile;
    }
    public long getBoundsTileLeft ()
    {
        return _boundsTileLeft;
    }
    public void setBoundsTileLeft (long boundsTileLeft)
    {
        this._boundsTileLeft = boundsTileLeft;
    }
    public long getBoundsTileRight ()
    {
        return _boundsTileRight;
    }
    public void setBoundsTileRight (long boundsTileRight)
    {
        this._boundsTileRight = boundsTileRight;
    }
    public long getBoundsTileTop ()
    {
        return _boundsTileTop;
    }
    public void setBoundsTileTop (long boundsTileTop)
    {
        this._boundsTileTop = boundsTileTop;
    }
    public long getBoundsTileBottom ()
    {
        return _boundsTileBottom;
    }
    public void setBoundsTileBottom (long boundsTileBottom)
    {
        this._boundsTileBottom = boundsTileBottom;
    }
    public int getZoomLevel ()
    {
        return _zoomLevel;
    }
    public void setZoomLevel (int zoomLevel)
    {
        this._zoomLevel = zoomLevel;
    }
    
}
