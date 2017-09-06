package com.wizecore.graylog2.plugin;

import java.util.Map;

import org.graylog2.plugin.Message;
import org.graylog2.syslog4j.SyslogIF;

/**
 * Using CEF format
 */

/*
 * http://blog.rootshell.be/2011/05/11/ossec-speaks-arcsight/
 * 
 * 
 * CEF:Version|Device Vendor|Device Product|Device Version|Signature ID|Name|Severity|Extension

CEF:0|ArcSight|Logger|5.0.0.5355.2|sensor:115|Logger Internal Event|1|\
cat=/Monitor/Sensor/Fan5 cs2=Current Value cnt=1 dvc=10.0.0.1 cs3=Ok \
cs1=null type=0 cs1Label=unit rt=1305034099211 cs3Label=Status cn1Label=value \
cs2Label=timeframe
 */
public class CEFSender implements MessageSender {

	@Override
	public void send(SyslogIF syslog, int level, Message msg) {
		StringBuilder out = new StringBuilder();
		// Header:
		// CEF:Version|Device Vendor|Device Product|Device Version|
		out.append("CEF:0|Graylog|graylog-output-syslog:cefsender|2.1.1|");
		// Device Event Class ID
		out.append("log:1");
		out.append("|");
		// Name
		String str = msg.getMessage();
		if (str.contains("|")) {
			str = str.replace("|", "");
		}
		out.append(str);
		// Severity
		out.append("|").append(level) .append("|"); 
		// Extension
		Map<String, Object> fields = msg.getFields();
		boolean have = false;
		for (String k: fields.keySet()) {
			Object v = fields.get(k);
			if (!k.equals("message") && !k.equals("full_message")) {
    			String s = v != null ? v.toString() : "null";
				s = s.replace("\\", "\\\\");
				s = s.replace("=", "\\=");
				s = s.replace("\r", "");
				s = s.replace("\n", "\\n");
				if (have) {
					out.append(" ");
				}
				out.append(k).append('=').append(s);
				have = true;
			}
		}

		syslog.log(level, out.toString());
	}
}
