package com.styl.pa.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*


/**
 * Created by Ngatran on 03/20/2019.
 */
@ExcludeFromJacocoGeneratedReport
object PdfUtils {
    private val pdfName = "receipt.pdf"
    private var file: File? = null
    private var document: Document? = null

    var boldFont1 = Font(
            Font.FontFamily.TIMES_ROMAN, 18f,
            Font.BOLD
    )
    var normalFont1 = Font(
            Font.FontFamily.TIMES_ROMAN, 18f,
            Font.NORMAL
    )
    var underlineFont1 = Font(
            Font.FontFamily.TIMES_ROMAN, 18f,
            Font.UNDERLINE
    )
    var italicFont1 = Font(
            Font.FontFamily.TIMES_ROMAN, 18f,
            Font.ITALIC
    )

    private fun getApplicationFolder(context: Context?): File? {
        try {
            val folderPath = context?.externalCacheDir.toString()
            val folder = File(folderPath)
            if (!folder.exists() && !GeneralUtils.mkdirs(folder)) {
                LogManager.i("Created cache folder")
            }

            return folder

        } catch (ex: Exception) {
            Toast.makeText(context, "error", Toast.LENGTH_LONG).show()
        }

        return null
    }

    private fun createPDFFile(context: Context?) {
        file = File(getApplicationFolder(context), "/" + pdfName)

        try {
            if (true == file?.exists()) {
                file?.delete()
            }
        } catch (e: IOException) {
            LogManager.i("Create PDF File failed!")
        }
    }

    fun openDocument(context: Context?) {
        // removed - not use pdf receipt anymore
    }

    fun closeDocument() {
        try {
            document?.close()
        } catch (e: Exception) {
            LogManager.i("Close document failed!")
        }
    }

    fun writeText(content: String?, align: Int, font: Font, marginStart: Float, marginEnd: Float) {
        if (document != null && !content.isNullOrEmpty()) {
            try {
                val paragraph = Paragraph(content, font)
                paragraph.alignment = align
                paragraph.indentationLeft = marginStart
                paragraph.indentationRight = marginEnd
                document?.add(paragraph)
            } catch (e: Exception) {
                LogManager.i("PDF write text failed")
            }
        }
    }

    fun createTableNoneBorder(
            column: Int,
            listSize: FloatArray,
            listAlign: IntArray,
            contentList: ArrayList<String>,
            font: Font,
            marginStart: Float,
            marginEnd: Float
    ) {
        try {


            val paragraph = Paragraph()
            paragraph.indentationLeft = marginStart
            paragraph.indentationRight = marginEnd
            val table = PdfPTable(column)
            table.widthPercentage = 100f
            table.setWidths(listSize)
            table.setTotalWidth(listSize)
            table.isLockedWidth = true
            for (i in 0 until column) {
                val c1 = PdfPCell(Phrase(contentList[i], font))
                c1.horizontalAlignment = listAlign[i]
                c1.border = 0
                table.addCell(c1)
            }
            paragraph.add(table)
            document?.add(table)
        } catch (e: Exception) {
            LogManager.i("Create table non-border failed")
        }
    }

    fun addImage(image: Bitmap?) {
        if (image != null) {
            try {
                val stream = ByteArrayOutputStream()

                val width = (2 * (document?.pageSize?.width ?: 0f)) / 3
                val height = (width * image.height) / image.width

                val img = GeneralUtils.getResizedBitmap(image, width.toInt(), height.toInt())

                img.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bitmapData = stream.toByteArray()
                val pdfImage = Image.getInstance(bitmapData)
                pdfImage.backgroundColor = BaseColor.WHITE

                pdfImage.alignment = Element.ALIGN_CENTER
                document?.add(pdfImage)
            } catch (e: Exception) {
                LogManager.i("Add image to document failed")
            }
        }
    }

    fun generateLine(characters: Int) {
        var line = "-"
        for (i in 0 until characters) {
            line += "-"
        }
        writeText(line, Element.ALIGN_LEFT, normalFont1, 0f, 0f)
    }

    fun convertBase64(): String? {
        if (file != null) {
            val base64 = GeneralUtils.parsePdfToBase64(file!!)
            file?.delete()
            return base64
        } else {
            return null
        }
    }
}