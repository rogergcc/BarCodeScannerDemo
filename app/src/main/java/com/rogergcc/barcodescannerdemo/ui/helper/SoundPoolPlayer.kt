package com.rogergcc.qrunlockclientapp.helper

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.rogergcc.barcodescannerdemo.R


/**
 * Created on agosto.
 * year 2022 .
 */
open class SoundPoolPlayer(pContext: Context?) {
 private var mShortPlayer: SoundPool? = null
 private val mSounds: HashMap<Int, Int> = HashMap()
 fun playShortResource(piResource: Int) {
  val iSoundId = mSounds[piResource] as Int
  mShortPlayer!!.play(iSoundId, 0.99f, 0.99f, 0, 0, 1f)
 }

 fun release() {
  mShortPlayer!!.release()
 }

 init {
  mShortPlayer = SoundPool.Builder()
   .setMaxStreams(4)
   .build()

//        this.mShortPlayer = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
  mSounds[R.raw.bleep] = mShortPlayer!!.load(pContext, R.raw.bleep, 1)
 }
}