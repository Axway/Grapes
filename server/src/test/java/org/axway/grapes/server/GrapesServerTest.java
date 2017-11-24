package org.axway.grapes.server;

import org.apache.commons.io.FileUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.DBException;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.materials.TestingRepositoryHandler;
import org.junit.Test;

import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNull;

/**
 *
 * @author jdcoffre
 */
public class GrapesServerTest extends GrapesServer{
    
    public static final String PROPERTY_PORT = "server.mock.http.port";
	private static final String DEFAULT_PORT = "8074";
    
    public static final String PROPERTY_ADMIN_PORT = "server.mock.http.port.admin";
	private static final String DEFAULT_ADMIN_PORT = "8073";

    @Override
    public RepositoryHandler getRepositoryHandler(final GrapesServerConfig config) throws DBException, UnknownHostException {
        return new TestingRepositoryHandler();
    }

    @Test
    public void checkInitialization() throws UnknownHostException {
        String portProperty = System.getProperty(PROPERTY_PORT, null);
		
		if(portProperty == null || !isInteger(portProperty)){
			Logger.getLogger(GrapesServerTest.class.getSimpleName()).info("No variable HTTP_PORT define, server mock will be run on the default port value: " + DEFAULT_PORT);
            portProperty = DEFAULT_PORT;
		}
        else{
            Logger.getLogger(GrapesServerTest.class.getSimpleName()).info("Uses port " + portProperty + " as grapes http port for tests.");
        }
        
        String adminPortProperty = System.getProperty(PROPERTY_ADMIN_PORT, null);
		
		if(adminPortProperty == null || !isInteger(adminPortProperty)){
            Logger.getLogger(GrapesServerTest.class.getSimpleName()).info("No variable HTTP_ADMIN_PORT define, server mock will be run on the default port value: " + DEFAULT_ADMIN_PORT);
            adminPortProperty = DEFAULT_ADMIN_PORT;
		}
        else{
            Logger.getLogger(GrapesServerTest.class.getSimpleName()).info("Uses port " + adminPortProperty + " as admin port for grapes tests.");
        }
        
        final String templatePath = getClass().getResource("server-conf-template.yml").getPath();
        final String confPath = overrideConfiguration(templatePath, portProperty, adminPortProperty);


        try {
            File templates = new File(templatePath).getParentFile();
            FileUtils.copyFileToDirectory(new File(templates, "all-messages.txt"), new File("target"));
        } catch(IOException e) {
            e.printStackTrace();
        }


        final GrapesServerTest grapesServer = new GrapesServerTest();
        final String[] args = {"server",confPath};
        
        Throwable exception  = null;
        
        try {
            grapesServer.run(args);
        } catch (Exception e) {
            exception = e;
        }
        
        assertNull(exception);
        
    }
    
    private static String overrideConfiguration(final String templateFilePath, final String adminPort, final String port) {
        final List<String> lines = new ArrayList<String>();
        
        final String output = "target/server-conf.yml";

        FileReader reader = null;
        PrintWriter out = null;
        BufferedReader in = null;
        
        try {
            reader = new FileReader(templateFilePath);
            in = new BufferedReader(reader);
            String line = in.readLine();
            while (line != null) {
                line = line.replace("#HTTP_PORT#", port);
                line = line.replace("#HTTP_ADMIN_PORT#", adminPort);
                 lines.add(line);
                 line = in.readLine();
            }
            out = new PrintWriter(new File(output));
            
            for (String l : lines){
                out.println(l);
            }
            
        } 
        catch (Exception e) {
             Logger.getLogger(GrapesServerTest.class.getName()).log(Level.SEVERE, null, e);
        }
        finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(GrapesServerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(GrapesServerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(out != null){
                out.close();
            }
        }
        
        return output;
    }


    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }

        return true;
    }
}
