package com.rogergcc.barcodescannerdemo.ui.helper


/**
 * Created on agosto.
 * year 2022 .
 */


import com.rogergcc.barcodescannerdemo.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree

object TimberAppLogger {
 fun d(s: String?, vararg objects: Any?) {
  Timber.d(s, *objects)
 }

 fun d(throwable: Throwable?, s: String?, vararg objects: Any?) {
  Timber.d(throwable, s, *objects)
 }

 fun e(s: String?, vararg objects: Any?) {
  Timber.e(s, *objects)
 }

 fun e(throwable: Throwable?, s: String?, vararg objects: Any?) {
  Timber.e(throwable, s, *objects)
 }

 fun i(s: String?, vararg objects: Any?) {
  Timber.i(s, *objects)
 }

 fun i(throwable: Throwable?, s: String?, vararg objects: Any?) {
  Timber.i(throwable, s, *objects)
 }

 fun init() {
  if (BuildConfig.DEBUG) {
   Timber.plant(DebugTree())
  }
 }

 fun w(s: String?, vararg objects: Any?) {
  Timber.w(s, *objects)
 }

 fun w(throwable: Throwable?, s: String?, vararg objects: Any?) {
  Timber.w(throwable, s, *objects)
 }
}