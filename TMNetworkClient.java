//package com.thinking.machines.client;
//import com.thinking.machines.network.common.*;
import java.net.*;
import java.io.*;
public class TMNetworkClient
{
Pair<ReceiverSocketWrapper,SenderSocketWrapper> pair;
private RequestListener requestListener;
private String server;
private int portForSending;
private int portForReceiving;
public TMNetworkClient(String server,int portForSending,int portForReceiving,RequestListener requestListener)
{
this.server=server;
this.portForSending=portForSending;
this.portForReceiving=portForReceiving;
this.requestListener=requestListener;
connect();
}
public void connect()
{
try
{
Socket socketForSendingRequest=new Socket(this.server,this.portForSending);
InputStream sendingInputStream=socketForSendingRequest.getInputStream();
OutputStream sendingOutputStream=socketForSendingRequest.getOutputStream();
byte ack[]=new byte[1];
ack[0]=100;
sendingOutputStream.write(ack);
sendingOutputStream.flush();
byte header[]=new byte[1024];
int uuidLength=0;
while(true)
{
uuidLength=sendingInputStream.read(header);
if(uuidLength!=-1) break;
}
int i,j,k;
for(i=uuidLength;i<1024;i++) header[i]=32;
String uuid=new String(header).trim();

Socket socketForReceivingRequest=new Socket(this.server,this.portForReceiving);
InputStream receivingInputStream=socketForReceivingRequest.getInputStream();
OutputStream receivingOutputStream=socketForReceivingRequest.getOutputStream();
receivingOutputStream.write(uuid.getBytes());
receivingOutputStream.flush();
while(true)
{
if(receivingInputStream.read(ack)!=-1) break;
}
Client client=new Client(uuid);
ReceiverSocketWrapper receiverSocketWrapper;
receiverSocketWrapper=new ReceiverSocketWrapper(socketForReceivingRequest,receivingInputStream,receivingOutputStream,uuid,requestListener);
SenderSocketWrapper senderSocketWrapper;
senderSocketWrapper=new SenderSocketWrapper(socketForSendingRequest,sendingInputStream,sendingOutputStream);
pair=new Pair<>();
pair.setFirst(receiverSocketWrapper);
pair.setSecond(senderSocketWrapper);
receiverSocketWrapper.start();
senderSocketWrapper.start();
requestListener.onOpen(client);
}catch(Exception exception)
{
exception.printStackTrace();
}
}
public void sendRequest(String actionType,Object object,ResponseListener responseListener)
{
SenderSocketWrapper senderSocketWrapper;
senderSocketWrapper=pair.getSecond();
RequestWrapper requestWrapper=new RequestWrapper();
requestWrapper.setObject(object);
requestWrapper.setActionType(actionType);
senderSocketWrapper.addRequest(requestWrapper,responseListener);
}
}