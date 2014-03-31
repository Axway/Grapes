package org.axway.grapes.commons.utils;

import java.io.*;

/**
 * FileUtils
 *
 * <p>Provides generic methods </p>
 */
public class FileUtils {

    /**
     * Serialize a content into a targeted file, checking that the parent directory exists.
     *
     * @param folder File
     * @param content String
     * @param fileName String
     */
    public static void serialize(final File folder, final String content, final String fileName) throws IOException {
        if(!folder.exists()){
            folder.mkdirs();
        }

        final File output = new File(folder, fileName);
        FileWriter writer = null;

        try {
            writer = new FileWriter(output);
            writer.write(content);
            writer.flush();
        }
        catch (Exception e){
            throw new IOException("Failed to serialize the notification in folder " + folder.getPath(), e);
        }
        finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new IOException("Failed to close the open file " + output.getPath(), e);
                }
            }
        }
    }

    /**
     * Reads a file and returns the result in a String
     *
     * @param file File
     * @return String
     * @throws IOException
     */
    public static String read(final File file) throws IOException {
        final StringBuilder sb = new StringBuilder();
        BufferedReader br = null;

        try {
            String sCurrentLine;

            br = new BufferedReader(new FileReader(file));

            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
            }

        } catch (IOException e) {
            throw new IOException("Failed to read file: " + file.getAbsolutePath(), e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException e) {
                throw new IOException("Failed to close file reader on: " + file.getAbsolutePath(), e);
            }
        }

        return sb.toString();
    }

    /**
     * Get file size
     *
     * @return Long
     */
    public static Long getSize(final File file){
        if ( file!=null && file.exists() ){
            return file.length();
        }
        return null;
    }
}
