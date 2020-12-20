package com.pmprogramms.todo.utils.note.pdf;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class PDFGenerator {
    private String TAG = "GENERATORPDF";
    public static final String mainFolderName = "TodoNotePDF";

    public void generateSpaceForPDF(Context context) {
        File mainFolder = new File(context.getExternalFilesDir(null), mainFolderName);
        if (!mainFolder.exists())
            mainFolder.mkdirs();
    }

    public boolean generatePDF(Context context, String title, View contentScreen, View contentText) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(contentScreen.getWidth(), contentScreen.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        contentText.draw(page.getCanvas());
        pdfDocument.finishPage(page);
        File file = new File(context.getExternalFilesDir(null) + "/" + mainFolderName, title + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            return false;
        }
        pdfDocument.close();

        return file.exists();
    }
}
