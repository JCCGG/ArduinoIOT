package cn.wwdab.wlwtest.socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MySocket extends Thread {

    private String mIp;//服务器ip
    private int mPort;//服务器端口
    private Socket mSocket;
    private BufferedReader mBr;
    private BufferedWriter mBw;
    private Handler mHandler;

    public MySocket(String ip, int port, Handler handler) {
        this.mIp = ip;
        this.mPort = port;
        this.mHandler = handler;

    }

    public void sendData(final String msg) {
//        发送数据到服务器
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   if (mSocket.isConnected()){
                       mBw.write(msg);
                       mBw.write("\n");
                       mBw.flush();
                   }

               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }).start();
    }


    public void closeConnection() {
//        关闭连接
        try {
            Log.e(MySocket.class.getSimpleName(), "关闭连接！");

            if (mSocket != null) {
                mSocket.close();
            }
            if (mBw != null) {
                mBw.close();
            }
            if (mBr != null) {
                mBr.close();
            }

            if(mSocket.isClosed()){
                Message closeMsg = new Message();
                closeMsg.what = -1;
                mHandler.sendMessage(closeMsg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
//        创建一个socket进程
        try {
            Log.e(MySocket.class.getSimpleName(), "Ip:" + mIp + "port:" + mPort);
            mSocket = new Socket(mIp, mPort);
            if (mSocket.isConnected()) {
                Message connectMsg = new Message();
                connectMsg.what = 0;
                mHandler.sendMessage(connectMsg);
                Log.e(MySocket.class.getSimpleName(), "连接成功！");

                mBr = new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"gbk"));
                mBw = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(),"gbk"));
                String data = "";
                while ((data = mBr.readLine()) != null) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
