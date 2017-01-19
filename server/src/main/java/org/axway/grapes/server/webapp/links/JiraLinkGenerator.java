package org.axway.grapes.server.webapp.links;

import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class JiraLinkGenerator {

    private String rootLink;

    public JiraLinkGenerator(final String link) {
        this.rootLink = link;

        if(!link.endsWith("&")) {
            this.rootLink += "&";
        }

    }

    private String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public String generateLink(final String issueSummary, final String issueDescription, final String issueReporter) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("summary", issueSummary);
        map.put("description", issueDescription);
        map.put("reporter", issueReporter);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s", urlEncodeUTF8(entry.getKey()
                    .toString()), urlEncodeUTF8(entry.getValue().toString())));
        }
        return String.format("%s%s", rootLink,sb.toString());
    }
    
//    public Map<String, Object> getFixedFields(){
//        Map<String, Object> map = new LinkedHashMap<String, Object>();
//        map.put("pid", 13425);
//        map.put("issuetype", 3);
//        map.put("priority", 3);
//        return map;
//    }
}
