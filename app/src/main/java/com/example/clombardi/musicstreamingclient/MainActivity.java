package com.example.clombardi.musicstreamingclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.ListMenuItemView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;


import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.Player;
import Models.Song;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;


public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener {
    ArrayList<Song> listItems=new ArrayList<Song>();
    ArrayAdapter<Song> adapter;
    String sharedPreferenceName = "MusicStreamPrefs";
    String sharedServerPreferenceName = "ServerAddress";
    String sharedServerPortNumName = "PortNumber";
    SharedPreferences settings;
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Song item = adapter.getItem(position);
            MyTask task = new MyTask() {
                @Override
                protected Void doInBackground(ListView... params) {
                    try {
                        //final AudioTrack audioTrack;
                        //int playBufSize=AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_8BIT);
                        //audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_8BIT, playBufSize, AudioTrack.MODE_STREAM);

                        SocketAddress sockaddr = new InetSocketAddress(settings.getString(sharedServerPreferenceName, null), settings.getInt(sharedServerPortNumName, 0));
                        String query;
                        final Socket client = new Socket();
                        client.connect(sockaddr, settings.getInt(sharedServerPortNumName, 0));
                        DataOutputStream oStream = new DataOutputStream(client.getOutputStream());
                        final DataInputStream iStream = new DataInputStream(client.getInputStream());


                        byte[] songIdArray = new Integer(item.SongId).toString().getBytes("UTF-8");
                        byte[] sendData = new byte[songIdArray.length + 1];
                        sendData[0] = 1;
                        for (int i = 0; i < songIdArray.length; i++)
                        {
                            sendData[i+1] = songIdArray[i];
                        }
                        oStream.write(sendData);
                        //final AdvancedPlayer player = new AdvancedPlayer(iStream);
                        new Thread() {
                            byte[] buffer = new byte[4096];
                            public void run() {
                                boolean isPlaying = true;
                                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 20000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, 4096, AudioTrack.MODE_STREAM);
                                        /*AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                                11025,
                                                AudioFormat.CHANNEL_IN_MONO,
                                                AudioFormat.ENCODING_PCM_16BIT,
                                                musicLength,
                                                AudioTrack.MODE_STREAM);*/
                                // Start playback
                                //int readSize = 0;
                                audioTrack.play();
                                try {
                                    while (iStream.read(buffer) > 0) {
                                        
                                        audioTrack.write(buffer, 0, buffer.length);
                                        //player.play();
                                    }
                                    //audioTrack.stop();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    client.close();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            task.execute();
        }
    };
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
    TextWatcher searchChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            listItems.clear();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setups
        ListView songListView = (ListView) findViewById(R.id.songListView);
        //ScrollView Setup
        songListView.setOnScrollListener(this);
        songListView.setOnItemClickListener(itemClickListener);
        //ArrayList Setup
        adapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, listItems);

        /*Song song1 = new Song(1, "a", "a", "a");
        Song song2 = new Song(2, "b", "b", "b");
        Song song3 = new Song(3, "c", "c", "c");

        listItems.add(song1);
        listItems.add(song2);
        listItems.add(song3);
        listItems.add(song1);
        listItems.add(song2);
        listItems.add(song3);
        listItems.add(song1);
        listItems.add(song2);
        listItems.add(song3);
        listItems.add(song1);
        listItems.add(song2);
        listItems.add(song3);
        listItems.add(song1);
        listItems.add(song2);
        listItems.add(song3);
        listItems.add(song1);
        listItems.add(song2);
        listItems.add(song3);*/

        songListView.setAdapter(adapter);

        //Search Setup
        EditText searchText = (EditText)findViewById(R.id.searchText);
        searchText.addTextChangedListener(searchChanged);

        //Preferences Setup
        settings = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.add(1, 1, Menu.FIRST, "Server Address");
        menu.add(1, 2, Menu.FIRST +1, "Port Number");
        menu.add(1, 3, Menu.FIRST +2, "About");


        // Return True
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        switch (id){
            case 1:
                final EditText localServerAddr = new EditText(this);
                localServerAddr.setText(settings.getString(sharedServerPreferenceName, null), TextView.BufferType.EDITABLE);
                alert.setView(localServerAddr);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(sharedServerPreferenceName, localServerAddr.getText().toString());
                        editor.commit();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
                break;
            case 2:
                final EditText localPortNum = new EditText(this);
                localPortNum.setInputType(InputType.TYPE_CLASS_NUMBER);
                localPortNum.setText(settings.getString(sharedServerPortNumName, null), TextView.BufferType.EDITABLE);
                alert.setView(localPortNum);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt(sharedServerPortNumName, Integer.parseInt(localPortNum.getText().toString()));
                        editor.commit();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
                break;
            case 3:
                break;
            default:
                break;
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {


    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        ListView myView = (ListView) view.findViewById(R.id.songListView);
        MyTask task = new MyTask() {
            @Override
            protected Void doInBackground(ListView... params) {
                try {
                    SocketAddress sockaddr = new InetSocketAddress(settings.getString(sharedServerPreferenceName, null), settings.getInt(sharedServerPortNumName, 0));
                    String query;
                    Socket client = new Socket();
                    client.connect(sockaddr, settings.getInt(sharedServerPortNumName, 0));
                    DataOutputStream oStream = new DataOutputStream(client.getOutputStream());
                    DataInputStream iStream = new DataInputStream(client.getInputStream());
                    String searchText = ((EditText) findViewById(R.id.searchText)).getText().toString();
                    byte[] searchBytes = searchText.getBytes("UTF-8");
                    byte[] sendData = new byte[searchText.length() + 2];
                    sendData[0] = 0;
                    sendData[1] = (byte)listItems.size();
                    for (int i = 0; i < searchText.length(); i++) {
                        sendData[i + 2] = searchBytes[i];
                    }
                    oStream.write(sendData);

                    byte[] recvData = new byte[8192];
                    iStream.read(recvData);

                    String songJson = new String(recvData, "UTF-8");

                    Song s = new Song();
                    String[] songs = songJson.split(";");
                    for (String song : songs) {
                        s = new Song();
                        String[] attributes = song.split("\\|");
                        for (int i = 0; i < 4; i++) {
                            switch (i) {
                                case 0:
                                    s.SongId = Integer.parseInt(attributes[i]);
                                    break;
                                case 1:
                                    s.Title = attributes[i];
                                    break;
                                case 2:
                                    s.Artist = attributes[i];
                                    break;
                                case 3:
                                    s.Album = attributes[i];
                                    break;
                                default:
                                    break;
                            }
                        }
                        listItems.add(s);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute(myView);
    }
    abstract class MyTask extends AsyncTask<ListView, Void, Void> {
        AsyncTask task = new AsyncTask<ListView, Void, Void>() {
            @Override
            protected Void doInBackground(ListView... params) {
                return null;
            }
        };
    }
}
