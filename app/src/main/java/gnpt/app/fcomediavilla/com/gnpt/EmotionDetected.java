package gnpt.app.fcomediavilla.com.gnpt;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by alvaro on 16/06/2017.
 */

public class EmotionDetected implements Serializable {
    float attention, anger, contempt, disgust, engagement, fear, joy, sadness, surprise, valence;
    int view;

    EmotionDetected(){}

    public float getAttention(){return attention;}
    public void setAttention(float attention){this.attention = attention;}
    public float getAnger(){return anger;}
    public void setAnger(float anger){this.anger = anger;}
    public float getContempt(){return contempt;}
    public void setContempt(float contempt){this.contempt = contempt;}
    public float getDisgust(){return disgust;}
    public void setDisgust(float disgust){this.disgust = disgust;}
    public float getEngagement(){return engagement;}
    public void setEngagement(float engagement){this.engagement = engagement;}
    public float getFear(){return fear;}
    public void setFear(float fear){this.fear = fear;}
    public float getJoy(){return joy;}
    public void setJoy(float joy){this.joy = joy;}
    public float getSadness(){return sadness;}
    public void setSadness(float sadness){this.sadness = sadness;}
    public float getSurprise(){return surprise;}
    public void setSurprise(float surprise){this.surprise = surprise;}
    public float getValence(){return valence;}
    public void setValence(float valence){this.valence = valence;}
    public float getView(){return view;}
    public void setView(int view){this.view = view;}
}
