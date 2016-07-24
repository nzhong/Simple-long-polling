package com.learn.jetty;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class AsyncServer
{
    private static SecureRandom secureRandom = new SecureRandom();
    static {
        secureRandom.setSeed( System.currentTimeMillis() );
    }

    private static String waitFor5To15Seconds() {
        long start = System.currentTimeMillis();
        try {
            long sleepTimeInMilli = 5000L + 1000L*secureRandom.nextInt(10);  // 5 - 15 seconds
            Thread.sleep(sleepTimeInMilli);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        long end = System.currentTimeMillis();
        return "After "+(end-start)+"ms the server responds with ECHO.";
    }

    public static class EmbeddedAsyncServlet extends HttpServlet
    {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
        {
            final AsyncContext asyncCtx = req.startAsync();
            asyncCtx.start( new Runnable(){
                public void run()
                {
                    ServletResponse response = asyncCtx.getResponse();
                    try {
                        response.getWriter().write( waitFor5To15Seconds() );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    asyncCtx.complete();
                }
            });
        }
    }

    public static class BlockingServlet extends HttpServlet
    {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
        {
            try {
                resp.getWriter().write( waitFor5To15Seconds() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(9000);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");

        ServletHolder asyncHolder = context.addServlet(EmbeddedAsyncServlet.class,"/async");
        asyncHolder.setAsyncSupported(true);

        ServletHolder blockingHolder = context.addServlet(BlockingServlet.class,"/block");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(false);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase("webapps/HTML");
        ContextHandler staticCtx = new ContextHandler("/static");
        staticCtx.setHandler(resource_handler);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { staticCtx, context, new DefaultHandler() });
        server.setHandler(handlers);
        server.start();
        server.join();
    }
}
