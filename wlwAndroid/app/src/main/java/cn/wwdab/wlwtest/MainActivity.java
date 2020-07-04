package cn.wwdab.wlwtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import cn.wwdab.wlwtest.socket.MySocket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTTextView;
    private TextView mHTextView;
    private EditText mIpEditText;
    private EditText mPortEditText;
    private MySocket mMySocket;
    private Handler mHandler;
    private Button mConnecteBtn;
    private Button mClearBtn;
    private Button mDisConnecteBtn;
    private ImageView mMyProLightImage;
    private TextView mMyProLightTextView;
    private TextView mMyProLightName;
    private ImageView mMyGasImage;
    private TextView mMyGasTextView;
    private Switch mMyLightSwitch;
    private ImageView mMyLightImage;
    private TextView mMyLightTextView;
    private ImageView mMyHcImage;
    private TextView mMyHcTextView;
    private TextView mMyGasName;
    private TextView mMyHcName;

    private final String myBule="#0882A1";
    private final String myRed="#ff6565";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initHandler();

    }


    public void initView() {
        mTTextView = (TextView) findViewById(R.id.t_textView);
        mHTextView = (TextView) findViewById(R.id.h_textView);
        mIpEditText = (EditText) findViewById(R.id.ip_editText);
        mPortEditText = (EditText) findViewById(R.id.port_editText);
        mMyProLightTextView = (TextView) findViewById(R.id.my_pro_light_textView);
        mMyProLightName = (TextView) findViewById(R.id.my_pro_light_name);
        mMyGasTextView = (TextView) findViewById(R.id.my_gas_textView);
        mMyLightTextView = (TextView) findViewById(R.id.my_light_textView);
        mMyHcTextView = (TextView) findViewById(R.id.my_hc_textView);
        mMyHcName = (TextView) findViewById(R.id.my_hc_name);
        mMyGasName = (TextView) findViewById(R.id.my_gas_name);

        mClearBtn = (Button) findViewById(R.id.clear_btn);//清空按钮
        mDisConnecteBtn = (Button) findViewById(R.id.disConnect_btn);//断开连接按钮
        mConnecteBtn = (Button) findViewById(R.id.connecte_btn);//连接按钮

        mMyProLightImage = (ImageView) findViewById(R.id.my_pro_light_image);
        mMyGasImage = (ImageView) findViewById(R.id.my_gas_image);
        mMyLightImage = (ImageView) findViewById(R.id.my_light_image);
        mMyHcImage = (ImageView) findViewById(R.id.my_hc_image);


        mMyLightSwitch = (Switch) findViewById(R.id.my_light_switch);
        MySwichListener mySwichListener = new MySwichListener();//添加开关变化监听
        mMyLightSwitch.setOnCheckedChangeListener(mySwichListener);

        mConnecteBtn.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        mDisConnecteBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connecte_btn:
//                点击连接
                Object[] ipAndPort = getIpAndPort();
                if (ipAndPort != null) {
                    String ip = (String) ipAndPort[0];
                    int port = (Integer) ipAndPort[1];
                    mMySocket = new MySocket(ip, port, mHandler);
                    mMySocket.start();
                }

                break;
            case R.id.disConnect_btn:
//                断开连接
                mMySocket.closeConnection();
                break;
            case R.id.clear_btn:
//                清空！

                break;


        }
    }

    public Object[] getIpAndPort() {
//获取输入的Ip和端口号
        Object[] mySet = null;
        String ip = null;
        int port = -1;
        if ("".equals(mIpEditText.getText().toString()) || "".equals(mPortEditText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "输入不能为空！！", Toast.LENGTH_SHORT).show();
        } else {
            ip = mIpEditText.getText().toString();
            port = Integer.parseInt(mPortEditText.getText().toString());
            mySet = new Object[2];
            mySet[0] = ip;
            mySet[1] = port;
        }


        return mySet;
    }

    public void showData(String data) {
//        显示数据
        if (data != "") {
            String[] splitData = data.split("&");
//            Log.e(MainActivity.class.getSimpleName(),"数据长度:"+splitData.length);
            if (splitData.length >=6) {

//                Log.e(MainActivity.class.getSimpleName(), splitData[0] + "|" + splitData[1] + "|" + splitData[2]+ "|" + splitData[3]);
                String temp = splitData[0];//温度
                String humi = splitData[1];//湿度
                String lightA = splitData[2];//光敏
                String gasA = splitData[3];//气体
                String hcA = splitData[4];//人体传感
                String tLight = splitData[5];//台灯
//            显示温湿度
                mTTextView.setText("温度：" + temp + "°C");
                mHTextView.setText("湿度：" + humi + "%");

//            显示光敏灯
                if (Float.parseFloat(lightA) < 3.0) {
//                灯关
                    mMyProLightImage.setImageResource(R.drawable.dn);
                    mMyProLightTextView.setText("灯灭");
                    mMyProLightName.setTextColor(Color.parseColor(myBule));
                    mMyProLightTextView.setTextColor(Color.parseColor(myBule));
                } else {
                    mMyProLightImage.setImageResource(R.drawable.dl);
                    mMyProLightTextView.setText("灯亮");
                    mMyProLightName.setTextColor(Color.parseColor(myRed));
                    mMyProLightTextView.setTextColor(Color.parseColor(myRed));
                }

//                判断有没有毒
                if (Float.parseFloat(gasA) < 1.5) {
//                    无毒
                    mMyGasImage.setImageResource(R.drawable.ydn);
                    mMyGasTextView.setText("无毒");
                    mMyGasTextView.setTextColor(Color.parseColor(myBule));
                    mMyGasName.setTextColor(Color.parseColor(myBule));

                } else {
//                    有毒
                    mMyGasImage.setImageResource(R.drawable.ydy);
                    mMyGasTextView.setText("剧毒");
                    mMyGasTextView.setTextColor(Color.parseColor(myRed));
                    mMyGasName.setTextColor(Color.parseColor(myRed));
                }

//                判断有没有小偷
                if(Float.parseFloat(hcA)>3.0){
//                    有狼
                    mMyHcImage.setImageResource(R.drawable.py);
                    mMyHcTextView.setText("有狼");
                    mMyHcTextView.setTextColor(Color.parseColor(myRed));
                    mMyHcName.setTextColor(Color.parseColor(myRed));
                }else{
//                    没人
                    mMyHcImage.setImageResource(R.drawable.pn);
                    mMyHcTextView.setText("没人");
                    mMyHcTextView.setTextColor(Color.parseColor(myBule));
                    mMyHcName.setTextColor(Color.parseColor(myBule));
                }
//                判断台灯
                if(Integer.parseInt(tLight)==1){
//                    开
                    mMyLightImage.setImageResource(R.drawable.dl);
                    mMyLightTextView.setTextColor(Color.parseColor("#ff6565"));
                    mMyLightSwitch.setText("开");
                }else{
//                    关
                    mMyLightImage.setImageResource(R.drawable.dn);
                    mMyLightTextView.setTextColor(Color.parseColor("#0882A1"));
                    mMyLightSwitch.setText("关");
                }


            } else {
//                Toast.makeText(getApplicationContext(),"数据错误！",Toast.LENGTH_SHORT).show();
                Log.e(MainActivity.class.getSimpleName(), "数据错误！");
            }
        } else {
            Log.e(MainActivity.class.getSimpleName(), "接到数据为空！");
        }

    }

    public void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case -1:
//                        断开连接
                        Toast.makeText(getApplicationContext(), "断开连接！", Toast.LENGTH_SHORT).show();
                        mTTextView.setText("温度：");
                        mHTextView.setText("湿度：");


                        mConnecteBtn.setEnabled(true);
                        mConnecteBtn.setText("连接");
                        mDisConnecteBtn.setEnabled(false);
                        break;
                    case 0:
//                        连接成功！
                        Toast.makeText(getApplicationContext(), "连接成功！", Toast.LENGTH_SHORT).show();
                        mConnecteBtn.setEnabled(false);
                        mConnecteBtn.setText("已连接");
                        mDisConnecteBtn.setEnabled(true);
                        break;
                    case 1:
//                        接收到消息
                        String data = (String) msg.obj;
                        showData(data);
                        Log.e(MainActivity.class.getSimpleName(), data);
                        break;
                    default:
                        break;

                }
            }
        };
    }

    public class MySwichListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
//                台灯开
                if (mMySocket != null) {
                    mMySocket.sendData("1");
                }

            } else {
                if (mMySocket != null) {
                    mMySocket.sendData("0");
                }


            }
        }
    }
}
