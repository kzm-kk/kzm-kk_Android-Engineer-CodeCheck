/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * OneFragment にて呼び出される
 *
 * TwoFragment ではsearchResults内部でitemクラス変数に格納されたデータ群を表示
 */
class OneViewModel(
    val context: Context
) : ViewModel() {

    // 検索結果
    fun searchResults(inputText: String): List<item> = runBlocking {
        val client = HttpClient(Android)
        var receiveItems = mutableListOf<item>()//try中のアイテム受け取り用

        return@runBlocking GlobalScope.async {

            try {
                val response: HttpResponse = client.get("https://api.github.com/search/repositories") {
                    header("Accept", "application/vnd.github.v3+json")
                    parameter("q", inputText)
                }

                val jsonBody = JSONObject(response.receive<String>())
                receiveItems = itemListSet(jsonBody)

            }catch (e: ClientRequestException){

            }
            return@async receiveItems.toList()
        }.await()
    }

    //
    fun itemListSet(jsonBody: JSONObject):MutableList<item>{
        val jsonItems = jsonBody.optJSONArray("items")?: JSONArray()
        val items = mutableListOf<item>()

        /**
         * アイテムの個数分ループする
         */
        for (i in 0 until jsonItems.length()) {
            val jsonItem = jsonItems.optJSONObject(i)?: JSONObject()

            items.add(
                item(
                    name = jsonItem.optString("full_name"),
                    ownerIconUrl = (jsonItem.optJSONObject("owner")?: JSONObject()).optString("avatar_url"),
                    language = context.getString(R.string.written_language, jsonItem.optString("language")),
                    stargazersCount = jsonItem.optLong("stargazers_count"),
                    watchersCount = jsonItem.optLong("watchers_count"),
                    forksCount = jsonItem.optLong("forks_conut"),
                    openIssuesCount = jsonItem.optLong("open_issues_count")
                )
            )
        }

        lastSearchDate = Date()
        return items
    }
}

@Parcelize
data class item(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable