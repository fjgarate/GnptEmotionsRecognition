package gnpt.app.fcomediavilla.com.gnpt;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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


    public Results(String idUser, String date, String idSesion, int numSesion, String task, int percentage, int correct, int mistake,
                   int omission, int score, int totalTime, double averageTime, int perseveringMistake, int categoryCompleted){
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



    public String toJSON(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("idUser", getIdUser());
            jsonObject.put("fecha", getDate());
            jsonObject.put("idSesion", getIdSesion());
            jsonObject.put("numSesion", getNumSesion());
            jsonObject.put("tarea", getTask());
            jsonObject.put("porcentaje", getPercentage());
            jsonObject.put("aciertos", getCorrect());
            jsonObject.put("errores", getMistake());
            jsonObject.put("omisiones", getOmission());
            jsonObject.put("puntuacion", getScore());
            jsonObject.put("tiempoTotal", getTotalTime());
            jsonObject.put("tiempoReaccion", getAverageTime());
            jsonObject.put("erroresPerseverativos", getPerseveringMistake());
            jsonObject.put("categoriasCompletadas", getCategoryCompleted());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
