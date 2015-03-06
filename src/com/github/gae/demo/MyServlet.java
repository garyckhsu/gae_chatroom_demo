
package com.github.gae.demo;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class MyServlet extends HttpServlet {

    private static final Logger sLogger = Logger.getLogger(MyServlet.class
            .getName());

    static HashMap<String, Set<String>> sChatRommMap = new HashMap<String, Set<String>>();

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        sLogger.info("doGet");
        final String roomNum = req.getParameter("room");
        sLogger.info("room num =" + roomNum);

        if (roomNum != null) {
            final ChannelService channelService = ChannelServiceFactory
                    .getChannelService();
            final String token = channelService.createChannel(roomNum);
            sLogger.info("token= " + token);
            Set<String> room = sChatRommMap.get(roomNum);
            if (room != null) {
                room.add(token);
                sChatRommMap.put(roomNum, room);
            } else {
                room = new HashSet<String>();
                room.add(token);
                sChatRommMap.put(roomNum, room);
            }
            resp.setContentType("text/javascript");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            final PrintWriter out = resp.getWriter();
            out.println(token);
            out.close();

        }
    }

    @Override
    protected void doPost(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        sLogger.info("doPost");
        final String roomNum = req.getParameter("room");
        sLogger.info("roomNum =" + roomNum);
        final String message = req.getReader().readLine();
        sLogger.info("send message = " + message);
        final Set<String> room = sChatRommMap.get(roomNum);
        resp.setContentType("text/javascript");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        if (room != null) {
            // Send a message based on the 'channelKey' any channel with this
            // key
            // will receive the message
            final ChannelService channelService = ChannelServiceFactory
                    .getChannelService();
            final String userAgent = req.getHeader("User-Agent");
            channelService.sendMessage(new ChannelMessage(roomNum, userAgent
                    + " : " + message));
        } else {
            return;
        }
    }
}
