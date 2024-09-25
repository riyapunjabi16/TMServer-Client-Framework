//package com.thinking.machines.network.common;
public class RequestWrapper implements java.io.Serializable
{
private Object object;
private String actionType;
public RequestWrapper()
{
}
public void setObject(Object object)
{
this.object=object;
}
public Object getObject()
{
return this.object;
}
public void setActionType(String actionType)
{
this.actionType=actionType;
}
public String getActionType()
{
return this.actionType;
}
}