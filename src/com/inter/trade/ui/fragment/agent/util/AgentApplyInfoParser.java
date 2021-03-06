package com.inter.trade.ui.fragment.agent.util;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.inter.protocol.ProtocolData;
import com.inter.protocol.body.NetParser;

/**
 * 代理商在线申请，获取申请基本信息,数据解析
 * @author Lihaifeng
 *
 */
public class AgentApplyInfoParser extends NetParser{

	@Override
	public void requestBodyToXml(ProtocolData body, XmlSerializer serializer)
			throws IllegalArgumentException, IllegalStateException, IOException {
		// TODO Auto-generated method stub
		if (body.mChildren != null && body.mChildren.size() > 0) {
			Set<String> keys = body.mChildren.keySet();
			for(String key:keys){
				List<ProtocolData> rs = body.mChildren.get(key);
				for(ProtocolData data: rs){
					//密钥
					if(data.mKey.equals("authorid")){
						serializer.startTag("", "authorid");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "authorid");
						
					}else 
					if(data.mKey.equals("prov")){
							serializer.startTag("", "prov");
							serializer.text(data.mValue.trim());
							serializer.endTag("", "prov");
							
					}else if(data.mKey.equals("city")){
							serializer.startTag("", "city");
							serializer.text(data.mValue.trim());
							serializer.endTag("", "city");
							
					}else if(data.mKey.equals("town")){
						serializer.startTag("", "town");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "town");
					}
				}
			}
		}
	}

	@Override
	public ProtocolData parserBody(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		// TODO Auto-generated method stub
		ProtocolData res = new ProtocolData("msgbody", null);
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG: {
				if (parser.getName().compareTo("result") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData r = new ProtocolData("result", temp);
						res.addChild(r);
					}
				}else if (parser.getName().compareTo("message") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData r = new ProtocolData("message", temp);
						res.addChild(r);
					}
				}else if (parser.getName().compareTo("agentprotocol") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData r = new ProtocolData("agentprotocol", temp);
						res.addChild(r);
					}
				}else if (parser.getName().compareTo("msgchild") == 0) {
					ProtocolData r = new ProtocolData("msgchild", null);
					parserHelpItem(parser, r);
					res.addChild(r);
				}
			}
				break;
			case XmlPullParser.END_TAG: {
				if (parser.getName().compareTo("msgbody") == 0) {
					return res;
				}
			}
				break;
			case XmlPullParser.TEXT: {

			}
				break;
			}
			eventType = parser.next();
		}
		return res;
	}

	private  void parserHelpItem(XmlPullParser parser, ProtocolData parent)
			throws XmlPullParserException, IOException {
		// TODO Auto-generated method stub
		// HashMap<String, List<ProtocolData>> res = new HashMap<String,
		// List<ProtocolData>>();
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG: {
				if (parser.getName().compareTo("custypeid") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("custypeid", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("custypename") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("custypename", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("msgfile") == 0) {
					ProtocolData r = new ProtocolData("msgfile", null);
					parserHelpItem2(parser, r);
					parent.addChild(r);
//				}else if (parser.getName().compareTo("msgchild") == 0) {
//					ProtocolData r = new ProtocolData("msgchild", null);
//					parserHelpItem2(parser, r);
//					parent.addChild(r);
				}
			}
				break;
			case XmlPullParser.END_TAG: {
				if (parser.getName().compareTo("msgchild") == 0) {
					return;
				}
				
			}
				break;
			case XmlPullParser.TEXT: {

			}
				break;
			}
			eventType = parser.next();
		}
		return;
	}
	
	private  void parserHelpItem2(XmlPullParser parser, ProtocolData parent)
			throws XmlPullParserException, IOException {
		// TODO Auto-generated method stub
		// HashMap<String, List<ProtocolData>> res = new HashMap<String,
		// List<ProtocolData>>();
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG: {
			if (parser.getName().compareTo("msgchild") == 0) {
				ProtocolData r = new ProtocolData("msgchild", null);
				parserHelpItem3(parser, r);
				parent.addChild(r);
			}
			}
			break;
			case XmlPullParser.END_TAG: {
				if (parser.getName().compareTo("msgfile") == 0) {
					return;
				}
			}
			break;
			case XmlPullParser.TEXT: {
				
			}
			break;
			}
			eventType = parser.next();
		}
		return;
	}
	
	private  void parserHelpItem3(XmlPullParser parser, ProtocolData parent)
			throws XmlPullParserException, IOException {
		// TODO Auto-generated method stub
		// HashMap<String, List<ProtocolData>> res = new HashMap<String,
		// List<ProtocolData>>();
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG: {
				if (parser.getName().compareTo("pictypeid") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("pictypeid", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("pictypename") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("pictypename", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("pictypeno") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("pictypeno", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("picuploadurl") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("picuploadurl", temp);
						parent.addChild(data);
					}
				}
			}
			break;
			case XmlPullParser.END_TAG: {
				if (parser.getName().compareTo("msgchild") == 0) {
					return;
				}
			}
			break;
			case XmlPullParser.TEXT: {
				
			}
			break;
			}
			eventType = parser.next();
		}
		return;
	}
	
}
