package com.sleeplessdog.pimi.server.domain

class ServerDateInteractorImpl(
    private val dateRepository: ServerDateRepository,
) : ServerDateInteractor {
    override fun getLocalDbDate(): String {
        return dateRepository.getLocalDbDate()
    }

    override fun saveLocalDbDate(date: String) {
        dateRepository.saveLocalDbDate(date)
    }
}