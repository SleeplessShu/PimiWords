package com.sleeplessdog.pimi.server.domain

interface ServerDateInteractor {
    fun getLocalDbDate(): String
    fun saveLocalDbDate(date: String)
}