package gnpt.app.fcomediavilla.com.gnpt;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fcome on 13/05/2017.
 */

public class Results {
    private String idUser;
    private String date;
    private String idSesion;
    private int numSesion;
    private String task;
    private int percentage;
    private int correct;
    private int mistake;
    private int omission;
    private int score;
    private int totalTime;
    private double averageTime;
    private int perseveringMistake;
    private int categoryCompleted;
    private int framesDetected;
    private int framesNoFace;
    private ArrayList<EmotionDetected> emotion = new ArrayList<>();

    int count;


    public Results(String idUser, String date, String idSesion, int numSesion, String task, int percentage, int correct, int mistake,
                   int omission, int score, int totalTime, double averageTime, int perseveringMistake, int categoryCompleted, int framesDetected, int framesNoFace, ArrayList<EmotionDetected> emotion){
        this.idUser = idUser;
        this.date = date;
        this.idSesion = idSesion;
        this.numSesion = numSesion;
        this.task = task;
        this.percentage = percentage;
        this.correct = correct;
        this.mistake = mistake;
        this.omission = omission;
        this.score = score;
        this.totalTime = totalTime;
        this.averageTime = averageTime;
        this.perseveringMistake = perseveringMistake;
        this.categoryCompleted = categoryCompleted;
        this.framesDetected = framesDetected;
        this.framesNoFace = framesNoFace;
        this.emotion = emotion;
    }

    public String getIdUser(){return this.idUser;}
    public String getDate(){return this.date;}
    public String getIdSesion(){return this.idSesion;}
    public int getNumSesion(){return this.numSesion;}
    public String getTask(){return this.task;}
    public int getPercentage(){
        return this.percentage;
    }
    public int getCorrect(){
        return this.correct;
    }
    public int getMistake(){
        return this.mistake;
    }
    public int getOmission(){
        return this.omission;
    }
    public int getScore(){
        return this.score;
    }
    public int getTotalTime(){return this.totalTime;}
    public double getAverageTime(){return this.averageTime;}
    public int getPerseveringMistake(){return this.perseveringMistake;}
    public int getCategoryCompleted(){return this.categoryCompleted;}
    public int getFramesDetected(){return this.framesDetected;}
    public int getFramesNoFace(){return this.framesNoFace;}
    public ArrayList<EmotionDetected> getEmotion() {
        return emotion;
    }

    /*public JSONObject emotionToJSON(){
        JSONObject jsonObject_All = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        ArrayList<EmotionDetected> emotion = getEmotion();
        for(EmotionDetected i : emotion) {
            count++;
            try {
                jsonObject.put("user_name", getIdUser());
                jsonObject.put("attention", i.attention);
                jsonObject.put("anger", i.anger);
                jsonObject.put("contempt", i.contempt);
                jsonObject.put("disgust", i.disgust);
                jsonObject.put("engagement", i.engagement);
                jsonObject.put("fear", i.fear);
                jsonObject.put("joy", i.joy);
                jsonObject.put("sadness", i.sadness);
                jsonObject.put("surprise", i.surprise);
                jsonObject.put("valence", i.valence);
                jsonObject.put("view", i.view);
                jsonObject_All.put(String.valueOf(count), jsonObject);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
        return jsonObject_All;
    }*/

    public JSONArray emotionToJSON(){
        //JSONObject jsonObject_All = new JSONObject();
        JSONArray array = new JSONArray();
        ArrayList<EmotionDetected> emotion = getEmotion();
        for(EmotionDetected i : emotion) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("attention", i.attention);
                jsonObject.put("anger", i.anger);
                jsonObject.put("contempt", i.contempt);
                jsonObject.put("disgust", i.disgust);
                jsonObject.put("engagement", i.engagement);
                jsonObject.put("fear", i.fear);
                jsonObject.put("joy", i.joy);
                jsonObject.put("sadness", i.sadness);
                jsonObject.put("surprise", i.surprise);
                jsonObject.put("valence", i.valence);
                jsonObject.put("view", i.view);
                array.put(count, jsonObject);
                count++;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
        return array;
    }

    public String toJSON(){
        String resultado = "";
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("user_name", getIdUser());
            jsonObject.put("date", getDate());
            jsonObject.put("session_id", getIdSesion());
            jsonObject.put("session_num", getNumSesion());
            jsonObject.put("task", getTask());
            jsonObject.put("percentage", getPercentage());
            jsonObject.put("correct", getCorrect());
            jsonObject.put("mistaken", getMistake());
            jsonObject.put("omitted", getOmission());
            jsonObject.put("score", getScore());
            jsonObject.put("time_total", getTotalTime());
            jsonObject.put("time_average", getAverageTime());
            jsonObject.put("persevering_mistakes", getPerseveringMistake());
            jsonObject.put("completed_category", getCategoryCompleted());
            jsonObject.put("frames_detected", getFramesDetected());
            jsonObject.put("frames_noface", getFramesNoFace());
            jsonObject.put("result_id", emotionToJSON());
            resultado =  encrypt("Prueba", jsonObject.toString());
            Log.i("PruebaEncriptacion", "Resultado: "+resultado);
            return resultado;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.i("PruebaEncriptacion", "JSONException: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.i("PruebaEncriptacion", "EncryptException: "+e.getMessage());
            e.printStackTrace();
            return null;
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

    public static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
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
