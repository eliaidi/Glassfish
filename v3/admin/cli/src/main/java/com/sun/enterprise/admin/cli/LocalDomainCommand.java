/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.enterprise.admin.cli;

import com.sun.enterprise.util.SystemPropertyConstants;
import com.sun.enterprise.universal.i18n.LocalStringsImpl;
import com.sun.enterprise.universal.io.SmartFile;
import com.sun.enterprise.universal.xml.MiniXmlParser;
import com.sun.enterprise.universal.xml.MiniXmlParserException;
import com.sun.enterprise.security.store.PasswordAdapter;

import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Set;

/**
 * A class that's supposed to capture all the behavior common to operation
 * on a "local" domain.  It's supposed to act as the abstract base class that
 * provides more functionality to the commands that operate on a local domain.
 * @author &#2325;&#2375;&#2342;&#2366;&#2352 (km@dev.java.net)
 */
public abstract class LocalDomainCommand extends CLICommand {

    protected File   domainsDir;
    protected File   domainRootDir;
    protected String domainName;
    
    private static final LocalStringsImpl strings =
            new LocalStringsImpl(LocalDomainCommand.class);

    @Override
    protected void validate()
                        throws CommandException, CommandValidationException {
        super.validate();
        initDomain();
    }
    
    protected void initDomain() throws CommandValidationException {
        if (!operands.isEmpty()) {
            domainName = operands.get(0);
        }

        // get domainsDir
        String domaindir = getOption("domaindir");

        if (ok(domaindir)) {
            domainsDir = new File(domaindir);
            if (!domainsDir.isDirectory()) {
                throw new CommandValidationException(
                        strings.get("Domain.badDomainsDir", domainsDir));
            }
        }
        if (domainsDir == null) {
            domainsDir = new File(getSystemProperty(
                            SystemPropertyConstants.DOMAINS_ROOT_PROPERTY));
        }

        if (!domainsDir.isDirectory()) {
            throw new CommandValidationException(
                    strings.get("Domain.badDomainsDir", domainsDir));
        }

        if (domainName != null) {
            domainRootDir = new File(domainsDir, domainName);
        } else {
            domainRootDir = getTheOneAndOnlyDomain(domainsDir);
            domainName    = domainRootDir.getName();
        }

        if (!domainRootDir.isDirectory()) {
            throw new CommandValidationException(
                    strings.get("Domain.badDomainDir", domainRootDir));
        }
        domainRootDir = SmartFile.sanitize(domainRootDir);
        domainsDir    = SmartFile.sanitize(domainsDir);

        initializeLocalPassword(domainRootDir);
    }
    
    private File getTheOneAndOnlyDomain(File parent)
            throws CommandValidationException {
        // look for subdirs in the parent dir -- there must be one and only one

        File[] files = parent.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory();
            }
        });

        if (files == null || files.length == 0) {
            throw new CommandValidationException(
                    strings.get("Domain.noDomainDirs", parent));
        }

        if (files.length > 1) {
            throw new CommandValidationException(
                    strings.get("Domain.tooManyDomainDirs", parent));
        }
        return files[0];
    }
    
    protected File getDomainXml() throws CommandValidationException {
        // root-dir/config/domain.xml
        File domainXml = new File(new File(domainRootDir, "config"),
                                    "domain.xml");

        if (!domainXml.canRead()) {
            throw new CommandValidationException(
                    strings.get("Domain.noDomainXml", domainXml));
        }
        return domainXml;
    }

    protected File getMasterPasswordFile() {
        File mp = new File(domainRootDir, "master-password");
        if (!mp.canRead())
            return null;
        return mp;
    }

    /**
     * If there's a local-password file, use the local password so the
     * user never has to enter a password.
     */
    protected void initializeLocalPassword(File domainRootDir) {
        // root-dir/config/local-password
        File localPassword = new File(new File(domainRootDir, "config"),
                                    "local-password");
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(localPassword));
            String pwd = r.readLine();
            if (ok(pwd)) {
                // use the local password
                logger.printDebugMessage("Using local password");
                programOpts.setPassword(pwd);
                // if no user specified, use the default
                if (programOpts.getUser() == null)
                    programOpts.setUser(
                                    SystemPropertyConstants.DEFAULT_ADMIN_USER);
            }
        } catch (IOException ex) {
            logger.printDebugMessage(
                "IOException reading local password: " + ex);
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) { }
            }
        }
    }
    
    protected File getJKS() {
        File mp = new File(new File(domainRootDir, "config"), "keystore.jks");
        if (!mp.canRead())
            return null;
        return mp;
    }

    protected boolean verifyMasterPassword(String mpv) {
        // only tries to open the keystore
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getJKS());
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, mpv.toCharArray());
            return true;
        } catch (Exception e) {
            logger.printDebugMessage(e.getMessage());
            return false;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ioe) {
                // ignore, I know ...
            }
        }
    }

    /**
     * Checks if the create-domain was created using --savemasterpassword flag
     * which obtains security by obfuscation! Returns null in case of failure
     * of any kind.
     * @return String representing the password from the JCEKS store named
     *          master-password in domain folder
     */
    protected String checkMasterPasswordFile() {
        File mpf = getMasterPasswordFile();
        if (mpf == null)
            return null;   // no master password  saved
        try {
            PasswordAdapter pw = new PasswordAdapter(mpf.getAbsolutePath(),
                                "master-password".toCharArray()); // fixed key
            return pw.getPasswordForAlias("master-password");
        } catch (Exception e) {
            logger.printDebugMessage("master password file reading error: " +
                                        e.getMessage());
            return null;
        }
    }

    /**
     * Returns the admin port of the local domain. Note that this method should
     * be called only when you own the domain that is available on accessible
     * file system.
     *
     * @param domainXml the domain.xml file
     * @return an integer that represents admin port
     * @throws CommandException in case of parsing errors
     * @throws CommandValidationException in case of parsing errors 
     */
    protected int getAdminPort(File domainXml)
                        throws CommandValidationException, CommandException {
        Integer[] ports;

        try {
            MiniXmlParser parser = new MiniXmlParser(domainXml);
            Set<Integer> portsSet = parser.getAdminPorts();
            ports = portsSet.toArray(new Integer[portsSet.size()]);
            return ports[0];
        } catch (MiniXmlParserException ex) {
            throw new CommandException("admin port not found", ex);
        }
    }

    /**
     * There is sometimes a need for subclasses to know if a
     * <code> local domain </code> is running. An example of such a command is
     * change-master-password command. The stop-domain command also needs to
     * know if a domain is running <i> without </i> having to provide user
     * name and password on command line (this is the case when I own a domain
     * that has non-default admin user and password) and want to stop it
     * without providing it.
     * <p>
     * In such cases, we need to know if the domain is running and this method
     * provides a way to do that.
     *
     * @return boolean indicating whether the server is running
     */
    protected boolean isRunning(int port) {
        Socket server = null;
        try {
            String host = null;
            server = new Socket(host, port);
            return true;
        } catch (Exception ex) {
            logger.printDebugMessage("\nisRunning got exception: " + ex);
            return false;
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException ex) { }
            }
        }
    }
}
