package com.inter.trade.ui.fragment.salarypay.parser;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.inter.protocol.ProtocolData;
import com.inter.protocol.body.NetParser;

/**
 * 
 * @author chenguangchi
 *
 */
public class GetBillParser extends NetParser {

	@Override
	public void requestBodyToXml(ProtocolData body, XmlSerializer serializer)
			throws IllegalArgumentException, IllegalStateException, IOException {


		// TODO Auto-generated method stub
		if (body.mChildren != null && body.mChildren.size() > 0) {
			Set<String> keys = body.mChildren.keySet();
			for(String key:keys){
				List<ProtocolData> rs = body.mChildren.get(key);
				for(ProtocolData data: rs){
					if(data.mKey.equals("wagepaymoney")){
						serializer.startTag("", "wagepaymoney");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "wagepaymoney");
						
					}else if(data.mKey.equals("wagemonth")){
						serializer.startTag("", "wagemonth");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "wagemonth");
						
					}else if(data.mKey.equals("wagemoney")){
						serializer.startTag("", "wagemoney");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "wagemoney");
						
					}else if(data.mKey.equals("fucardno")){
						serializer.startTag("", "fucardno");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "fucardno");
						
					}else if(data.mKey.equals("fucardbank")){
						serializer.startTag("", "fucardbank");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "fucardbank");
						
					}else if(data.mKey.equals("wagelistid")){
						serializer.startTag("", "wagelistid");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "wagelistid");
					}else if(data.mKey.equals("paycardid")){
						serializer.startTag("", "paycardid");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "paycardid");
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
				}else if (parser.getName().compareTo("bkntno") == 0) {
					String temp = parser.nextText();
					if (temp != null && temp.length() > 0) {
						ProtocolData r = new ProtocolData("bkntno", temp);
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
}