package org.glassfish.jdbc.devtests.v3;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.util.Map;
import javax.annotation.Resource;
import org.glassfish.jdbc.devtests.v3.test.SimpleTest;
import org.glassfish.jdbc.devtests.v3.util.HtmlUtil;

/**
 *
 * @author jagadish
 */
public class TestResultServlet extends HttpServlet {

    @Resource(name = "jdbc/jdbc-common-resource", mappedName = "jdbc/jdbc-common-resource")
    DataSource dsCommon;

    @Resource(name = "jdbc/jdbc-multiple-user-test-resource", mappedName = "jdbc/jdbc-multiple-user-test-resource")
    DataSource dsMultipleUserCred;
    
    @Resource(name="jdbc/jdbc-app-auth-test-resource", mappedName = "jdbc/jdbc-app-auth-test-resource")
    DataSource dsAppAuth;
    
    @Resource(name="jdbc/jdbc-stmt-timeout-test-resource", mappedName = "jdbc/jdbc-stmt-timeout-test-resource")
    DataSource dsStmtTimeout;
    
    @Resource(name="jdbc/jdbc-max-conn-usage-test-resource", mappedName = "jdbc/jdbc-max-conn-usage-test-resource")
    DataSource dsMaxConnUsage;
    
    @Resource(name="jdbc/jdbc-conn-leak-tracing-test-resource", mappedName="jdbc/jdbc-conn-leak-tracing-test-resource")
    DataSource dsConnLeakTracing;
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        SimpleTest testMultipleUserCred = null;
        SimpleTest testAppAuth = null;
        SimpleTest testStmtTimeout = null;
        SimpleTest testMaxConnUsage = null;
        SimpleTest testConnLeakTracing = null;
        SimpleTest[] testsOther = null;
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet TestResultServlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Servlet TestResultServlet at " + request.getContextPath() + "</h1>");
        StringBuffer buf = new StringBuffer();

        try {
            testMultipleUserCred = loadMultipleUserCredTest();
            testAppAuth = loadAppAuthTest();
            testStmtTimeout = loadStmtTimeoutTest();
            testMaxConnUsage = loadMaxConnUsageTest();
            testConnLeakTracing = loadConnLeakTracingTest();
            testsOther = initializeTests();
        } catch (Exception e) {
            HtmlUtil.printException(e, out);
        }

