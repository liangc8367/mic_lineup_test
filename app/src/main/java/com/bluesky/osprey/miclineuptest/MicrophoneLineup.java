package com.bluesky.osprey.miclineuptest;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * mcirophone lineup, to demo the uses of Android microphone and audio encoder
 *
 * Created by liangc on 07/01/15.
 */
public class MicrophoneLineup {
    public class MicrophoneConfiguration{


    }

    public class AudioEncoderConfiguration{
        static final int AUDIO_SAMPLE_RATE = 8000; // 8KHz
        static final int AUDIO_AMR_BITRATE = 7400; // 7.4Kbps
    }

    /** ctor of lineup */
    public MicrophoneLineup(){
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AMR_NB);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, AudioEncoderConfiguration.AUDIO_SAMPLE_RATE);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        format.setInteger(MediaFormat.KEY_BIT_RATE, AudioEncoderConfiguration.AUDIO_AMR_BITRATE);
        
        try {
            mCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AMR_NB);
            mCodec.configure(
                    format,
                    null /* surface */,
                    null /* crypto */,
                    MediaCodec.CONFIGURE_FLAG_ENCODE
            );

        } catch (Exception e){
            Log.e(TAG, "failed to create encoder: " + e);
            mCodec = null;
        }
    }

    /** from start or restart (user navigates to the activity */
    /** start Mic lineup */
    public boolean start(){
        mCodec.start();

        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mCodec.getOutputBuffers();


        return true;
    }

    /** activity is no longer visible */
    /** stop mic lineup */
    public boolean stop(){
        mCodec.stop();
        return true;
    }

    /** destroy */
    public boolean cleanup(){
        mCodec.release();
        mCodec = null;
        return false;
    }

    /** another activity comes to the foreground */
    public boolean pause(){
        return false;
    }

    /** user returns to the activity */
    public boolean resume(){
        return true;
    }



    /** private methods and members */
    static final String TAG = "MicLineup";
    MediaCodec mCodec = null;
    
}
