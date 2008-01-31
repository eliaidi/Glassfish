package com.sun.enterprise.v3.services.impl;

import com.sun.grizzly.tcp.Adapter;
import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.tcp.Response;
import com.sun.grizzly.tcp.ActionCode;
import com.sun.grizzly.standalone.DynamicContentAdapter;
import com.sun.grizzly.util.buf.ByteChunk;
import com.sun.enterprise.module.impl.Utils;
import com.sun.enterprise.config.serverbeans.VirtualServer;
import com.sun.enterprise.config.serverbeans.HttpListener;
import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.ConfigBeansUtilities;
import com.sun.enterprise.v3.data.ContainerRegistry;
import com.sun.enterprise.v3.data.ContainerInfo;
import com.sun.enterprise.v3.server.ContainerStarter;
import com.sun.enterprise.v3.server.HK2Dispatcher;
import com.sun.enterprise.util.StringUtils;
import com.sun.logging.LogDomains;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.glassfish.api.deployment.ApplicationContainer;
import org.glassfish.api.content.FileServer;
import org.glassfish.api.content.WebRequestHandler;
import org.glassfish.api.container.Sniffer;
import org.jvnet.hk2.component.ComponentException;

/**
 * Created by IntelliJ IDEA.
 * User: dochez
 * Date: Jan 16, 2008
 * Time: 3:29:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class VsAdapter extends AbstractAdapter implements Adapter {

    private static final String RFC_2616_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
    
    private final String docRoot;

    private final VirtualServer vs;

    private final Set<String> hostsNames;

    private Map<String, com.sun.grizzly.tcp.Adapter> endpoints = new HashMap<String, com.sun.grizzly.tcp.Adapter>();
    private Map<com.sun.grizzly.tcp.Adapter, ApplicationContainer> apps = new HashMap();
    private List<FileServer> handlers = new ArrayList<FileServer>();

    private HK2Dispatcher dispatcher = new HK2Dispatcher();
    

    public VsAdapter(VirtualServer vs) {
        this.vs = vs;
        this.docRoot = getDocRoot(vs);
        hostsNames = new HashSet(StringUtils.parseStringList(vs.getHosts(), ","));
    }

    public boolean handles(String serverName) {
        return hostsNames.contains(serverName);
    }

    public VirtualServer getVirtualServer() {
        return vs;
    }
    

    /**
     * Returns a meaningful string
     * @return a meaningful String
     */
    public String toString() {
        return "Virtual Server " + vs.getId() + " adapter ";
    }
    
    /**
     * Call the service method, and notify all listeners
     *
     * @exception Exception if an error happens during handling of
     *   the request. Common errors are:
     *   <ul><li>IOException if an input/output error occurs and we are
     *   processing an included servlet (otherwise it is swallowed and
     *   handled by the top level error handler mechanism)
     *       <li>ServletException if a servlet throws an exception and
     *  we are processing an included servlet (otherwise it is swallowed
     *  and handled by the top level error handler mechanism)
     *  </ul>
     *  Tomcat should be able to handle and log any other exception ( including
     *  runtime exceptions )
     */
    public void service(Request req, Response res)
        throws Exception {

        if (Utils.getDefaultLogger().isLoggable(Level.FINER)) {
            Utils.getDefaultLogger().finer("Received something on " + req.requestURI());
        }


        String requestURI = req.requestURI().toString();
        try{
            // TODO: This for loop is extremely dangerous. We need to re-use
            // Grizzly HTTP Mapper here to avoid scripting attach, dos, etc.
            for(;;) {
                com.sun.grizzly.tcp.Adapter dispatchTo = endpoints.get(requestURI);
                ApplicationContainer container = apps.get(dispatchTo);
                if (dispatchTo!=null) {
                    ClassLoader cl = null;
                    if (container!=null) {
                        cl = container.getClassLoader();
                    }
                    dispatcher.dispath(dispatchTo, cl, req, res);

                    if (res.getStatus()!=200) {
                        sendError(res, "GlassFish v3 Error " + res.getStatus());
                    }
                    dispatchTo.afterService(req, res);
                    return;
                }

                if (requestURI.lastIndexOf("/")!=-1) {
                    requestURI = requestURI.substring(0, requestURI.lastIndexOf("/"));
                } else {
                    // last chance, look in our docroot...
                    File file = new File(docRoot, req.requestURI().getString());
                    if (file.exists()) {
                        serviceFile(req, res, file);
                    } else if (req.requestURI().toString().equals("/favicon.ico")) {
                        serviceFile(req, res, new File(docRoot, "/favicon.gif"));
                    } else {
                        // TODO : a better job at error reporting
                        Utils.getDefaultLogger().info("No adapter registered for : "
                                + req.requestURI().toString());
                        sendError(res, "Glassfish v3 Error : NotFound : "
                                + req.requestURI().getString());
                    }
                    return;
                }
            }
        } finally {
            try{
                req.action( ActionCode.ACTION_POST_REQUEST , null);
            }catch (Throwable t) {
                t.printStackTrace();
            }

            res.finish();
        }
    }

    /*
     * Registers a new endpoint (adapter implementation) for a particular
     * context-root. All request coming with the context root will be dispatched
     * to the adapter instance passed in.
     * @param contextRoot for the adapter
     * @param endpointAdapter servicing requests.
     */
    public void registerEndpoint(String contextRoot, com.sun.grizzly.tcp.Adapter endpointAdapter,
                                 ApplicationContainer container) {
        if (!contextRoot.startsWith("/")) {
            contextRoot = "/" + contextRoot;
        }
        endpoints.put(contextRoot, endpointAdapter);
        if (container!=null) {
            apps.put(endpointAdapter, container);
        }
    }

    /**
     * Removes the contex-root from our list of endpoints.
     */
    public void unregisterEndpoint(String contextRoot, ApplicationContainer app) {
        if (apps.containsValue(app)) {
            endpoints.remove(contextRoot);
            apps.remove(app);
        }
    }    

    /**
     * Crude implementation of the extraction of the docroot from
     * what I think is the right virtual server config
     */
    private String getDocRoot(VirtualServer virtualServer) {

        String docRoot = virtualServer.getDocroot();
        if (docRoot==null) {
            // look for the properties...
            return ConfigBeansUtilities.getPropertyValueByName(virtualServer, "docroot");
        }
        return "/";
    }

    /**
     * Utility method to service a local file
     */
    public void serviceFile(Request req, Response res, File file) throws IOException, FileNotFoundException {

        if (file.isDirectory()) {
            file = new File(file, "index.html");
        }
        if (modifiedSince(req, file)) {
            // Not Modified
            res.setStatus(304);

            return;
        }
    //    FileServer handler = getHandlerFor(req);
        FileServer handler = null;
        if (handler!=null) {
            ClassLoader currentCl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(handler.getClass().getClassLoader());
                DynamicContentAdapter adapter = DynamicContentAdapter.class.cast(handler);
                adapter.setRootFolder(docRoot);
                adapter.service(req, res);
                return;
            } catch(Exception e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while servicing " + file, e);
            } finally {
                Thread.currentThread().setContextClassLoader(currentCl);
            }
        }

        // set headers
        String type = URLConnection.guessContentTypeFromName(file.getName());
        res.setStatus(200);
        res.setContentType(type);
        res.setContentLength((int) file.length());
        res.addHeader("Server", "GlassFish/v3");
        res.sendHeaders();

        FileInputStream fis = new FileInputStream(file);
        byte b[] = new byte[8192];
        ByteChunk chunk = new ByteChunk();
        int rd = 0;
        while ((rd = fis.read(b)) > 0) {
            chunk.setBytes(b, 0, rd);
            res.doWrite(chunk);
        }

        try{
            req.action( ActionCode.ACTION_POST_REQUEST , null);
        }catch (Throwable t) {
            t.printStackTrace();
        }

        res.finish();
    }

    private static boolean modifiedSince(Request req, File file) {
        try {
            String since = req.getMimeHeaders().getHeader("If-Modified-Since");
            if (since == null) {
                return false;
            }

            Date date = new SimpleDateFormat(RFC_2616_FORMAT, Locale.US).parse(since);
            if (date.getTime() > file.lastModified()) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Returns a handler for a static file service.
     * @return the request handler
     */

    // TODO : dochez : revive this code.
    /*
    public FileServer getHandlerFor(Request req) {
        if (handlers==null) {
            return null;
        }

        String uri = req.requestURI().getString();
        for (FileServer handler : habitat.getAllByContract(FileServer.class)) {
            WebRequestHandler annotation = handler.getClass().getAnnotation(WebRequestHandler.class);
            if (annotation!=null) {
                if (annotation.urlPattern().length()>0) {
                    Pattern pattern = Pattern.compile(annotation.urlPattern());
                    if (pattern.matcher(uri).matches()) {
                        return handler;
                    }
                }
                return handler;
            }
        }
        Logger logger = LogDomains.getLogger(LogDomains.WEB_LOGGER);
        Collection<? extends Sniffer> sniffers = habitat.getAllByContract(Sniffer.class);
        for (Sniffer sniffer : sniffers) {
            if (sniffer.getURLPattern()==null)
                continue;
            Matcher m = sniffer.getURLPattern().matcher(uri);
            if (m.matches()) {
                ContainerRegistry containerRegistry;
                try {
                    containerRegistry = habitat.getComponent(ContainerRegistry.class);
                } catch(ComponentException e) {
                    logger.log(Level.SEVERE, "Cannot get container registry", e);
                    return null;
                }
                ContainerInfo containerInfo = containerRegistry.getContainer(sniffer.getModuleType());
                if (containerInfo==null) {
                    ContainerStarter starter = new ContainerStarter(modulesRegistry, habitat, logger);
                    Collection<ContainerInfo> containersInfo = starter.startContainer(sniffer);
                    containerInfo = containersInfo.iterator().next();
                }
                Iterable<Class<? extends FileServer>> services;

                if (containerInfo==null) {
                    // last ditch, maybe an implementation of the FileServer interface
                    services = modulesRegistry.getProvidersClass(FileServer.class);
                } else {
                    containerRegistry.addContainer(sniffer.getContainersNames()[0], containerInfo);
                    com.sun.enterprise.module.Module connectorModule = com.sun.enterprise.module.Module.find(containerInfo.getContainer().getClass());

                    services = connectorModule.getProvidersClass(FileServer.class);
                }
                for (Class<? extends FileServer> service :services) {
                    WebRequestHandler annotation = service.getAnnotation(WebRequestHandler.class);
                    if (annotation!=null) {
                        if (annotation.urlPattern().length()>0) {
                            Pattern pattern = Pattern.compile(annotation.urlPattern());
                            if (!pattern.matcher(uri).matches()) {
                                continue;
                            }
                        }
                        // if we are here, we have a matching WebRequestHandler.
                        FileServer server = null;
                        try {
                            server = habitat.getComponent(service);
                        } catch (ComponentException e) {
                            logger.log(Level.SEVERE, "Cannot create FileServer implementation " + service, e);
                        }
                        return server;


                    }
                }
            }

        }

        return null;
    }
       */
    
}
