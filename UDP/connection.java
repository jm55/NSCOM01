import java.io.*;

public class connection implements Serializable{
    private String src, tgt;
    private int port;
    
    public connection(String src, String tgt, int port){
        this.src = src;
        this.tgt = tgt;
        this.port = port;
    }

    public connection(){
        this.src = "";
        this.tgt = "";
        this.port = -1;
    }

    public void setSrc(String src){
        this.src = src;
    }

    public void setTgt(String tgt){
        this.tgt = tgt;
    }

    public void setPort(int port){
        this.port = port;
    }

    public String getSrc(){
        return this.src;
    }

    public String getTgt(){
        return this.tgt;
    }

    public int getPort(){
        return this.port;
    }

    //Reference: https://stackoverflow.com/a/2836659
    public byte[] getBytes(){
        ByteArrayOutputStream b = null;
        ObjectOutputStream obj = null;
        try{
            b = new ByteArrayOutputStream();
            obj = new ObjectOutputStream(b);
            obj.writeObject(this);
        }catch(IOException e){
            System.out.println(e);
        }
        return b.toByteArray();
    }
}