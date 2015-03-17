package com.dcp.sm.web.sftp;

import java.io.File;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;


public class JschFactory
{
    private String initRemDir;//Initial Remote Directory
    
    private Session session;
    private Channel channel;
    
    private boolean ready = false;
    public boolean isReady() { if (ready) { ready = false; return true;} return false; }
    
    public JschFactory(String host, String username, String password, String initRemDir) throws JSchException
    {
        if (!initRemDir.endsWith("/")) initRemDir = initRemDir.concat("/");
        this.initRemDir = initRemDir;
        
        session = (new JSch()).getSession(username, host, 22);
        //ssh.setKnownHosts("/path/of/known_hosts/file");
        session.setPassword(password);
        session.setOutputStream(System.out);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(30000);
        session.connect();
        channel = session.openChannel("sftp");
        channel.setOutputStream(System.out);
        channel.connect();
    }
    
    /**
     * Send a file to server path via SFTP
     * @param src
     * @param path
     * @throws SftpException
     */
    public void put(final File src, String path) throws SftpException
    {
        if (!path.endsWith("/")) path = path.concat("/");
        if (path.startsWith("/")) path = path.substring(1);
        
        ChannelSftp sftp = (ChannelSftp) channel;
        SftpProgressMonitor progress = new SftpProgressMonitor() {
            
            @Override public void init(int arg0, String arg1, String arg2, long arg3)
            {
                System.out.println("File transfer begin..");
            }
            
            @Override public void end()
            {
                Out.print(LOG_LEVEL.INFO, "Upload of file "+ src.getName() +" succeeded.");
                ready = true;
            }
            
            @Override public boolean count(long i) { return false; }
        };
        sftp.put(src.getAbsolutePath(), initRemDir+path+src.getName(), progress);
    }
    
    
    public void disconnect()
    {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }
    
    public static void main(String[] args)
    {
        JschFactory test = null;
        try {
            test = new JschFactory("web.sourceforge.net", "jackred", "password", "/home/project-web/devcompack/htdocs");
            File file = new File("build.xml");
            System.out.println("Uploading file");
            test.put(file, "files/releases/1.0.1/");
        }
        catch (JSchException e) {
            e.printStackTrace();
        }
        catch (SftpException e) {
            e.printStackTrace();
        }
        finally {
            test.disconnect();
        }
    }

}