        try {
            buf.append("<table border=1><tr><th>Test Name</th><th> Pass </th></tr>");

            //Run other tests
            for (SimpleTest test : testsOther) {
                Map<String, Boolean> map = test.runTest(dsCommon, out);
                for (Map.Entry entry : map.entrySet()) {
                    buf.append("<tr> <td>");
                    buf.append(entry.getKey());
                    buf.append("</td>");
                    buf.append("<td>");
                    buf.append(entry.getValue());
                    buf.append("</td></tr>");
                }
            }

            //Run Multiple User Credentials Test 
            Map<String, Boolean> mapMultipleUserCred = 
                    testMultipleUserCred.runTest(dsMultipleUserCred, out);
            for (Map.Entry entry : mapMultipleUserCred.entrySet()) {
                    buf.append("<tr> <td>");
                    buf.append(entry.getKey());
                    buf.append("</td>");
                    buf.append("<td>");
                    buf.append(entry.getValue());
                    buf.append("</td></tr>");                
            }
            
            //Run Application Authentication Test
            Map<String, Boolean> mapAppAuth = 
                    testAppAuth.runTest(dsAppAuth, out);
            for (Map.Entry entry : mapAppAuth.entrySet()) {
                    buf.append("<tr> <td>");
                    buf.append(entry.getKey());
                    buf.append("</td>");
                    buf.append("<td>");
                    buf.append(entry.getValue());
                    buf.append("</td></tr>");                
            }

            //Run Statement Timeout Test
            Map<String, Boolean> mapStmtTimeout = 
                    testStmtTimeout.runTest(dsStmtTimeout, out);
            for (Map.Entry entry : mapStmtTimeout.entrySet()) {
                    buf.append("<tr> <td>");
                    buf.append(entry.getKey());
                    buf.append("</td>");
                    buf.append("<td>");
                    buf.append(entry.getValue());
                    buf.append("</td></tr>");                
            }

            //Run Max Connection Usage Test
            Map<String, Boolean> mapMaxConnUsage = 
                    testMaxConnUsage.runTest(dsMaxConnUsage, out);
            for (Map.Entry entry : mapMaxConnUsage.entrySet()) {
                    buf.append("<tr> <td>");
                    buf.append(entry.getKey());
                    buf.append("</td>");
                    buf.append("<td>");
                    buf.append(entry.getValue());
                    buf.append("</td></tr>");                
            }

            //Run Connection Leak Tracing Test
            Map<String, Boolean> mapConnLeakTracing = 
                    testConnLeakTracing.runTest(dsConnLeakTracing, out);
            for (Map.Entry entry : mapConnLeakTracing.entrySet()) {
                    buf.append("<tr> <td>");
                    buf.append(entry.getKey());
                    buf.append("</td>");
                    buf.append("<td>");
                    buf.append(entry.getValue());
                    buf.append("</td></tr>");                
            }
            
            buf.append("</table>");
            out.println(buf.toString());
        } catch (Throwable e) {
            out.println("got outer excpetion");
            out.println(e);
            e.printStackTrace();
        } finally {
            out.println("</body>");
            out.println("</html>");

            out.close();
            out.flush();
        }
    }

    private SimpleTest loadAppAuthTest() throws Exception {
        String test = "org.glassfish.jdbc.devtests.v3.test.ApplicationAuthTest";
        Class testClass = Class.forName(test);
        Constructor c = testClass.getConstructor();
        SimpleTest testInstance = (SimpleTest) c.newInstance();
        return testInstance;
    }

    private SimpleTest loadConnLeakTracingTest() throws Exception {
        String test = "org.glassfish.jdbc.devtests.v3.test.ConnectionLeakTracingTest";
        Class testClass = Class.forName(test);
        Constructor c = testClass.getConstructor();
        SimpleTest testInstance = (SimpleTest) c.newInstance();
        return testInstance;
    }

    private SimpleTest loadMaxConnUsageTest() throws Exception {
        String test = "org.glassfish.jdbc.devtests.v3.test.MaxConnectionUsageTest";
        Class testClass = Class.forName(test);
        Constructor c = testClass.getConstructor();
        SimpleTest testInstance = (SimpleTest) c.newInstance();
        return testInstance;
    }

    private SimpleTest loadMultipleUserCredTest() throws Exception {
        String test = "org.glassfish.jdbc.devtests.v3.test.MultipleUserCredentialsTest";
        Class testClass = Class.forName(test);
        Constructor c = testClass.getConstructor();
        SimpleTest testInstance = (SimpleTest) c.newInstance();
        return testInstance;
    }
    
    private SimpleTest[] initializeTests() throws Exception {
        String[] tests = {"org.glassfish.jdbc.devtests.v3.test.ConnectionSharingTest",
            "org.glassfish.jdbc.devtests.v3.test.LeakTest",
            "org.glassfish.jdbc.devtests.v3.test.UserTxTest", 
            "org.glassfish.jdbc.devtests.v3.test.NoTxConnTest",
            "org.glassfish.jdbc.devtests.v3.test.MultipleConnectionCloseTest",
            "org.glassfish.jdbc.devtests.v3.test.MarkConnectionAsBadTest",
            "org.glassfish.jdbc.devtests.v3.test.ContainerAuthTest"
        };

        SimpleTest[] testInstances = new SimpleTest[tests.length];
        for (int i = 0; i < tests.length; i++) {
            Class testClass = Class.forName(tests[i]);
            //Constructor c = testClass.getConstructor(javax.sql.DataSource.class, java.io.PipedInputStream.class);
            Constructor c = testClass.getConstructor();
            testInstances[i] = (SimpleTest) c.newInstance();
        }
        return testInstances;
    }    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }

    private SimpleTest loadStmtTimeoutTest() throws Exception {
        String test = "org.glassfish.jdbc.devtests.v3.test.StatementTimeoutTest";
        Class testClass = Class.forName(test);
        Constructor c = testClass.getConstructor();
        SimpleTest testInstance = (SimpleTest) c.newInstance();
        return testInstance;
    }
    // </editor-fold>
}
