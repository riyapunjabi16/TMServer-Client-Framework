//package com.thinking.machines.network.common;
public interface RequestListener
{
public Object onData(Client client,String actionType,Object object);
public void onOpen(Client client);
public void onError(Client client);
public void onClose(Client client);
}
