package com.newsalesbeatApp.utilityclass;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PingServer extends AsyncTask<Void, Void, Boolean> {

    private DelhiClientPing delhiClientPing;

    public PingServer(DelhiClientPing mDelhiClientPing) {
        delhiClientPing = mDelhiClientPing;
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean internet) {
        delhiClientPing.accept(internet);
    }

    public interface DelhiClientPing {
        void accept(Boolean internet);
    }
}
    

