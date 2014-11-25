package com.inter.trade.ui.fragment.airticket.util;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.inter.protocol.ProtocolData;
import com.inter.protocol.body.NetParser;

/**
 * 乘机人解析
 * @author zhichao.huang
 *
 */
public class PassengerParser extends NetParser{

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
					if(data.mKey.equals("type")){
						serializer.startTag("", "type");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "type");
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
				if (parser.getName().compareTo("name") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("name", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("cardType") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("cardType", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("cardId") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("cardId", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("phoneNumber") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("phoneNumber", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("id") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("id", temp);
						parent.addChild(data);
					}
				}else if (parser.getName().compareTo("passengerType") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("passengerType", temp);
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
