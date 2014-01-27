package com.chanapps.glass.chan.model;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/21/14
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Text {
    public static String filter(String text) {
        return text
                .replaceAll("<br/?>", "\n")
                .replaceAll("<[^>]*>", "")
                .replaceAll("&#039;|&quot;", "'")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">");
    }
}
