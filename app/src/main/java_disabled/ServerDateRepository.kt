package com.sleeplessdog.matchthewords.server.domain

interface ServerDateRepository {
    fun getLocalDbDate(): String

    fun saveLocalDbDate(date: String)
}