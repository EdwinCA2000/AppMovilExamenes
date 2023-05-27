package com.example.examenesseq.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomDateDeserializer : JsonDeserializer<Date> {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy, h:mm:ss", Locale.US)

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        val dateString = json?.asString
        return dateFormat.parse(dateString)
    }
}

// Crear una instancia personalizada de Gson con el deserializador de fecha personalizado
val gson: Gson = GsonBuilder()
    .registerTypeAdapter(Date::class.java, CustomDateDeserializer())
    .create()


