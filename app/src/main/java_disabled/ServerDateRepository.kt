package com.sleeplessdog.pimi.server.domain

interface ServerDateRepository {
    fun getLocalDbDate(): String

    fun saveLocalDbDate(date: String)
}