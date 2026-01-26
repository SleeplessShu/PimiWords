package com.sleeplessdog.matchthewords.server.domain

interface ServerDateInteractor {
    fun getLocalDbDate(): String
    fun saveLocalDbDate(date: String)
}