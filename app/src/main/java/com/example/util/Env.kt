package com.example.util

object Env {
    val GEMINI_API_KEY: String?
        get() = try {
            System.getenv("GEMINI_API_KEY")
        } catch (t: Throwable) {
            null
        }
}
