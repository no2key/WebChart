package com.lfx.db;

import java.security.*;
import javax.crypto.*;

public class DBPassword 
{
    private static String Algorithm  = "Blowfish";
    private static String EncryptKey = "531616C7983404028ECA6D7E75E91B27";

    static
    {
       Security.addProvider(new com.sun.crypto.provider.SunJCE());
    }

    public final static String encrypt(String src)
    {
      try 
      {    
        SecretKey deskey = new javax.crypto.spec.SecretKeySpec(hex2byte(EncryptKey),Algorithm);
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.ENCRYPT_MODE,deskey);
        byte[] cipherByte=c1.doFinal(src.getBytes());
        return byte2hex(cipherByte);
      }
      catch (java.security.NoSuchAlgorithmException e1) {}
      catch (javax.crypto.NoSuchPaddingException e2) {}
      catch (javax.crypto.BadPaddingException e3) {}
      catch (java.lang.Exception e4) {}
      return src;
    }

    public final static String encrypt(String enkey, String src)
    {
      try 
      {    
        SecretKey deskey = new javax.crypto.spec.SecretKeySpec(hex2byte(enkey),Algorithm);
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.ENCRYPT_MODE,deskey);
        byte[] cipherByte=c1.doFinal(src.getBytes());
        return byte2hex(cipherByte);
      }
      catch (java.security.NoSuchAlgorithmException e1) {}
      catch (javax.crypto.NoSuchPaddingException e2) {}
      catch (javax.crypto.BadPaddingException e3) {}
      catch (java.lang.Exception e4) {}
      return src;
    }

    public final static String decrypt(String enkey, String src)
    {
      try 
      {
         SecretKey deskey = new javax.crypto.spec.SecretKeySpec(hex2byte(enkey),Algorithm);
         Cipher c1 = Cipher.getInstance(Algorithm);
         c1.init(Cipher.DECRYPT_MODE,deskey);
         byte[] clearByte=c1.doFinal(hex2byte(src));
         return new String(clearByte);
      }
      catch (java.security.NoSuchAlgorithmException e1) {}
      catch (javax.crypto.NoSuchPaddingException e2) {}
      catch (javax.crypto.BadPaddingException e3) {}
      catch (java.lang.Exception e4) {}
      return src;
   }

   private static int getHexValue(char b)
   {
         if (b >= '0' && b <= '9') return b-'0';
         if (b >= 'A' && b <= 'F') return 10 + b - 'A';
         if (b >= 'a' && b <= 'f') return 10 + b - 'a';
	 return 0;
   }

    private static byte[] hex2byte(String b) //二行制转字符串
    {
       char stmp[] = b.toCharArray();
       byte bs[] = new byte[stmp.length/2];
       for (int n=0;n<bs.length;n++)
       {
         bs[n] = (byte)(16 * getHexValue(stmp[2*n]) + getHexValue(stmp[2*n+1]));
       }
       return bs;
   }

   private static String byte2hex(byte[] b) //二行制转字符串
   {
      String hs="";
      String stmp="";
      for (int n=0;n<b.length;n++)
      {
        stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
        if (stmp.length()==1) hs=hs+"0"+stmp;
        else hs=hs+stmp;
      }
      return hs.toUpperCase();
  }
}