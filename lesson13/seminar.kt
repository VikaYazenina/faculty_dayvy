import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

// ===========================================
// Задача 1. HTTP-запросы через HttpURLConnection
// ===========================================
// Цель: научиться отправлять GET и POST запросы, читать ответ и статус-код.
// API: https://jsonplaceholder.typicode.com
//
// TODO 1: Отправить GET /posts/1, вывести статус-код и тело ответа
// TODO 2: Отправить POST /posts с JSON-телом, вывести статус-код и тело
// TODO 3: Отправить GET /posts/9999, обработать ошибку (код != 2xx)
//
fun disableSslVerification() {
    val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAll, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
}
fun main() {
    disableSslVerification()
	

    // TODO 1: GET /posts/1
    println("=== GET /posts/1 ===")
	val getUrl = URL("https://jsonplaceholder.typicode.com/posts/1")
    val getConn = getUrl.openConnection() as HttpURLConnection
    getConn.requestMethod = "GET"

    println("Код: ${getConn.responseCode}")
    val getBody = getConn.inputStream.bufferedReader().readText()
    println("Тело: $getBody")
    getConn.disconnect()

    // TODO 2: POST /posts
    println("\n=== POST /posts ===")
	val postUrl=URL("https://jsonplaceholder.typicode.com/posts/1")
    val postConn=postUrl.openConnection() as HttpURLConnection
    postConn.requestMethod = "POST"
    postConn.doOutput=true
    postConn.setRequestProperty("Content-Type", "application/json")
    val json="""{ "title": "foo", "body": "bar", "userId":1}""".trimIndent()
    postConn.outputStream.write(json.toByteArray())
    println("Код: ${postConn.responseCode}")
    val postBody = postConn.inputStream.bufferedReader().readText()
    println("Тело: $postBody")
    postConn.disconnect()
    

    // TODO 3: GET /posts/9999 (несуществующий ресурс)
    println("\n=== GET /posts/9999 ===")
	val errorUrl = URL("https://jsonplaceholder.typicode.com/posts/1")
    val errorConn = errorUrl.openConnection() as HttpURLConnection
    errorConn.requestMethod = "GET"
    println("Код: ${errorConn.responseCode}")
    val statuscode=errorConn.responseCode
    if (statuscode>=400){
        val errorbody=errorConn.errorStream?.bufferedReader().readText()
        println("Тело: $errorBody")
    }else{
        val Body = errorConn.inputStream.bufferedReader().readText()
        println("Тело: $Body")
    }
    errorConn.disconnect()
}

import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

// ===========================================
// Задача 2. REST — полный CRUD
// ===========================================
// Цель: реализовать все CRUD-операции для ресурса /posts.
// API: https://jsonplaceholder.typicode.com/posts
//
// TODO 1: Реализовать sendRequest() — универсальную функцию отправки запросов
// TODO 2: Реализовать 5 CRUD-функций (ниже)
// TODO 3: Вызвать каждую функцию в main() и вывести результат
//
/

val BASE_URL = "https://jsonplaceholder.typicode.com/posts"


fun sendRequest(urlStr: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URL(urlStr).openConnection() as HttpURLConnection
    connection.requestMethod = method
    connection.connectTimeout = 5000
    connection.readTimeout = 5000
    if (body != null) {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.use { it.write(body.toByteArray()) }
    }
    val statusCode = connection.responseCode
    val responseStream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
    val responseBody = responseStream?.bufferedReader()?.use { it.readText() } ?: ""
	connection.disconnect()
    return Pair(statusCode, responseBody)
}

/** GET /posts — получить все посты */
fun getPosts(): String {
    // TODO 2a
    TODO("Реализуй getPosts")
    val(code, body)=sendRequest("$BASE_URL/$id", "GET")
    return "Code: $code\nBody: $body"
}

/** GET /posts/{id} — получить пост по ID */
fun getPost(id: Int): String {
    // TODO 2b
    TODO("Реализуй getPost")
    val(code, body)=sendRequest("$BASE_URL/$id", "GET")
    return "Code: $code\nBody: $body"
}

