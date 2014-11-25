/*
 * @Title:  ModelParser.java
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * @Description:  TODO<请描述此文件是做什么的>
 * @author:  MeiYi
 * @data:  2014年7月4日 下午5:19:58
 * @version:  V1.0
 */
package com.inter.trade.ui.func.parser;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.inter.protocol.ProtocolData;
import com.inter.protocol.body.NetParser;

/**
 * 统计模块使用次数
 * @author  ChenGuangChi
 * @data:  2014年7月4日 下午5:19:58
 * @version:  V1.0
 */
public class CountParser extends NetParser{

	@Override
	public void requestBodyToXml(ProtocolData body, XmlSerializer serializer)
			throws IllegalArgumentException, IllegalStateException, IOException {
		// TODO Auto-generated method stub
				if (body.mChildren != null && body.mChildren.size() > 0) {
					Set<String> keys = body.mChildren.keySet();
					for(String key:keys){
						List<ProtocolData> rs = body.mChildren.get(key);
						for(ProtocolData data: rs){
							if(data.mKey.equals("appmnuid")){
								serializer.startTag("", "appmnuid");
								serializer.text(data.mValue.trim());
								serializer.endTag("", "appmnuid");
							}else if(data.mKey.equals("agentno")){
								serializer.startTag("", "agentno");
								serializer.text(data.mValue.trim());
								serializer.endTag("", "agentno");
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
				if (parser.getName().compareTo("mnuname") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData data = new ProtocolData("mnuname", temp);
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
