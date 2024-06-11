package com.dicoding.picodiploma.loginwithanimation.data.remote

open class Event<out T>(private val content: T) {

  @Suppress("MemberVisibilityCanBePrivate")
  var hasBeenHandled = false
    private set

  fun getContentIfNotHandled(): T? {
    return if (hasBeenHandled) {
      null
    } else {
      hasBeenHandled = true
      content
    }
  }
}