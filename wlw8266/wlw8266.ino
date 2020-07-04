#include "ESP8266WiFi.h"

const char* ssid = "JCC";//连接的WiFi名称
const char* password =  "j862278721";//连接的wifi密码

IPAddress local_IP(192, 168, 1, 109);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);


WiFiServer wifiServer(6666);

int len;
int i;
String readClientData = "";//读取客户端发来的数据

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  delay(1000);

  WiFi.config(local_IP, gateway, subnet);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    //    Serial.println("Connecting..");
  }

  //  Serial.print("Connected to WiFi. IP:");
  //  Serial.println(WiFi.localIP());
  wifiServer.begin();
}

void loop() {
  // put your main code here, to run repeatedly:
  WiFiClient client = wifiServer.available();

  while (client.connected()) {

    if (client.available()) {
      //        等待客户端发送消息进来
      while (true) {
        char c = client.read();
        readClientData += c;
        if (c == '\n') {
          //          接收完一行数据发送给arduino
          Serial.print(readClientData);
          break;
        }
      }
    }

    if (Serial.available()) {
      //    接收到Arduino 发来的 数据处理
      size_t c = Serial.available();
      uint8_t sbuf[c];
      Serial.readBytes(sbuf, c); //读取arduino数据
      //      Serial.write(sbuf, c); //打印到串口监视器

      //          发送到客户端
      len = sizeof(sbuf) / sizeof(sbuf[0]);
      for (i = 0; i < len; i++) {
        client.write(sbuf[i]);
      }

    }
    readClientData = "";
    delay(10);
  }

  client.stop();
  //  Serial.println("Client disconnected");
  delay(100);
}
