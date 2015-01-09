package com.bluesky.osprey.miclineuptest;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Audio Recorder, running in dedicated thread context, providing following public methods:
 * - start
 * - stop. Once being stopped, ThreadedAudioRecorder could not be restarted again.
 *
 * downstream of ThreadedAudioRecoder shall register itself through CompletionHandler
 *
 * Created by liangc on 08/01/15.
 */
public class ThreadedAudioRecorder implements DataSource {
    public class AudioRecorderConfiguration{
        static final int AUDIO_SAMPLE_RATE = 8000; // 8KHz
        static final int BUFFER_SIZE_MULTIPLIER = 10;
    }

    /** ctor */
    public ThreadedAudioRecorder(ByteBufferSource bufferSource){
        mBufferSource = bufferSource;
    }

    @Override
    public boolean setCompletionHandler(CompletionHandler handler) {
        if(mCompletionHandler == null){
            mCompletionHandler = handler;
            return true;
        }
        return false;
    }

    /** start recording */
    public boolean start(){
        if(mState!=State.INITIAL){
            return false;
        }

        mState = State.RUNNING;

        mThread = new Thread( new Runnable(){
            @Override
            public void run(){
                // initialization
                int szMinBuffer = AudioRecord.getMinBufferSize(
                        AudioRecorderConfiguration.AUDIO_SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT
                );
                AudioRecord recorder;
                Log.i(TAG, "create audioRecord with buffer size as "
                        + szMinBuffer * AudioRecorderConfiguration.BUFFER_SIZE_MULTIPLIER);
                try{
                    recorder = new AudioRecord(
                            MediaRecorder.AudioSource.MIC,
                            AudioRecorderConfiguration.AUDIO_SAMPLE_RATE,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            szMinBuffer * AudioRecorderConfiguration.BUFFER_SIZE_MULTIPLIER
                    );
                }catch(Exception e){
                    Log.e(TAG, "exception happened in creating audio record:" + e);
                    mState = State.ZOMBIE;
                    return;
                }

                // recording
                recorder.startRecording();
                while(mState == State.RUNNING){
                    ByteBuffer buf = mBufferSource.getByteBuffer();
                    if(buf != null) {
                        int szRead = recorder.read(buf, buf.limit());
                        if (mCompletionHandler != null) {
                            mCompletionHandler.dataAvailable(buf);
                        }
                    }
                }

                // house cleanup
                recorder.stop();
                recorder.release();
                mState = State.ZOMBIE;
                if(mCompletionHandler != null){
                    mCompletionHandler.onEndOfLife();
                }
            }

        });

        mThread.start();
        return true;
    }

    /** stop recording and release resources */
    public void stop(){
        mState=State.ZOMBIE;
    }

    /** private methods and members */
    private enum State{
        INITIAL,
        RUNNING,
        ZOMBIE
    }

    ByteBufferSource    mBufferSource;
    CompletionHandler   mCompletionHandler = null;
    State               mState;
    Thread              mThread = null;

    static final String TAG = "ThreadedAudioRecorder";

}
