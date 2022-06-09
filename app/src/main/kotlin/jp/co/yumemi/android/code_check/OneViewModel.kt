/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * OneFragment にて呼び出される
 * TwoFragment ではitemクラス変数に格納されたデータ群を表示
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
                val response: HttpResponse = client?.get("https://api.github.com/search/repositories") {
                    header("Accept", "application/vnd.github.v3+json")
                    parameter("q", inputText)
                }

                val jsonBody = JSONObject(response.receive<String>())

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
                    /*val name = jsonItem.optString("full_name")
                    val ownerIconUrl = (jsonItem.optJSONObject("owner")?: JSONObject()).optString("avatar_url"),
                    val language = jsonItem.optString("language")
                    val stargazersCount = jsonItem.optLong("stargazers_count")
                    val watchersCount = jsonItem.optLong("watchers_count")
                    val forksCount = jsonItem.optLong("forks_conut")
                    val openIssuesCount = jsonItem.optLong("open_issues_count")

                    items.add(
                        item(
                            name = name,
                            ownerIconUrl = ownerIconUrl,
                            language = context.getString(R.string.written_language, language),
                            stargazersCount = stargazersCount,
                            watchersCount = watchersCount,
                            forksCount = forksCount,
                            openIssuesCount = openIssuesCount
                        )
                    )*/
                }

                lastSearchDate = Date()
                receiveItems = items

            }catch (e: ClientRequestException){

            }
            return@async receiveItems.toList()
        }.await()
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
) : Parcelable/*{
    fun itemDataSet(jsonItem: JSONObject, items: MutableList<item>, context: Context){
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
}*/