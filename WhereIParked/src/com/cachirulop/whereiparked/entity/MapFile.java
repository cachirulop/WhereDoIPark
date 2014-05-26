
package com.cachirulop.whereiparked.entity;

import java.util.ArrayList;
import java.util.Date;

public class MapFile
{
    private long                  _idMapFile;
    private String                _fileName;
    private Date                  _creationDate;

    private ArrayList<MapSubfile> _subFiles;

    public MapFile ()
    {
        _subFiles = new ArrayList<MapSubfile> ();
    }

    public long getIdMapFile ()
    {
        return _idMapFile;
    }

    public void setIdMapFile (long idMapFile)
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

    public ArrayList<MapSubfile> getSubFiles ()
    {
        return _subFiles;
    }

    public void setSubFiles (ArrayList<MapSubfile> value)
    {
        _subFiles = value;
    }
}
