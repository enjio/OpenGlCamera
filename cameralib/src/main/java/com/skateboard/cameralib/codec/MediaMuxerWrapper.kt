package com.skateboard.cameralib.codec

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import java.nio.ByteBuffer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.locks.ReentrantLock

class MediaMuxerWrapper(private val mediaMuxer: MediaMuxer, private val trackNum: Int)
{

    private val lock = ReentrantLock()

    private var num = 0
    //    private var countDownLatch = CountDownLatch(trackNum)
    //
    //    private val stopCyclicBarrier = CyclicBarrier(trackNum)
    //    {
    //        mediaMuxer.stop()
    //    }
    //
    //    private val releaseCyclicBarrier = CyclicBarrier(trackNum) {
    //
    //        mediaMuxer.release()
    //    }

    val TAG="MediaMuxerWrapper"

    private var isStarting = false

    fun isStarting(): Boolean
    {
        return isStarting
    }

    fun addTrack(mediaFormat: MediaFormat): Int
    {

        return mediaMuxer.addTrack(mediaFormat)
    }

    fun start()
    {
        lockAction {

            if (!isStarting)
            {
                num++
                if (num == trackNum)
                {
                    mediaMuxer.start()
                    isStarting = true
                }
            }

        }


    }

    fun writeSampleData(trackIndex: Int, byteBuf: ByteBuffer, bufferInfo: MediaCodec.BufferInfo)
    {
        mediaMuxer.writeSampleData(trackIndex, byteBuf, bufferInfo)
    }

    fun release()
    {
        lockAction {

            if(!isStarting)
            {
                mediaMuxer.release()
            }
        }

    }

    fun stop()
    {
        lockAction {
            if(isStarting)
            {
                num--
                if(num==0)
                {
                    mediaMuxer.stop()
                    isStarting=false
                }
            }
        }

    }

    fun lockAction(T: () -> Unit)
    {

        try
        {
            lock.lock()
            T()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        finally
        {
            lock.unlock()
        }

    }

}