/** POST /posts — создать новый пост. Тело: {"title":"...", "body":"...", "userId":1} */
fun createPost(json: String): String {
    // TODO 2c
    TODO("Реализуй createPost")
    val(code, body)=sendRequest(BASE_URL, "POST", json)
    return "Code: $code\nBody: $body"
}

/** PUT /posts/{id} — полностью обновить пост */
fun updatePost(id: Int, json: String): String {
    // TODO 2d
    TODO("Реализуй updatePost")
	val(code, body)=sendRequest("$BASE_URL/$id", "PUT", json)
    return "Code: $code\nBody: $body"
}

/** DELETE /posts/{id} — удалить пост, вернуть статус-код */
fun deletePost(id: Int): Int {
    // TODO 2e
    TODO("Реализуй deletePost")
    val(code, body)=sendRequest("$BASE_URL/$id", "DELETE")
    return code
}
fun disableSslVerification() {
    val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAll, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
}


fun main() {
    disableSslVerification()

    // TODO 3: вызвать каждую функцию и вывести результат
    println("=== GET ALL ===")
	println(getPosts())
    println("\n=== GET ONE ===")
    println(getPosts(1))

    println("\n=== CREATE ===")
    val newjson="""{"title": "Lesson", "body": "HTTP is fun", "userId": 1}"""
    println(createPost(newjson))

    println("\n=== UPDATE ===")
    val updatejson="""{"id" : 1, "title": "Updated title", "body": "New body", "userId": 1}"""
    println(updatePost(1, updatejson))


    println("\n=== DELETE ===")
    val deletecode=deletePost(1)
    println("Status Code: $deletecode")
}


import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64
import javax.net.ssl.*
import java.security.cert.X509Certificate

// ===========================================
// Задача 3. JWT — авторизация
// ===========================================
// Цель: понять структуру JWT, собрать и декодировать токен, отправить запрос с Bearer-авторизацией.
// API: https://httpbin.org/bearer (возвращает 200 если есть Bearer, 401 если нет)
//
// TODO 1: Собрать JWT из трёх частей (header, payload, signature) в Base64URL
// TODO 2: Декодировать JWT обратно — вывести header и payload как JSON
// TODO 3: Отправить GET https://httpbin.org/bearer с заголовком Authorization: Bearer <token>
// TODO 4: Отправить тот же запрос БЕЗ токена — убедиться, что вернулся 401
// TODO 5: Подменить payload (role: student → admin), объяснить почему сервер отвергнет
//
fun disableSslVerification() {
    val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAll, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
}


fun main() {
    disableSslVerification()

    val encoder = Base64.getUrlEncoder().withoutPadding()

    // TODO 1: Собрать JWT
    println("=== Сборка JWT ===")
    val header = """{"alg":"HS256","typ":"JWT"}"""
    val payload = """{"sub":"1","name":"Ivan Petrov","role":"student","iat":1234567890}"""
    val fakeSignature = "dummysignature"
    val encheader=encoder.encoddeToString(header.toByteArray())
	val encpayload=encoder.encoddeToString(payload.toByteArray())
	
    val token = "$encheader.$encpayload.$fakeSignature" 
	println(token)

    // TODO 2: Декодировать JWT
    println("\n=== Декодирование JWT ===")
	val parts=token.split(".")
if (parts.size==3){
    val decheader=String(decoder.decode(parts[0]))
    val decpayload=String(decoder.decode(parts[1]))
    println(decheader)
    println(decpayload)
	}
	
    // TODO 3: GET /bearer с токеном
    println("\n=== GET /bearer (с токеном) ===")
	val url=URL("https://httpbin.org/bearer")
    val conn = getUrl.openConnection() as HttpURLConnection
    conn.requestMethod = "GET"
    conn.setRequestProperty("Authorization", "Bearer $token")
    println("Код: ${conn.responseCode}")
    val response = conn.inputStream.bufferedReader().use{it.readText}
    println("Тело: $response")
    conn.disconnect()
    

    // TODO 4: GET /bearer без токена
    println("\n=== GET /bearer (без токена) ===")
	
	val connn=URL("https://httpbin.org/bearer").openConnection() as HttpURLConnection
    
    connn.requestMethod = "GET"
    val coden=connn.responseCode
    println("Код (ожидаем 401): $coden")
    if(coden==401){
        println("Доступ запрещен")
    }
    connn.disconnect()
    
    // TODO 5: Подмена payload
    println("\n=== Подмена payload ===")
	val  adminpayload = """{"sub":"1","name":"Ivan Petrov","role":"student","iat":1234567890}"""
    
	val encadminpayload=encoder.encoddeToString(adminpayload.toByteArray())
	
    val hackedtoken = "$encheader.$encadminpayload.$fakeSignature" 
	println(hackedtoken)
	
    }
	
