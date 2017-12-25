package android.tapmisam.az.testregister;

import android.content.SharedPreferences;

/**
 * Created by ayselkas on 12/4/17.
 */

public class TokenManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static TokenManager INSTANCE = null;

    private TokenManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    static synchronized TokenManager getInstance(SharedPreferences prefs){
        if (INSTANCE == null) {
            INSTANCE = new TokenManager(prefs);
        }

        return INSTANCE;
    }

    public void saveToken(String token) {
        editor.putString("ACCESS_TOKEN", token).commit();
    }

    public void deleteToken(){
        editor.remove("ACCESS_TOKEN").commit();
        editor.remove("REFRESH_TOKEN").commit();
    }

    public String getToken(){
       String token;
        token=prefs.getString("ACCESS_TOKEN", null);
        return  token;
    }
}