/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package com.sun.enterprise.tools.verifier.web;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.sun.enterprise.deployment.Descriptor;
import com.sun.enterprise.deployment.WebBundleDescriptor;
import com.sun.enterprise.deployment.util.ModuleDescriptor;
import com.sun.enterprise.tools.verifier.BaseVerifier;
import com.sun.enterprise.tools.verifier.FrameworkContext;
import com.sun.enterprise.tools.verifier.SpecVersionMapper;
import com.sun.enterprise.tools.verifier.apiscan.classfile.ClassFileLoaderFactory;
import com.sun.enterprise.tools.verifier.apiscan.packaging.ClassPathBuilder;
import com.sun.enterprise.tools.verifier.apiscan.stdapis.WebClosureCompiler;
import com.sun.enterprise.util.FileUtil;
import com.sun.enterprise.util.io.FileUtils;
import com.sun.enterprise.loader.EJBClassLoader;

/**
 * Responsible for verfying the j2ee war archive.
 *
 * @author Vikas Awasthi
 */
public class WebVerifier extends BaseVerifier {

    private WebBundleDescriptor webd = null;
    private String classPath;//this is lazily populated in getClassPath()
    private boolean isASMode = false;
    private File jspOutDir = null;

    public WebVerifier(FrameworkContext frameworkContext,
                       WebBundleDescriptor webd) {
        this.frameworkContext = frameworkContext;
        this.webd = webd;
        this.isASMode = !frameworkContext.isPortabilityMode();
    }

    /**
     * Responsible for running web based verifier tests on the the web archive.
     * Called from runVerifier in {@link BaseVerifier} class.
     *
     * @throws Exception
     */
    public void verify() throws Exception {
        if (areTestsNotRequired(frameworkContext.isWeb()) &&
                areTestsNotRequired(frameworkContext.isWebServices()) &&
                areTestsNotRequired(frameworkContext.isWebServicesClient()) &&
                areTestsNotRequired(frameworkContext.isPersistenceUnits()))
            return;

        jspOutDir = getJspOutDir();
        try {
            preVerification();
            context.setOutDir(jspOutDir);
            createClosureCompiler();
            verify(webd, new WebCheckMgrImpl(frameworkContext));
        } finally {
            // frameworkContext.getJspOutDir() will be non-null only when the 
            // call is from deployment backend and precompilejsp is set
            if(frameworkContext.getJspOutDir()==null)
                FileUtil.deleteDir(jspOutDir);
        }
    }

    /**
     *
     * @return web bundle descriptor
     */
    public Descriptor getDescriptor() {
        return webd;
    }

    /**
     * Creates the ClassLoader for the war archive.
     *
     * @return ClassLoader
     * @throws IOException
     */
    protected ClassLoader createClassLoader()
            throws IOException {
        EJBClassLoader ejbClassLoader = new EJBClassLoader(webd.getClassLoader());
        ejbClassLoader.appendURL(jspOutDir);
        return ejbClassLoader;
    }

    /**
     *
     * @return name of the war archive
     */
    protected String getArchiveUri() {
        return FileUtils.makeFriendlyFileName(webd.getModuleDescriptor().getArchiveUri());
    }

    /**
     * @return the array of deployment descriptor names
     */
    protected String[] getDDString() {
        String dd[] = {"WEB-INF/sun-web.xml", "WEB-INF/web.xml", // NOI18N
                       "WEB-INF/webservices.xml"}; // NOI18N
        return dd;
    }

    /**
     * Creates and returns the class path associated with the web archive.
     * Uses the exploded location of the archive for generating the classpath.
     *
     * @return entire classpath string
     * @throws IOException
     */
    protected String getClassPath() throws IOException {
        if (classPath != null) return classPath;

        if(isASMode)
            return (classPath = getClassPath(frameworkContext.getClassPath()) + 
                                File.pathSeparator + 
                                jspOutDir.getAbsolutePath());

        String cp;
        if (!webd.getModuleDescriptor().isStandalone()) {
            //take the cp from the enclosing ear file
            String ear_uri = frameworkContext.getExplodedArchivePath();
            File ear = new File(ear_uri);
            assert(ear.isDirectory());
            String earCP = ClassPathBuilder.buildClassPathForEar(ear);
            String libdir = webd.getApplication().getLibraryDirectory();
            if (libdir!=null) {
                earCP = getLibdirClasspath(ear_uri, libdir) + earCP;
            }
            String module_uri = webd.getModuleDescriptor().getArchiveUri();//this is a relative path
            File module = new File(module_uri);
            assert(module.isFile() && !module.isAbsolute());
            // exploder creates the directory replacing all dots by '_'
            File explodedModuleDir = new File(ear_uri,
                    FileUtils.makeFriendlyFileName(module_uri));
            String moduleCP = ClassPathBuilder.buildClassPathForWar(
                    explodedModuleDir);
            cp = moduleCP + File.pathSeparator + earCP;
        } else {
            String module_uri = frameworkContext.getExplodedArchivePath();//this is an absolute path
            File module = new File(module_uri);
            assert(module.isDirectory() && module.isAbsolute());
            cp = ClassPathBuilder.buildClassPathForWar(module);
        }
        String as_lib_root=System.getProperty("com.sun.aas.installRoot")+File.separator+"lib"+File.separator;
        if (frameworkContext.getJavaEEVersion().compareTo("5") >= 0) { // NOI18N
            cp += File.pathSeparator+as_lib_root+"jsf-impl.jar"+File.pathSeparator+ // NOI18N
                  as_lib_root+"appserv-jstl.jar"+File.pathSeparator; // NOI18N
        }
        cp = cp + File.pathSeparator + jspOutDir.getAbsolutePath();
        return (classPath = cp);
    }

    /**
     * creates the ClosureCompiler for the web module and sets it to the
     * verifier context. This is used to compute the closure on the classes used
     * in the web archive.
     *
     * @throws IOException
     */
    protected void createClosureCompiler() throws IOException {
        String specVer = SpecVersionMapper.getWebAppVersion(
                frameworkContext.getJavaEEVersion());
        Object arg = (isASMode)?context.getClassLoader():(Object)getClassPath();
        WebClosureCompiler cc = new WebClosureCompiler(specVer,
                ClassFileLoaderFactory.newInstance(new Object[]{arg}));
        context.setClosureCompiler(cc);
    }

    /**
     * If precompilejsp is set in the backend then return the jspOutput 
     * directory set in the frameworkContext. Otherwise create a new unique 
     * directory and return it.
     * @return the output directory where compiled JSPs will be put.
     */ 
    private File getJspOutDir(){
        // frameworkContext.getJspOutDir() will be non-null only when the 
        // call is from deployment backend and precompilejsp is set
        File jspOutDir = frameworkContext.getJspOutDir();
        if(jspOutDir != null) {
            ModuleDescriptor moduleDescriptor = webd.getModuleDescriptor();
            if(moduleDescriptor.isStandalone())
                return jspOutDir;
            return new File(jspOutDir, FileUtils.makeFriendlyFilename(moduleDescriptor.getArchiveUri()));
        }
        Random random=new Random();
        String prefix=System.getProperty("java.io.tmpdir")+File.separator+".jspc";
        do{
            float f=random.nextFloat();
            String outDirPath=new String(prefix+f);
            File out=new File(outDirPath);
            if(out.mkdirs()) 
                return out;
        }while(true);
    }
}
