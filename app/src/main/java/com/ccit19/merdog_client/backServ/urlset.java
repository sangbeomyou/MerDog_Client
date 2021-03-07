package com.ccit19.merdog_client.backServ;

public class urlset {
    private String data = "http://jghee717.cafe24.com";
    public String getData()
    {
        return data;
    }
    public void setData(String data)
    {
        this.data = data;
    }
    private static urlset instance = null;

    public static synchronized urlset getInstance(){
        if(null == instance){
            instance = new urlset();
        }
        return instance;
    }
}
