package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiContentGenerator {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun generateStatusOrQuote(topic: String, category: String, language: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = "Generate a short, viral $category status or quote in $language language about '$topic' suitable for social media sharing. Do not use quotation marks."
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", prompt) })
                            })
                        })
                    })
                }

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val respString = response.body?.string() ?: ""
                    val root = JSONObject(respString)
                    val candidates = root.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val text = candidates.getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        return@withContext text.trim()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback intelligent generator
        return@withContext getTemplateQuote(topic, category, language)
    }

    private fun getTemplateQuote(topic: String, category: String, language: String): String {
        val quotes = when (language.lowercase()) {
            "hindi" -> listOf(
                "सफलता पाना है तो परिश्रम को अपना गुरु बना लो। #$topic #HeloHindi",
                "हर नया दिन एक नई उम्मीद और नया मौका लेकर आता है। 🌟 #$topic",
                "सपनों को पाने के लिए खुद पर भरोसा होना बहुत जरूरी है! 💪 #Motivation"
            )
            "tamil" -> listOf(
                "உன் முயற்சியே உன் வெற்றியின் முதல் தாரக மந்திரம்! ✨ #$topic #HeloTamil",
                "வாழ்க்கை என்பது ஒருமுறை மட்டுமே, அதை மகிழ்ச்சியாக வாழ்வோம்! 💥 #Motivational",
                "நம்பிக்கை உள்ளவனுக்கு தோல்வி என்பது ஒரு தற்காலிக அனுபவமே! 🔥 #Status"
            )
            "telugu" -> listOf(
                "ప్రతి కొత్త రోజు నీ జీవితంలో ఒక అద్భుతమైన మార్పు తీసుకొస్తుంది! ✨ #$topic",
                "నీ కష్టమే నీ విజయానికి అసలైన బాట! 💪 #HeloTelugu #Motivation",
                "నమ్మకమే మన ప్రతి అడుగులో గెలుపుని ఇస్తుంది! 🌟 #Quotes"
            )
            else -> listOf(
                "Great things never come from comfort zones. Believe in yourself and keep pushing forward! ✨ #$topic #Motivation",
                "Start every day with a grateful heart and a focused mind. Success will follow! 🌟 #MorningVibes #Helo",
                "Your time is now. Make every moment count and write your own story! 🔥 #$topic #StatusQuotes"
            )
        }
        return quotes.random()
    }
}
