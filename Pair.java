//package com.thinking.machines.network.common;
public class Pair<A,B>
{
private Object first;
private Object second;
public Pair()
{
}
public void setFirst(A first)
{
this.first=first;
}
public A getFirst()
{
return (A)first;
}
public void setSecond(B second)
{
this.second=second;
}
public B getSecond()
{
return (B)second;
}
}
