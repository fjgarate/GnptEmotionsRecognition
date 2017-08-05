package gnpt.app.fcomediavilla.com.gnpt;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by alvaro on 21/06/2017.
 */

public class User {
    private String user_name;
    private String age;
    private String gender;
    private String ethnicity;
    private String glasses;
    private String age_detected;
    private String gender_detected;
    private String ethnicity_detected;
    private String glasses_detected;

    public User(String user_name){
        this.user_name = user_name;
    }

    public User(String user_name, String age, String gender, String ethnicity, String glasses, String age_detected, String gender_detected, String ethnicity_detected, String glasses_detected){
        this.user_name = user_name;
        this.age = age;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.glasses = glasses;
        this.age_detected = age_detected;
        this.gender_detected = gender_detected;
        this.ethnicity_detected = ethnicity_detected;
        this.glasses_detected = glasses_detected;
    }

    public String getUserName(){return user_name;}
    public String getAge(){return age;}
    public String getGender(){return gender;}
    public String getEthnicity(){return ethnicity;}
    public String getGlasses(){return glasses;}
    public String getAgeDetected(){return age_detected;}
    public String getGenderDetected(){return gender_detected;}
    public String getEthnicityDetected(){return ethnicity_detected;}
    public String getGlassesDetected(){return glasses_detected;}

    /*public String toJSON(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("user_name", getUserName());
            jsonObject.put("age", getAge());
            jsonObject.put("gender", getGender());
            jsonObject.put("ethnicity", getEthnicity());
            jsonObject.put("glasses", getGlasses());
            jsonObject.put("age_detected", getAgeDetected());
            jsonObject.put("gender_detected", getGenderDetected());
            jsonObject.put("ethnicity_detected", getEthnicityDetected());
            jsonObject.put("glasses_detected", getGlassesDetected());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }*/

    public String toJSON(){
        JSONObject jsonObject = new JSONObject();
        String result;
        try{
            jsonObject.put("user_name", getUserName());
            jsonObject.put("age", getAge());
            jsonObject.put("gender", getGender());
            jsonObject.put("ethnicity", getEthnicity());
            jsonObject.put("glasses", getGlasses());
            jsonObject.put("age_detected", getAgeDetected());
            jsonObject.put("gender_detected", getGenderDetected());
            jsonObject.put("ethnicity_detected", getEthnicityDetected());
            jsonObject.put("glasses_detected", getGlassesDetected());
            result = encrypt("Prueba",jsonObject.toString());
            //result = jsonObject.toString();
            return result;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("User ", "- Encryptation error: " + e.getMessage());
            return "Error: "+ e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("User ", "- Encryptation error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    public String encrypt(String seed, String entrada) throws Exception{
        // Encrypt where jo is input, and query is output and ENCRPYTION_KEy is key
        byte[] input = entrada.getBytes("utf-8");
        Log.i("PruebaEncriptacion", "Supera el punto problematico");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(seed.getBytes("UTF-8"));
        SecretKeySpec skc = new SecretKeySpec(thedigest, "AES/ECB/PKCS5Padding");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skc);

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        String resultado = Base64.encodeToString(cipherText, Base64.DEFAULT);
        Log.i("PruebaEncriptacion", "Resultado: "+ resultado);
        return resultado;
    }

    /*public static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES_256");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES_256");
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }*/
}