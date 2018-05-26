package xyf.frpc.remoting.codec.netty;

public class ByteUtil {
	//��λ��ǰ����λ�ں�  
    public static byte[] int2bytes(int num){  
        byte[] result = new byte[4];  
        result[0] = (byte)((num >>> 24) & 0xff);//˵��һ  
        result[1] = (byte)((num >>> 16)& 0xff );  
        result[2] = (byte)((num >>> 8) & 0xff );  
        result[3] = (byte)((num >>> 0) & 0xff );  
        return result;  
    }  
      
    //��λ��ǰ����λ�ں�  
    public static int bytes2int(byte[] bytes){  
        int result = 0;  
        if(bytes.length == 4){  
            int a = (bytes[0] & 0xff) << 24;//˵����  
            int b = (bytes[1] & 0xff) << 16;  
            int c = (bytes[2] & 0xff) << 8;  
            int d = (bytes[3] & 0xff);  
            result = a | b | c | d;  
        }  
        return result;  
    }  
    
  //��λ��ǰ����λ�ں�  
    public static byte[] short2bytes(int num){  
        byte[] result = new byte[2];  
        result[0] = (byte)((num >>> 8) & 0xff );  
        result[1] = (byte)((num >>> 0) & 0xff );  
        return result;  
    }  
      
    //��λ��ǰ����λ�ں�  
    public static int bytes2short(byte[] bytes){  
        int result = 0;  
        if(bytes.length == 2){  
            int a = (bytes[0] & 0xff) << 8;//˵����  
            int b = (bytes[1] & 0xff) << 0;   
            result = a | b;  
        }  
        return result;  
    }  
    
  //��λ��ǰ����λ�ں�  
    public static byte[] long2bytes(int num){  
        byte[] result = new byte[8];  
        result[0] = (byte)((num >>> 56) & 0xff);//˵��һ  
        result[1] = (byte)((num >>> 48)& 0xff );  
        result[2] = (byte)((num >>> 40) & 0xff );  
        result[3] = (byte)((num >>> 32) & 0xff ); 
        result[4] = (byte)((num >>> 24) & 0xff);//˵��һ  
        result[5] = (byte)((num >>> 16)& 0xff );  
        result[6] = (byte)((num >>> 8) & 0xff );  
        result[7] = (byte)((num >>> 0) & 0xff );  
        return result;  
    }  
      
    //��λ��ǰ����λ�ں�  
    public static int bytes2long(byte[] bytes){  
        int result = 0;  
        if(bytes.length == 8){  
        	int a = (bytes[0] & 0xff) << 56;//˵����  
            int b = (bytes[1] & 0xff) << 48;  
            int c = (bytes[2] & 0xff) << 40;  
            int d = (bytes[3] & 0xff) << 32;  
            int e = (bytes[0] & 0xff) << 24;//˵����  
            int f = (bytes[1] & 0xff) << 16;  
            int g = (bytes[2] & 0xff) << 8;  
            int h = (bytes[3] & 0xff);  
            result = a | b | c | d | e | f | g | h;  
        }  
        return result;  
    }  
    
}
