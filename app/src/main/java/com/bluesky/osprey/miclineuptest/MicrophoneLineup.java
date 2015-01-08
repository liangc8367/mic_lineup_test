package com.bluesky.osprey.miclineuptest;

/**
 * mcirophone lineup, to demo the uses of Android microphone and audio encoder
 *
 * Created by liangc on 07/01/15.
 */
public class MicrophoneLineup {
    public class MicrophoneConfiguration{

    }

    public class AudioEncoderConfiguration{

    }

    public MicrophoneLineup(){

    }

    /** from start or restart (user navigates to the activity */
    public boolean start(){
        return false;
    }

    /** activity is no longer visible */
    public boolean stop(){
        return false;
    }

    /** another activity comes to the foreground */
    public boolean pause(){
        return false;
    }

    /** user returns to the activity */
    public boolean resume(){
        return false;
    }



    /** private methods and members */

}
