package Models;

/**
 * Created by clombardi on 12/7/2014.
 */
public class Song {
    public int SongId;
    public String Title;
    public String Artist;
    public String Album;

    public Song()
    {

    }

    public Song(int sid, String t, String ar, String al)
    {
        SongId = sid;
        Title = t;
        Artist = ar;
        Album = al;
    }
    @Override
    public String toString()
    {
        return SongId + ". Title:" + Title + " Artist:" + Artist + " Album:" + Album;
    }
}
