package com.forday.app.remote

interface RemoteMapper<DataModel> {
    fun toData(): DataModel
}