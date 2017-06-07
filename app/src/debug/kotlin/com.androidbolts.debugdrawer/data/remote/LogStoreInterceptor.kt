package com.androidbolts.debugdrawer.data.remote


import com.androidbolts.debugdrawer.data.local.DebugPrefs
import com.androidbolts.debugdrawer.data.model.NetworkLogEntry
import com.redeem.data.local.LogStorage

import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit

import javax.inject.Inject
import javax.inject.Singleton

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.internal.http.HttpHeaders
import okio.Buffer

@Singleton
class LogStoreInterceptor @Inject
internal constructor(private val logStorage: LogStorage, private val urls: AppUrls, debugPrefs: DebugPrefs) : Interceptor {
    private var showImageLogs: Boolean = false

    init {
        this.showImageLogs = debugPrefs.showImageLogs
    }

    fun showImageLogs(show: Boolean) {
        this.showImageLogs = show
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!showImageLogs && chain.request().url().toString().contains(urls.baseImageUrl()))
            return chain.proceed(request)
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        val connection = chain.connection()
        val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
        var log = StringBuilder()
        log.append("REQUEST\n\n--> ").append(request.method()).append(' ').append(request.url()).append(' ').append(protocol).append("\n")
        if (request.headers() != null)
            log.append("\nHeaders:\n").append(request.headers().toString()).append("\n")
        if (hasRequestBody) {
            log.append(" (").append(requestBody!!.contentLength()).append("-byte body)").append("\n")
        }

        if (hasRequestBody) {
            if (requestBody!!.contentType() != null) {
                log.append("Content-Type: ").append(requestBody.contentType()).append("\n")
            }

            if (requestBody.contentLength() != -1L) {
                log.append("Content-Length: ").append(requestBody.contentLength()).append("\n")
            }
        }

        val startNs = request.headers()
        var buffer = 0

        val response = startNs.size()
        while (buffer < response) {
            val tookMs = startNs.name(buffer)
            if (!"Content-Type".equals(tookMs, ignoreCase = true) && !"Content-Length".equals(tookMs, ignoreCase = true)) {
                log.append(tookMs).append(": ").append(startNs.value(buffer)).append("\n")
            }
            ++buffer
        }

        if (hasRequestBody) {
            if (this.bodyEncoded(request.headers())) {
                log.append("--> END ").append(request.method()).append(" (encoded body omitted)").append("\n")
            } else {
                val buffer1 = Buffer()
                requestBody!!.writeTo(buffer1)
                var charset = UTF8
                val mediaType = requestBody.contentType()
                if (mediaType != null) {
                    charset = mediaType.charset(UTF8)
                }

                log.append("")
                if (isPlaintext(buffer1)) {
                    log.append(buffer1.readString(charset)).append("\n")
                    log.append("--> END ").append(request.method()).append(" (").append(requestBody.contentLength()).append("-byte body)").append("\n")
                } else {
                    log.append("--> END ").append(request.method()).append(" (binary ").append(requestBody.contentLength()).append("-byte body omitted)").append("\n")
                }
            }
        } else {
            log.append("--> END ").append(request.method()).append("\n")
        }
        val requestLog = log.toString()

        log = StringBuilder()
        val var28 = System.nanoTime()
        log.append("\n\nRESPONSE\n\n<-- ")
        val var31: Response
        try {
            var31 = chain.proceed(request)
        } catch (var27: Exception) {
            log.append("HTTP FAILED: ").append(var27).append("\n")
            logStorage.insertLog(NetworkLogEntry(requestLog + log.toString()))
            throw var27
        }

        val var33 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - var28)
        val responseBody = var31.body()
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) contentLength.toString() + "-byte" else "unknown-length"
        log.append(var31.code()).append(' ').append(var31.message()).append(' ').append(var31.request().url())
                .append("\n")
                .append(" (").append(var33).append("ms").append(", ").append(bodySize).append(" body)").append("\n")
        val headers = var31.headers()
        var source = 0

        val buffer1 = headers.size()
        while (source < buffer1) {
            log.append(headers.name(source)).append(": ").append(headers.value(source)).append("\n")
            ++source
        }

        if (HttpHeaders.hasBody(var31)) {
            if (this.bodyEncoded(var31.headers())) {
                log.append("<-- END HTTP (encoded body omitted)").append("\n")
            } else {
                val var34 = responseBody.source()
                var34.request(9223372036854775807L)
                val var35 = var34.buffer()
                var charset = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8)
                    } catch (var26: UnsupportedCharsetException) {
                        log.append("\n\n")
                        log.append("Couldn\'t decode the response body; charset is likely malformed.")
                        log.append("<-- END HTTP")
                        logStorage.insertLog(NetworkLogEntry(requestLog + log.toString()))
                        return var31
                    }

                }

                if (!isPlaintext(var35)) {
                    log.append("\n\n")
                    log.append("<-- END HTTP (binary ").append(var35.size()).append("-byte body omitted)")
                    logStorage.insertLog(NetworkLogEntry(requestLog + log.toString()))
                    return var31
                }

                if (contentLength != 0L) {
                    log.append("\n\n")
                    log.append(var35.clone().readString(charset))
                }
                log.append("<-- END HTTP (").append(var35.size()).append("-byte body)").append("\n\n")
            }
        } else {
            log.append("<-- END HTTP")
        }
        logStorage.insertLog(NetworkLogEntry(requestLog + log.toString()))
        return var31
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        @Throws(EOFException::class)
        internal fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val e = Buffer()
                val byteCount = if (buffer.size() < 64L) buffer.size() else 64L
                buffer.copyTo(e, 0L, byteCount)

                var i = 0
                while (i < 16 && !e.exhausted()) {
                    val codePoint = e.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                    ++i
                }

                return true
            } catch (var6: EOFException) {
                return false
            }

        }
    }
}
