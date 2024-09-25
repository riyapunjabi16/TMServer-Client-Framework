//package com.thinking.machines.server;
//import com.thinking.machines.network.common.*;
import java.io.*;
import java.net.*;
import java.util.*;
public class TMNetworkServer
{
private RequestListener requestListener;
private ServerSocket socketForReceiving;
private ServerSocket socketForSending;
private int portForReceiving;
private int portForSending;
private Thread senderThreadListener;
private Thread receiverThreadListener;
private HashMap<String,Pair<ReceiverSocketWrapper,SenderSocketWrapper>> connectionsMap;
public TMNetworkServer(int portForReceiving,int portForSending,RequestListener requestListener)
{
this.requestListener=requestListener;
this.connectionsMap=new HashMap<>();
this.portForReceiving=portForReceiving;
this.portForSending=portForSending;
try
{
this.socketForReceiving=new ServerSocket(this.portForReceiving);
this.socketForSending=new ServerSocket(this.portForSending);
}catch(Exception exception)
{
exception.printStackTrace();
System.exit(0);
}
startReceiverThreadListener();
startSenderThreadListener();
}
private void startReceiverThreadListener()
{
receiverThreadListener=new Thread(new Runnable(){
public void run()
{
try
{
System.out.println("Receiver port is : "+portForReceiving);
while(true)
{
Socket socket=socketForReceiving.accept();
Thread thread=new Thread(new Runnable(){
public void run()
{
try
{
InputStream is=socket.getInputStream();
byte r[];
r=new byte[1024];
while(true)
{
if(is.read(r)!=-1) break;
}
// some security aspect can be put over here
String uuid=java.util.UUID.randomUUID().toString();
byte s[]=uuid.getBytes();
OutputStream os=socket.getOutputStream();
ReceiverSocketWrapper receiverSocketWrapper;
receiverSocketWrapper=new ReceiverSocketWrapper(socket,is,os,uuid,requestListener);
Pair<ReceiverSocketWrapper,SenderSocketWrapper> pair=new Pair<>();
pair.setFirst(receiverSocketWrapper);
connectionsMap.put(uuid,pair);
os.write(s);
os.flush();

}catch(Exception e)
{
e.printStackTrace();
}
}
});
thread.start();
}
}catch(Exception exception)
{
}
}
});
receiverThreadListener.start();
}
private void startSenderThreadListener()
{
senderThreadListener=new Thread(new Runnable(){
public void run()
{
try
{
System.out.println("Sender port is : "+portForSending);
while(true)
{
Socket socket=socketForSending.accept();
Thread thread = new Thread(new Runnable (){
public void run()
{
try
{
int uuidLength;
int i;
InputStream is=socket.getInputStream();
byte r[];
r=new byte[1024];
while(true)
{
uuidLength=is.read(r);
if(uuidLength!=-1) break;
}
for(i=uuidLength;i<1024;i++) r[i]=32;
String uuid=new String(r).trim();
Pair<ReceiverSocketWrapper,SenderSocketWrapper> pair=connectionsMap.get(uuid);
byte ack[]=new byte[1];
if(pair!=null) ack[0]=100;
else ack[0]=101;
OutputStream os=socket.getOutputStream();
os.write(ack);
os.flush();
if(pair==null) 
{
socket.close();
return;
}
SenderSocketWrapper senderSocketWrapper=new SenderSocketWrapper(socket,is,os);
pair.setSecond(senderSocketWrapper);
ReceiverSocketWrapper receiverSocketWrapper;
receiverSocketWrapper=pair.getFirst();
senderSocketWrapper.start();
receiverSocketWrapper.start();
Client client = new Client(uuid);
requestListener.onOpen(client);
}catch(Exception ee)
{
}
}
});
thread.start();
}
}catch(Exception exception)
{
}
}
});
senderThreadListener.start();
}
public void waitForServerToStop()
{
try
{
Thread.sleep(60000);
}catch(Exception e)
{
e.printStackTrace();
}
try
{
receiverThreadListener.join();
}catch(Exception e)
{
e.printStackTrace();
}
try
{
senderThreadListener.join();
}catch(Exception e)
{
e.printStackTrace();
}
}
public void sendRequest(Client client,String actionType,Object object,ResponseListener responseListener)
{
Pair<ReceiverSocketWrapper,SenderSocketWrapper> pair;
pair=connectionsMap.get(client.getClientId());
if(pair==null)
{
responseListener.onError("Client disconnected");
return;
}
SenderSocketWrapper senderSocketWrapper;
senderSocketWrapper=pair.getSecond();
RequestWrapper requestWrapper=new RequestWrapper();
requestWrapper.setObject(object);
requestWrapper.setActionType(actionType);
senderSocketWrapper.addRequest(requestWrapper,responseListener);
}
}