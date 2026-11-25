package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val PDF_TAG = "LabPdfHelper"

data class RenderedLabPdf(
    val bitmap: Bitmap?,
    val pageCount: Int,
    val renderedPages: Int
)

object LabPdfHelper {

    /**
     * Renders up to the first 2 pages of a user-selected lab report PDF into a crisp,
     * stacked white-background Bitmap so Gemini multimodal vision can read the actual report.
     */
    suspend fun renderPdfToBitmap(context: Context, uri: Uri): RenderedLabPdf = withContext(Dispatchers.IO) {
        try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext RenderedLabPdf(null, 0, 0)

            pfd.use { descriptor ->
                PdfRenderer(descriptor).use { renderer ->
                    val totalPages = renderer.pageCount
                    if (totalPages <= 0) return@withContext RenderedLabPdf(null, 0, 0)

                    val pagesToRender = minOf(totalPages, 2)
                    val pageBitmaps = mutableListOf<Bitmap>()
                    val targetWidth = 1240

                    for (pageIndex in 0 until pagesToRender) {
                        renderer.openPage(pageIndex).use { page ->
                            val scale = targetWidth.toFloat() / page.width.coerceAtLeast(1)
                            val targetHeight = (page.height * scale).toInt().coerceIn(400, 1900)
                            val bmp = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bmp)
                            canvas.drawColor(Color.WHITE)
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            pageBitmaps.add(bmp)
                        }
                    }

                    val combined = if (pageBitmaps.size == 1) {
                        pageBitmaps.first()
                    } else {
                        val totalHeight = pageBitmaps.sumOf { it.height }
                        val merged = Bitmap.createBitmap(targetWidth, totalHeight, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(merged)
                        canvas.drawColor(Color.WHITE)
                        var currentY = 0f
                        pageBitmaps.forEach { bmp ->
                            canvas.drawBitmap(bmp, 0f, currentY, null)
                            currentY += bmp.height
                            bmp.recycle()
                        }
                        merged
                    }

                    RenderedLabPdf(
                        bitmap = combined,
                        pageCount = totalPages,
                        renderedPages = pagesToRender
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(PDF_TAG, "Failed to render lab PDF", e)
            RenderedLabPdf(null, 0, 0)
        }
    }
}
