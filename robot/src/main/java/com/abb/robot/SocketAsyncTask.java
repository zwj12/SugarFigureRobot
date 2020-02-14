package com.abb.robot;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketAsyncTask extends AsyncTask<SocketMessageData, Integer, SocketMessageData[]> {
    private static final String TAG = "SocketAsyncTask";
    private static final int connectTimeOut = 2000;
    private static final int soTimeOut = 2000;

    private boolean ioExceptionRaised=false;
    public boolean isIoExceptionRaised() {
        return ioExceptionRaised;
    }

    private static String HOST = "10.0.2.2";
    private static int PORT = 3003;
    private static Socket socket =null;
    private static OutputStream outputStream;
    private static InputStream inputStream;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;

    private OnSocketListener socketListener;

    public SocketAsyncTask(String HOST, int PORT, OnSocketListener socketListener) {
        SocketAsyncTask.HOST = HOST;
        SocketAsyncTask.PORT = PORT;
        this.socketListener = socketListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute in UI thread");
    }

    @Override
    protected SocketMessageData[] doInBackground(SocketMessageData... socketMessageDatas) {
        Log.d(TAG, "doInBackground");
        try {
            connectToRobot();
            byte[] receiveBytes = new byte[1024];
            for (int i = 0; i < socketMessageDatas.length; i++) {
                if(socketMessageDatas[i]==null){
                    continue;
                }
                dataOutputStream.write(socketMessageDatas[i].getRequestRawBytes());
                dataOutputStream.flush();
                dataInputStream.read(receiveBytes, 0, 3);
                dataInputStream.read(receiveBytes, 3, (receiveBytes[1] << 8) + receiveBytes[2]);
                socketMessageDatas[i].unpackResponseRawBytes(receiveBytes);
                publishProgress(i * 100 / socketMessageDatas.length);
                if(socketMessageDatas[i].getSocketMessageType()==SocketMessageType.CloseConnection){
                    socket.close();
                    socket=null;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Socket Error: " + e.getClass().toString());
            this.ioExceptionRaised=true;
            socket=null;
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, String.format("doInBackground in SocketAsyncTask thread"));
        return socketMessageDatas;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        Log.d(TAG, String.format("onProgressUpdate in UI thread, %d", progress[0]));
    }

    @Override
    protected void onPostExecute(SocketMessageData[] socketMessageDatas) {
        super.onPostExecute(socketMessageDatas);
        for (SocketMessageData socketMessageData : socketMessageDatas) {
            if(socketMessageData!=null){
                Log.d(TAG, String.format("onPostExecute in UI thread, %s", socketMessageData.responseValue));
            }
        }
        this.socketListener.refreshUI(socketMessageDatas);
    }

    private void connectToRobot() throws IOException {
        if (socket==null || !socket.isConnected() || socket.isClosed() ) {
            Log.d(TAG, "The robot is connecting");
            this.ioExceptionRaised=false;
            socket=new Socket();
            SocketAddress endpoint = new InetSocketAddress(HOST, PORT);
            socket.connect(endpoint, connectTimeOut);
            socket.setSoTimeout(soTimeOut);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));
            Log.d(TAG, "The robot is connected");
        }else
        {
            Log.d(TAG, "The robot is already connected");
        }

    }

    public interface OnSocketListener {
        void refreshUI(SocketMessageData[] socketMessageDatas);
    }

}
