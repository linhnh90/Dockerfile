package com.styl.pa.serverlocal

import android.content.Context
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.utils.LogManager
import java.io.*
import java.net.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple, tiny, nicely embeddable HTTP 1.0 (partially 1.1) server in Java
 *
 *
 *  NanoHTTPD version 1.25,
 * Copyright  2001,2005-2012 Jarno Elonen (elonen@iki.fi, http://iki.fi/elonen/)
 * and Copyright  2010 Konstantinos Togias (info@ktogias.gr, http://ktogias.gr)
 *
 *
 * **Features + limitations: **
 *
 *  *  Only one Java file
 *  *  Java 1.1 compatible
 *  *  Released as open source, Modified BSD licence
 *  *  No fixed config files, logging, authorization etc. (Implement yourself if you need them.)
 *  *  Supports parameter parsing of GET and POST methods (+ rudimentary PUT support in 1.25)
 *  *  Supports both dynamic content and file serving
 *  *  Supports file upload (since version 1.2, 2010)
 *  *  Supports partial content (streaming)
 *  *  Supports ETags
 *  *  Never caches anything
 *  *  Doesn't limit bandwidth, request time or simultaneous connections
 *  *  Default code serves files and shows all HTTP parameters and headers
 *  *  File server supports directory listing, index.html and index.htm
 *  *  File server supports partial content (streaming)
 *  *  File server supports ETags
 *  *  File server does the 301 redirection trick for directories without '/'
 *  *  File server supports simple skipping for files (continue download)
 *  *  File server serves also very long files without memory overhead
 *  *  Contains a built-in list of most common mime types
 *  *  All header names are converted lowercase so they don't vary between browsers/clients
 *
 *
 *
 *
 * **Ways to use: **
 *
 *  *  Run as a standalone app, serves files and shows requests
 *  *  Subclass serve() and embed to your own program
 *  *  Call serveFile() from serve() with your own base directory
 *
 *
 *
 * See the end of the source file for distribution license
 * (Modified BSD licence)
 */
@ExcludeFromJacocoGeneratedReport
class NanoHTTPD(private val context: Context, private val myTcpPort: Int, ip: String?, private val myRootDir: File) {
    // ==================================================
    // API parts
    // ==================================================
    /**
     * Override this to customize the server.
     *
     *
     *
     * (By default, this delegates to serveFile() and allows directory listing.)
     *
     * @param uri    Percent-decoded URI without parameters, for example "/index.cgi"
     * @param method    "GET", "POST" etc.
     * @param parms    Parsed, percent decoded parameters from URI and, in case of POST, data.
     * @param header    Header entries, percent decoded
     * @return HTTP response, see class Response for details
     */
    fun serve(
        uri: String,
        method: String?,
        header: Properties,
        parms: Properties,
        files: Properties
    ): Response? {
        myOut.println("$method '$uri' ")
        var e = header.propertyNames()
        while (e.hasMoreElements()) {
            val value = e.nextElement() as String
            myOut.println(
                "  HDR: '" + value + "' = '" +
                        header.getProperty(value) + "'"
            )
        }
        e = parms.propertyNames()
        while (e.hasMoreElements()) {
            val value = e.nextElement() as String
            myOut.println(
                "  PRM: '" + value + "' = '" +
                        parms.getProperty(value) + "'"
            )
        }
        e = files.propertyNames()
        while (e.hasMoreElements()) {
            val value = e.nextElement() as String
            myOut.println(
                "  UPLOADED: '" + value + "' = '" +
                        files.getProperty(value) + "'"
            )
        }
        return serveFile(uri, header, myRootDir, true)
    }

    /**
     * HTTP response.
     * Return one of these from serve().
     */
    inner class Response {
        /**
         * Default constructor: response = HTTP_OK, data = mime = 'null'
         */
        constructor() {
            status = HTTP_OK
        }

        /**
         * Basic constructor.
         */
        constructor(status: String, mimeType: String?, data: InputStream?) {
            this.status = status
            this.mimeType = mimeType
            this.data = data
        }

        /**
         * Convenience method that makes an InputStream out of
         * given text.
         */
        constructor(status: String, mimeType: String?, txt: String) {
            this.status = status
            this.mimeType = mimeType
            try {
                data = ByteArrayInputStream(txt.toByteArray(charset("UTF-8")))
            } catch (uee: UnsupportedEncodingException) {
                LogManager.i("NanoHTTPD: Encoding not supported")
            }
        }

        /**
         * Adds given line to the header.
         */
        fun addHeader(name: String, value: String) {
            header[name] = value
        }

        /**
         * HTTP status code after processing, e.g. "200 OK", HTTP_OK
         */
        var status: String

        /**
         * MIME type of content, e.g. "text/html"
         */
        var mimeType: String? = null

        /**
         * Data of the response, may be null.
         */
        var data: InputStream? = null

        /**
         * Headers for the HTTP response. Use addHeader()
         * to add lines.
         */
        var header = Properties()
    }

    /**
     * Stops the server.
     */
    fun stop() {
        try {
            myServerSocket?.close()
            myThread!!.join()
        } catch (ioe: IOException) {
            LogManager.i("NanoHTTPD: Unable to stop server")
        } catch (e: InterruptedException) {
            LogManager.i("NanoHTTPD: Unable to stop server")
        }
    }

    /**
     * Handles one session, i.e. parses the HTTP request
     * and returns the response.
     */
    @ExcludeFromJacocoGeneratedReport
    private inner class HTTPSession(private val mySocket: Socket) : Runnable {
        override fun run() {
            try {
                val `is` = mySocket.getInputStream() ?: return

                // Read the first 8192 bytes.
                // The full header should fit in here.
                // Apache's default header limit is 8KB.
                // Do NOT assume that a single read will get the entire header at once!
                val bufsize = 8192
                var buf = ByteArray(bufsize)
                var splitbyte = 0
                var rlen = 0
                run {
                    var read = `is`.read(buf, 0, bufsize)
                    while (read > 0) {
                        rlen += read
                        splitbyte = findHeaderEnd(buf, rlen)
                        if (splitbyte > 0) break
                        read = `is`.read(buf, rlen, bufsize - rlen)
                    }
                }

                // Create a BufferedReader for parsing the header.
                val hbis = ByteArrayInputStream(buf, 0, rlen)
                val hin =
                    BufferedReader(InputStreamReader(hbis))
                val pre = Properties()
                val parms = Properties()
                val header = Properties()
                val files = Properties()

                // Decode the header into parms and header java properties
                decodeHeader(hin, pre, parms, header)
                val method = pre.getProperty("method")
                val uri = pre.getProperty("uri")
                var size = 0x7FFFFFFFFFFFFFFFL
                val contentLength = header.getProperty("content-length")
                if (contentLength != null) {
                    try {
                        size = contentLength.toInt().toLong()
                    } catch (ex: NumberFormatException) {
                        LogManager.i("NanoHTTPD: Unable to format string to number")
                    }
                }

                // Write the part of body already read to ByteArrayOutputStream f
                val f = ByteArrayOutputStream()
                if (splitbyte < rlen) f.write(buf, splitbyte, rlen - splitbyte)

                // While Firefox sends on the first read all the data fitting
                // our buffer, Chrome and Opera send only the headers even if
                // there is data for the body. We do some magic here to find
                // out whether we have already consumed part of body, if we
                // have reached the end of the data to be sent or we should
                // expect the first byte of the body at the next read.
                if (splitbyte < rlen) size -= rlen - splitbyte + 1.toLong() else if (splitbyte == 0 || size == 0x7FFFFFFFFFFFFFFFL) size =
                    0

                // Now read all the body and write it to f
                buf = ByteArray(512)
                while (rlen >= 0 && size > 0) {
                    rlen = `is`.read(buf, 0, 512)
                    size -= rlen.toLong()
                    if (rlen > 0) f.write(buf, 0, rlen)
                }

                // Get the raw body as a byte []
                val fbuf = f.toByteArray()

                // Create a BufferedReader for easily reading it as string.
                val bin = ByteArrayInputStream(fbuf)
                val `in` =
                    BufferedReader(InputStreamReader(bin))

                // If the method is POST, there may be parameters
                // in data section, too, read it:
                if (method != null && method.equals("POST", ignoreCase = true)) {
                    var contentType = ""
                    val contentTypeHeader = header.getProperty("content-type")
                    var st =
                        StringTokenizer(contentTypeHeader, "; ")
                    if (st.hasMoreTokens()) {
                        contentType = st.nextToken()
                    }
                    if (contentType.equals("multipart/form-data", ignoreCase = true)) {
                        // Handle multipart/form-data
                        if (!st.hasMoreTokens()) sendError(
                            HTTP_BADREQUEST,
                            "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html"
                        )
                        val boundaryExp = st.nextToken()
                        st = StringTokenizer(boundaryExp, "=")
                        if (st.countTokens() != 2) sendError(
                            HTTP_BADREQUEST,
                            "BAD REQUEST: Content type is multipart/form-data but boundary syntax error. Usage: GET /example/file.html"
                        )
                        st.nextToken()
                        val boundary = st.nextToken()
                        decodeMultipartData(boundary, fbuf, `in`, parms, files)
                    } else {
                        // Handle application/x-www-form-urlencoded
                        var postLine = ""
                        val pbuf = CharArray(512)
                        var read = `in`.read(pbuf)
                        while (read >= 0 && !postLine.endsWith("\r\n")) {
                            postLine += String(pbuf, 0, read)
                            read = `in`.read(pbuf)
                        }
                        postLine = postLine.trim { it <= ' ' }
                        decodeParms(postLine, parms)
                    }
                }
                if (method != null && method.equals(
                        "PUT",
                        ignoreCase = true
                    )
                ) files["content"] = saveTmpFile(fbuf, 0, f.size())

                // Ok, now do the serve()
                uri?.let {
                    val r =
                        serve(uri, method, header, parms, files)
                    if (r == null) sendError(
                        HTTP_INTERNALERROR,
                        "SERVER INTERNAL ERROR: Serve() returned a null response."
                    ) else sendResponse(r.status, r.mimeType, r.header, r.data)
                }
                `in`.close()
                `is`.close()
            } catch (ioe: IOException) {
                try {
                    sendError(
                        HTTP_INTERNALERROR,
                        "SERVER INTERNAL ERROR: IOException: " + ioe.message
                    )
                } catch (t: Throwable) {
                    LogManager.i("NanoHTTPD: Send error failed")
                }
            } catch (ie: InterruptedException) {
                // Thrown by sendError, ignore and exit the thread.
            }
        }

        private fun readLine(br: BufferedReader): String? {
            val sb = StringBuffer()
            var intC: Int
            while (br.read().also { intC = it } != -1) {
                val c = intC.toChar()
                if (c == '\n') {
                    break
                }
                if (sb.length >= MAX_STR_LEN) {
                    errorMaximumLength()
                    return null
                }
                sb.append(c)
            }
            return sb.toString()
        }

        private fun errorMaximumLength() {
            sendError(
                    HTTP_BADREQUEST,
                    "BAD REQUEST: MAXIMUM REQUEST LENGTH $MAX_STR_LEN"
            )
        }

        /**
         * Decodes the sent headers and loads the data into
         * java Properties' key - value pairs
         */
        @Throws(InterruptedException::class)
        private fun decodeHeader(
            `in`: BufferedReader,
            pre: Properties,
            parms: Properties,
            header: Properties
        ) {
            try {
                // Read the request line
                val inLine = readLine(`in`) ?: return
                val st = StringTokenizer(inLine)
                if (!st.hasMoreTokens()) sendError(
                    HTTP_BADREQUEST,
                    "BAD REQUEST: Syntax error. Usage: GET /example/file.html"
                )
                val method = st.nextToken()
                pre["method"] = method
                if (!st.hasMoreTokens()) sendError(
                    HTTP_BADREQUEST,
                    "BAD REQUEST: Missing URI. Usage: GET /example/file.html"
                )
                var uri = st.nextToken()

                // Decode parameters from the URI
                val qmi = uri!!.indexOf('?')
                uri = if (qmi >= 0) {
                    decodeParms(uri.substring(qmi + 1), parms)
                    decodePercent(uri.substring(0, qmi))
                } else decodePercent(uri)

                // If there's another token, it's protocol version,
                // followed by HTTP headers. Ignore version but parse headers.
                // NOTE: this now forces header names lowercase since they are
                // case insensitive and vary by client.
                if (st.hasMoreTokens()) {
                    var line = readLine(`in`)
                    while (line != null && line.trim { it <= ' ' }.length > 0) {
                        val p = line.indexOf(':')
                        if (p >= 0) header[line.substring(0, p).trim { it <= ' ' }.toLowerCase()] =
                            line.substring(p + 1).trim { it <= ' ' }
                        line = readLine(`in`)
                    }
                }
                pre["uri"] = uri
            } catch (ioe: IOException) {
                sendError(
                    HTTP_INTERNALERROR,
                    "SERVER INTERNAL ERROR: IOException: " + ioe.message
                )
            }
        }

        /**
         * Decodes the Multipart Body data and put it
         * into java Properties' key - value pairs.
         */
        @Throws(InterruptedException::class)
        private fun decodeMultipartData(
            boundary: String,
            fbuf: ByteArray,
            `in`: BufferedReader,
            parms: Properties,
            files: Properties
        ) {
            try {
                val bpositions = getBoundaryPositions(fbuf, boundary.toByteArray())
                var boundarycount = 1
                var mpline = readLine(`in`)
                while (mpline != null) {
                    if (mpline.indexOf(boundary) == -1) sendError(
                        HTTP_BADREQUEST,
                        "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html"
                    )
                    boundarycount++
                    val item = Properties()
                    mpline = readLine(`in`)
                    while (mpline != null && mpline.trim { it <= ' ' }.length > 0) {
                        val p = mpline.indexOf(':')
                        if (p != -1) item[mpline.substring(0, p).trim { it <= ' ' }.toLowerCase()] =
                            mpline.substring(p + 1).trim { it <= ' ' }
                        mpline = readLine(`in`)
                    }
                    if (mpline != null) {
                        val contentDisposition =
                            item.getProperty("content-disposition")
                        if (contentDisposition == null) {
                            sendError(
                                HTTP_BADREQUEST,
                                "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html"
                            )
                        }
                        val st =
                            StringTokenizer(contentDisposition, "; ")
                        val disposition = Properties()
                        while (st.hasMoreTokens()) {
                            val token = st.nextToken()
                            val p = token.indexOf('=')
                            if (p != -1) disposition[token.substring(0, p).trim { it <= ' ' }
                                .toLowerCase()] = token.substring(p + 1).trim { it <= ' ' }
                        }
                        var pname = disposition.getProperty("name")
                        pname = pname.substring(1, pname.length - 1)
                        var value = ""
                        if (item.getProperty("content-type") == null) {
                            while (mpline != null && mpline.indexOf(boundary) == -1) {
                                mpline = readLine(`in`)
                                if (mpline != null) {
                                    val d = mpline.indexOf(boundary)
                                    value += if (d == -1) mpline else mpline.substring(
                                        0,
                                        d - 2
                                    )
                                }
                            }
                        } else {
                            if (boundarycount > bpositions.size) sendError(
                                HTTP_INTERNALERROR,
                                "Error processing request"
                            )
                            val offset =
                                stripMultipartHeaders(fbuf, bpositions[boundarycount - 2])
                            val path = saveTmpFile(
                                fbuf,
                                offset,
                                bpositions[boundarycount - 1] - offset - 4
                            )
                            files[pname] = path
                            value = disposition.getProperty("filename")
                            value = value.substring(1, value.length - 1)
                            do {
                                mpline = readLine(`in`)
                            } while (mpline != null && mpline.indexOf(boundary) == -1)
                        }
                        parms[pname] = value
                    }
                }
            } catch (ioe: IOException) {
                sendError(
                    HTTP_INTERNALERROR,
                    "SERVER INTERNAL ERROR: IOException: " + ioe.message
                )
            }
        }

        /**
         * Find byte index separating header from body.
         * It must be the last byte of the first two sequential new lines.
         */
        private fun findHeaderEnd(buf: ByteArray, rlen: Int): Int {
            var splitbyte = 0
            while (splitbyte + 3 < rlen) {
                if (buf[splitbyte] == '\r'.code.toByte() && buf[splitbyte + 1] == '\n'.code.toByte() && buf[splitbyte + 2] == '\r'.code.toByte() && buf[splitbyte + 3] == '\n'.code.toByte()
                ) return splitbyte + 4
                splitbyte++
            }
            return 0
        }

        /**
         * Find the byte positions where multipart boundaries start.
         */
        fun getBoundaryPositions(b: ByteArray, boundary: ByteArray): IntArray {
            var matchcount = 0
            var matchbyte = -1
            val matchbytes: Vector<Int> = Vector<Int>()
            run {
                var i = 0
                while (i < b.size) {
                    if (b[i] == boundary[matchcount]) {
                        if (matchcount == 0) matchbyte = i
                        matchcount++
                        if (matchcount == boundary.size) {
                            matchbytes.addElement(matchbyte)
                            matchcount = 0
                            matchbyte = -1
                        }
                    } else {
                        i -= matchcount
                        matchcount = 0
                        matchbyte = -1
                    }
                    i++
                }
            }
            val ret = IntArray(matchbytes.size)
            for (i in ret.indices) {
                ret[i] = (matchbytes.elementAt(i) as Int).toInt()
            }
            return ret
        }

        /**
         * Retrieves the content of a sent file and saves it
         * to a temporary file.
         * The full path to the saved file is returned.
         */
        private fun saveTmpFile(b: ByteArray, offset: Int, len: Int): String {
            var path = ""
            if (len > 0) {
                val tmpdir = context.cacheDir.absolutePath
                if (tmpdir?.contains("cache", true) == false) {
                    LogManager.i("Temporary directory is not accepted")
                    return path
                }
                try {
                    val temp =
                        File.createTempFile("NanoHTTPD", "", File(tmpdir))
                    val fstream: OutputStream = FileOutputStream(temp)
                    fstream.write(b, offset, len)
                    fstream.close()
                    path = temp.absolutePath
                } catch (e: Exception) { // Catch exception if any
                    myErr.println("Error: Unable to generate temporary file")
                }
            }
            return path
        }

        /**
         * It returns the offset separating multipart file headers
         * from the file's data.
         */
        private fun stripMultipartHeaders(b: ByteArray, offset: Int): Int {
            var i = 0
            i = offset
            while (i < b.size) {
                if (b[i] == '\r'.code.toByte() && b[++i] == '\n'.code.toByte() && b[++i] == '\r'.code.toByte() && b[++i] == '\n'.code.toByte()) break
                i++
            }
            return i + 1
        }

        /**
         * Decodes the percent encoding scheme. <br></br>
         * For example: "an+example%20string" -> "an example string"
         */
        @Throws(InterruptedException::class)
        private fun decodePercent(str: String?): String? {
            return try {
                val sb = StringBuffer()
                var i = 0
                while (i < str!!.length) {
                    val c = str[i]
                    when (c) {
                        '+' -> sb.append(' ')
                        '%' -> {
                            sb.append(str.substring(i + 1, i + 3).toInt(16).toChar())
                            i += 2
                        }
                        else -> sb.append(c)
                    }
                    i++
                }
                sb.toString()
            } catch (e: Exception) {
                sendError(HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.")
                null
            }
        }

        /**
         * Decodes parameters in percent-encoded URI-format
         * ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and
         * adds them to given Properties. NOTE: this doesn't support multiple
         * identical keys due to the simplicity of Properties -- if you need multiples,
         * you might want to replace the Properties with a Hashtable of Vectors or such.
         */
        @Throws(InterruptedException::class)
        private fun decodeParms(parms: String?, p: Properties) {
            if (parms == null) return
            val st = StringTokenizer(parms, "&")
            while (st.hasMoreTokens()) {
                val e = st.nextToken()
                val sep = e.indexOf('=')
                if (sep >= 0) p[decodePercent(e.substring(0, sep))!!.trim { it <= ' ' }] =
                    decodePercent(e.substring(sep + 1))
            }
        }

        /**
         * Returns an error message as a HTTP response and
         * throws InterruptedException to stop further request processing.
         */
        @Throws(InterruptedException::class)
        private fun sendError(status: String, msg: String) {
            sendResponse(
                status,
                MIME_PLAINTEXT,
                null,
                ByteArrayInputStream(msg.toByteArray())
            )
            throw InterruptedException()
        }

        /**
         * Sends given response to the socket.
         */
        private fun sendResponse(
            status: String?,
            mime: String?,
            header: Properties?,
            data: InputStream?
        ) {
            try {
                if (status == null) throw Error("sendResponse(): Status can't be null.")
                val out = mySocket.getOutputStream()
                val pw = PrintWriter(out)
                pw.print("HTTP/1.0 $status \r\n")
                if (mime != null) pw.print("Content-Type: $mime\r\n")
                val gmtFrmt = initGmtFrmt()
                if (header == null || header.getProperty("Date") == null) pw.print(
                    "Date: " + gmtFrmt.format(
                        Date()
                    ) + "\r\n"
                )
                if (header != null) {
                    val e: Enumeration<*> = header.keys()
                    while (e.hasMoreElements()) {
                        val key = e.nextElement() as String
                        val value = header.getProperty(key)
                        pw.print("$key: $value\r\n")
                    }
                }
                pw.print("\r\n")
                pw.flush()
                if (data != null) {
                    var pending =
                        data.available() // This is to support partial sends, see serveFile()
                    val buff = ByteArray(theBufferSize)
                    while (pending > 0) {
                        val read = data.read(
                            buff,
                            0,
                            if (pending > theBufferSize) theBufferSize else pending
                        )
                        if (read <= 0) break
                        out.write(buff, 0, read)
                        pending -= read
                    }
                }
                out.flush()
                out.close()
                data?.close()
            } catch (ioe: IOException) {
                // Couldn't write? No can do.
                try {
                    mySocket.close()
                } catch (t: Throwable) {
                    LogManager.i("NanoHTTPD: close socket failed")
                }
            }
        }

        init {
            val t = Thread(this)
            t.isDaemon = true
            t.start()
        }
    }

    /**
     * URL-encodes everything between "/"-characters.
     * Encodes spaces as '%20' instead of '+'.
     */
    private fun encodeUri(uri: String): String {
        var newUri = ""
        val st = StringTokenizer(uri, "/ ", true)
        while (st.hasMoreTokens()) {
            val tok = st.nextToken()
            newUri += if (tok == "/") "/" else if (tok == " ") "%20" else {
                URLEncoder.encode(tok, "UTF-8")
                // For Java 1.4 you'll want to use this instead:
                // try { newUri += URLEncoder.encode( tok, "UTF-8" ); } catch ( java.io.UnsupportedEncodingException uee ) {}
            }
        }
        return newUri
    }

    private var myServerSocket: ServerSocket? = null
    private var myThread: Thread? = null
    // ==================================================
    // File server code
    // ==================================================
    /**
     * Serves file from homeDir and its' subdirectories (only).
     * Uses only URI, ignores all headers and HTTP parameters.
     */
    fun serveFile(
        uri: String, header: Properties, homeDir: File,
        allowDirectoryListing: Boolean
    ): Response? {
        var uri = uri
        var res: Response? = null
        try {
            // Make sure we won't die of an exception later
            if (!homeDir.isDirectory) res = Response(
                HTTP_INTERNALERROR, MIME_PLAINTEXT,
                "INTERNAL ERRROR: serveFile(): given homeDir is not a directory."
            )
            if (res == null) {
                // Remove URL arguments
                uri = uri.trim { it <= ' ' }.replace(File.separatorChar, '/')
                if (uri.indexOf('?') >= 0) uri = uri.substring(0, uri.indexOf('?'))

                // Prohibit getting out of current directory
                if (uri.startsWith("..") || uri.endsWith("..") || uri.indexOf("../") >= 0) res =
                    Response(
                        HTTP_FORBIDDEN, MIME_PLAINTEXT,
                        "FORBIDDEN: Won't serve ../ for security reasons."
                    )
            }
            var f = File(homeDir, uri)
            if (res == null && !f.exists()) res = Response(
                HTTP_NOTFOUND, MIME_PLAINTEXT,
                "Error 404, file not found."
            )

            // List the directory, if necessary
            if (res == null && f.isDirectory) {
                // Browsers get confused without '/' after the
                // directory, send a redirect.
                if (!uri.endsWith("/")) {
                    uri += "/"
                    res = Response(
                        HTTP_REDIRECT, MIME_HTML,
                        "<html><body>Redirected: <a href=\"" + uri + "\">" +
                                uri + "</a></body></html>"
                    )
                    res.addHeader("Location", uri)
                }
                if (res == null) {
                    // First try index.html and index.htm
                    if (File(f, "index.html").exists()) f =
                        File(homeDir, "$uri/index.html") else if (File(
                            f,
                            "index.htm"
                        ).exists()
                    ) f = File(
                        homeDir,
                        "$uri/index.htm"
                    ) else if (allowDirectoryListing && f.canRead()) {
                        val files = f.list()
                        var msg = "<html><body><h1>Directory $uri</h1><br/>"
                        if (uri.length > 1) {
                            val u = uri.substring(0, uri.length - 1)
                            val slash = u.lastIndexOf('/')
                            if (slash >= 0 && slash < u.length) msg += "<b><a href=\"" + uri.substring(
                                0,
                                slash + 1
                            ) + "\">..</a></b><br/>"
                        }
                        if (files != null) {
                            for (i in files.indices) {
                                val curFile = File(f, files[i])
                                val dir = curFile.isDirectory
                                if (dir) {
                                    msg += "<b>"
                                    files[i] += "/"
                                }
                                msg += "<a href=\"" + encodeUri(uri + files[i]) + "\">" +
                                        files[i] + "</a>"

                                // Show file size
                                if (curFile.isFile) {
                                    val len = curFile.length()
                                    msg += " &nbsp;<font size=2>("
                                    msg += if (len < 1024) "$len bytes" else if (len < 1024 * 1024) (len / 1024).toString() + "." + len % 1024 / 10 % 100 + " KB" else (len / (1024 * 1024)).toString() + "." + len % (1024 * 1024) / 10 % 100 + " MB"
                                    msg += ")</font>"
                                }
                                msg += "<br/>"
                                if (dir) msg += "</b>"
                            }
                        }
                        msg += "</body></html>"
                        res = Response(
                            HTTP_OK,
                            MIME_HTML,
                            msg
                        )
                    } else {
                        res = Response(
                            HTTP_FORBIDDEN, MIME_PLAINTEXT,
                            "FORBIDDEN: No directory listing."
                        )
                    }
                }
            }
            try {
                if (res == null) {
                    // Get MIME type from file name extension, if possible
                    var mime: String? = null
                    val dot = f.canonicalPath.lastIndexOf('.')
                    if (dot >= 0) mime =
                        theMimeTypes[f.canonicalPath.substring(dot + 1).toLowerCase()] as String?
                    if (mime == null) mime = MIME_DEFAULT_BINARY

                    // Calculate etag
                    val etag =
                        Integer.toHexString((f.absolutePath + f.lastModified() + "" + f.length()).hashCode())

                    // Support (simple) skipping:
                    var startFrom: Long = 0
                    var endAt: Long = -1
                    var range = header.getProperty("range")
                    if (range != null && range.startsWith("bytes=")) {
                        range = range.substring("bytes=".length)
                        val minus = range.indexOf('-')
                        try {
                            if (minus > 0) {
                                startFrom = range.substring(0, minus).toLong()
                                endAt = range.substring(minus + 1).toLong()
                            }
                        } catch (nfe: NumberFormatException) {
                            LogManager.i("NanoHTTPD: Unable to format string to number")
                        }
                    }

                    // Change return code and add Content-Range header when skipping is requested
                    val fileLen = f.length()
                    if (range != null && startFrom >= 0) {
                        if (startFrom >= fileLen) {
                            res = Response(
                                HTTP_RANGE_NOT_SATISFIABLE,
                                MIME_PLAINTEXT,
                                ""
                            )
                            res.addHeader("Content-Range", "bytes 0-0/$fileLen")
                            res.addHeader("ETag", etag)
                        } else {
                            if (endAt < 0) endAt = fileLen - 1
                            var newLen = endAt - startFrom + 1
                            if (newLen < 0) newLen = 0
                            val dataLen = newLen
                            val fis: FileInputStream = object : FileInputStream(f) {
                                @Throws(IOException::class)
                                override fun available(): Int {
                                    return dataLen.toInt()
                                }
                            }
                            val bytesSkipped = fis.skip(startFrom)
                            if ( bytesSkipped != startFrom) {
                                LogManager.i("The actual number of bytes skipped was not as expected ${bytesSkipped/startFrom}")
                            }
                            res = Response(
                                HTTP_PARTIALCONTENT,
                                mime,
                                fis
                            )
                            res.addHeader("Content-Length", "" + dataLen)
                            res.addHeader(
                                "Content-Range",
                                "bytes $startFrom-$endAt/$fileLen"
                            )
                            res.addHeader("ETag", etag)
                        }
                    } else {
                        if (etag == header.getProperty("if-none-match")) res =
                            Response(
                                HTTP_NOTMODIFIED,
                                mime,
                                ""
                            ) else {
                            res = Response(
                                HTTP_OK,
                                mime,
                                FileInputStream(f)
                            )
                            res.addHeader("Content-Length", "" + fileLen)
                            res.addHeader("ETag", etag)
                        }
                    }
                }
            } catch (ioe: IOException) {
                res = Response(
                    HTTP_FORBIDDEN,
                    MIME_PLAINTEXT,
                    "FORBIDDEN: Reading file failed."
                )
            }
            res!!.addHeader(
                "Accept-Ranges",
                "bytes"
            ) // Announce that the file server accepts partial content requestes
        } catch (e: Exception) {
            LogManager.d("NanoHTTPD: Processing failed")
        }
        return res
    }

    companion object {
        /**
         * Some HTTP response status codes
         */
        const val HTTP_OK = "200 OK"
        const val HTTP_PARTIALCONTENT = "206 Partial Content"
        const val HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable"
        const val HTTP_REDIRECT = "301 Moved Permanently"
        const val HTTP_NOTMODIFIED = "304 Not Modified"
        const val HTTP_FORBIDDEN = "403 Forbidden"
        const val HTTP_NOTFOUND = "404 Not Found"
        const val HTTP_BADREQUEST = "400 Bad Request"
        const val HTTP_INTERNALERROR = "500 Internal Server Error"
        const val HTTP_NOTIMPLEMENTED = "501 Not Implemented"

        /**
         * Common mime types for dynamic content
         */
        const val MIME_PLAINTEXT = "text/plain"
        const val MIME_HTML = "text/html"
        const val MIME_DEFAULT_BINARY = "application/octet-stream"
        const val MIME_XML = "text/xml"

        const val MAX_STR_LEN = 2048

        /**
         * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
         */
        private val theMimeTypes: Hashtable<String, String> =
            Hashtable<String, String>()
        private const val theBufferSize = 16 * 1024

        // Change these if you want to log to somewhere else than stdout
        protected var myOut = System.out
        protected var myErr = System.err

        /**
         * GMT date formatter
         */
        private var gmtFrmt: SimpleDateFormat? = null

        init {
            val st = StringTokenizer(
                "css		text/css " +
                        "htm		text/html " +
                        "html		text/html " +
                        "xml		text/xml " +
                        "txt		text/plain " +
                        "asc		text/plain " +
                        "gif		image/gif " +
                        "jpg		image/jpeg " +
                        "jpeg		image/jpeg " +
                        "png		image/png " +
                        "mp3		audio/mpeg " +
                        "m3u		audio/mpeg-url " +
                        "mp4		video/mp4 " +
                        "ogv		video/ogg " +
                        "flv		video/x-flv " +
                        "mov		video/quicktime " +
                        "swf		application/x-shockwave-flash " +
                        "js			application/javascript " +
                        "pdf		application/pdf " +
                        "doc		application/msword " +
                        "ogg		application/x-ogg " +
                        "zip		application/octet-stream " +
                        "exe		application/octet-stream " +
                        "class		application/octet-stream "
            )
            while (st.hasMoreTokens()) {
                theMimeTypes[st.nextToken()] = st.nextToken()
            }
        }

        init {
            gmtFrmt = initGmtFrmt()
        }

        private fun initGmtFrmt(): SimpleDateFormat {
            val gmtFrmt =
                    SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US)
            gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"))
            return gmtFrmt
        }
    }
    // ==================================================
    // Socket & server code
    // ==================================================
    /**
     * Starts a HTTP server to given port.
     *
     *
     * Throws an IOException if the socket is already in use
     */
    init {
        try {
            myServerSocket = ServerSocket(myTcpPort, 1, InetSocketAddress(ip,myTcpPort).address)
            myThread = Thread(Runnable {
                try {
                    while (true) {
                        myServerSocket?.let {
                            HTTPSession(it.accept())
                        }
                    }
                } catch (ioe: IOException) {
                    LogManager.i("NanoHTTPD: Unable to create server socket")
                }
            })
            myThread?.setDaemon(true)
            myThread?.start()
        } catch (ex: Exception) {
            LogManager.d("NanoHTTPD: Create server failed")
        }
    }
}