package com.example.todo.utils.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class CopyWriteFile {
    private File originalFile;
    private File copyFile;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private FileReader fileReader;
    private FileWriter fileWriter;

    public CopyWriteFile(File originalFile, File copyFile) {
        this.originalFile = originalFile;
        this.copyFile = copyFile;
    }

    public void copyTextFile() throws IOException {
        fileReader = new FileReader(originalFile.getPath());
        bufferedReader = new BufferedReader(fileReader);

        fileWriter = new FileWriter(copyFile.getName());

        bufferedWriter = new BufferedWriter(fileWriter);
        while (bufferedReader.readLine() != null) {
            bufferedWriter.write(bufferedReader.readLine());
        }

        bufferedReader.close();
    }
}