import java.net.HttpURLConnection
import java.net.URL

// ===========================================
// Задача 6. Клиент для сервера заметок
// ===========================================
// Цель: написать клиент, который тестирует все эндпоинты сервера.
// Перед запуском: запустить Task6_Server.kt
//
// TODO 1: Реализовать request() — универсальную функцию отправки запросов
// TODO 2: В main() выполнить 8 шагов (ниже), вывести код и тело каждого ответа

val BASE = "http://localhost:8080/api/notes"


fun request(urlStr: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URL(urlStr).openConnection() as HttpURLConnection
    connection.requestMethod = method
    connection.connectTimeout = 5000
    connection.readTimeout = 5000
    if (body != null) {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.use { it.write(body.toByteArray()) }
    }
    val statusCode = connection.responseCode
    val responseStream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
    val responseBody = responseStream?.bufferedReader()?.use { it.readText() } ?: ""
	connection.disconnect()
    return Pair(statusCode, responseBody)
}

fun main() {
    // TODO 2: выполнить 8 шагов, каждый раз вызывая request() и выводя результат

    // Шаг 1: получить все заметки
    println("=== 1. GET /api/notes — все заметки ===")
	val res1=request(BASE, "GET")
	println("Code: ${res1.first}\nBody: ${res1.second}")
	
    // Шаг 2: создать новую заметку
    println("\n=== 2. POST /api/notes — создать заметку ===")
    // JSON: {"title":"Домашка","content":"Сделать задание по сетям","tag":"учёба"}
	
    val newNote = """{"title":"Домашка","content":"Сделать задание по сетям","tag":"учёба"}"""
    val res2 = request(BASE, "POST", newNote)
    println("Код: ${res2.first}\nТело: ${res2.second}")

    // Шаг 3: получить заметку по id
    println("\n=== 3. GET /api/notes/1 — одна заметка ===")
	val res3 = request("$BASE/1", "GET")
    println("Код: ${res3.first}\nТело: ${res3.second}")

    // Шаг 4: обновить заметку     
	println("\n=== 5. GET /api/notes?tag=учёба — фильтр по тегу ===")
	val updatedNote = """{"title":"Домашка (ред.)","content":"Задание уже почти готово","tag":"учёба"}"""
    val res4 = request("$BASE/1", "PUT", updatedNote)
    println("Код: ${res4.first}\nТело: ${res4.second}")

​	// Шаг 5: фильтр по тегу
    println("\n=== 5. GET /api/notes?tag=учёба — фильтр по тегу ===")
    val res5 = request("$BASE?tag=учёба", "GET")
    println("Код: ${res5.first}\nТело: ${res5.second}"

    // Шаг 6: удалить заметку

    println("\n=== 6. DELETE /api/notes/1 — удалить заметку ===")
	val res6 = request("$BASE/1", "DELETE")
    println("Код: ${res6.first}\nТело: ${res6.second}")

​

    // Шаг 7: запросить несуществующую заметку (ожидаем 404)

    println("\n=== 7. GET /api/notes/999 — несуществующая заметка ===")
	val res7 = request("$BASE/999", "GET")
    println("Код: ${res7.first}\nТело: ${res7.second}")

​

    // Шаг 8: финальное состояние

    println("\n=== 8. GET /api/notes — финальное состояние ===")
	val res8 = request(BASE, "GET")
    println("Код: ${res8.first}\nТело: ${res8.second}")

}
