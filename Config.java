package com.samarth261.asd;

/**
 * Created by SUMANTH C on 12/28/2016.
 */
public class Config {
    //URL to our login1.php file
    public static final String LOGIN_URL = MyUtilities.WEBSITE_NAME + "login.php";
    public static final String GRADE_URL = "http://ashadeepautismapp.esy.es/ashadeepapp/grade.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the email of current logged in user
    public static final String USERNAME_SHARED_PREF = "username";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}