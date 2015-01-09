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
            return;
        }

        MicLineupByteBufferSource bufferSource = new MicLineupByteBufferSource();
        mAudioRecorder = new ThreadedAudioRecorder(bufferSource);
        MicLineupCompHandler handler = new MicLineupCompHandler();
        mAudioRecorder.setCompletionHandler(handler);

        mState = State.INITIAL;
    }

    /** from start or restart (user navigates to the activity */
    /** start Mic lineup */
    public boolean start(){
        if(mState != State.INITIAL){
            return false;
        }

        mCodec.start();
        mInputBuffers = mCodec.getInputBuffers();
        mOutputBuffers = mCodec.getOutputBuffers();

        mAudioRecorder.start();
        mState = State.RUNNING;

        return true;
    }

    /** activity is no longer visible */
    /** stop mic lineup */
    public boolean stop(){
        mState = State.ZOMBIE;
        mAudioRecorder.stop();
        return true;
    }

    /** destroy */
    private void release(){
        mCodec.stop();
        mCodec.release();
        mCodec = null;
        mAudioRecorder = null;
    }

//    /** another activity comes to the foreground */
//    public boolean pause(){
//        return false;
//    }
//
//    /** user returns to the activity */
//    public boolean resume(){
//        return true;
//    }



    /** private methods and members */

    /** byte buffer factory, utilizes the input byte buffers from codec */
    private class MicLineupByteBufferSource implements ByteBufferSource{

        @Override
        public ByteBuffer getByteBuffer() {
            ++mCountTotal;
            int index = mCodec.dequeueInputBuffer(0);// immediately
            if(index == MediaCodec.INFO_TRY_AGAIN_LATER){
                ++mCountFailed;
                Log.i(TAG, "failed to get buffer from encoder, "
                        + mCountFailed + "/" + mCountTotal);
                return null;
            } else {
                return mInputBuffers[index];
            }
        }

        private int mCountFailed    = 0;
        private int mCountTotal     = 0;
    }

    /** completion handler to process Microphone events */
    private class MicLineupCompHandler implements  DataSource.CompletionHandler{

        @Override
        public void dataAvailable(ByteBuffer byteBuffer) {
            // compress audio
            int sz  = byteBuffer.position();
            mCodec.queueInputBuffer(
                    index,
                    0, // offset
                    sz,
                    0, // presentationTimeUS
                    0  // flags
            );

            // get compressed audio
        }

        @Override
        public void onEndOfLife() {
            release();
        }
    }

    static final String     TAG             = "MicLineup";

    MediaCodec              mCodec;
    ByteBuffer[]            mInputBuffers;
    ByteBuffer[]            mOutputBuffers;

    ThreadedAudioRecorder   mAudioRecorder;

    private enum State{
        INITIAL,
        RUNNING,
        ZOMBIE
    }
    State                   mState;

}
