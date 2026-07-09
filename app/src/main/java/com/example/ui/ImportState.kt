package com.example.data

sealed class ImportState {

    object Idle : ImportState()

    object Loading : ImportState()

    data class Success(
        val message: String = "تمت الإضافة بنجاح"
    ) : ImportState()

    data class Error(
        val error: String
    ) : ImportState()
}
