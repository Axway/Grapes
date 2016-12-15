package org.axway.grapes.commons.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * FileUtils
 *
 * <p>Provides generic methods </p>
 */
public final class FileUtils {

    private FileUtils(){
        // hide utility class constructor
    }

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
                writer.close();
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
            if (br != null){br.close();}
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

    /**
     * Creates a file
     *
     * @param folder File
     * @param fileName String
     * @throws IOException
     */
    public static void touch(final File folder , final String fileName) throws IOException {
        if(!folder.exists()){
            folder.mkdirs();
        }

        final File touchedFile = new File(folder, fileName);

        // The JVM will only 'touch' the file if you instantiate a
        // FileOutputStream instance for the file in question.
        // You don't actually write any data to the file through
        // the FileOutputStream.  Just instantiate it and close it.
        FileOutputStream doneFOS = null;

        try {
            doneFOS = new FileOutputStream(touchedFile);
        }
        catch (FileNotFoundException e) {
            // Handle error
        }
        finally {
            if(doneFOS != null){
                doneFOS.close();
            }
        }  
    }

	public static String getFileChecksumSHA256(final File artifactFile) throws IOException{
    	MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
		
    	FileInputStream fis = new FileInputStream(artifactFile);
	    
	    byte[] byteArray = new byte[1024];
	    int bytesCount = 0; 
	    
	    while ((bytesCount = fis.read(byteArray)) != -1) {
	        digest.update(byteArray, 0, bytesCount);
	    }
	    
	    fis.close();
	   
	    byte[] bytes = digest.digest();
	    StringBuilder sb = new StringBuilder();
	    for(int countr=0; countr< bytes.length ;countr++){
	        sb.append(Integer.toString((bytes[countr] & 0xff) + 0x100, 16).substring(1));
	    }
	   
	   return sb.toString();
	}
    
}
