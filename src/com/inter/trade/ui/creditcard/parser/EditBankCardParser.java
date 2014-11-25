package com.inter.trade.ui.creditcard.parser;

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
public class EditBankCardParser extends NetParser {

	@Override
	public void requestBodyToXml(ProtocolData body, XmlSerializer serializer)
			throws IllegalArgumentException, IllegalStateException, IOException {
		if (body.mChildren != null && body.mChildren.size() > 0) {
			Set<String> keys = body.mChildren.keySet();
			for(String key:keys){
				List<ProtocolData> rs = body.mChildren.get(key);
				for(ProtocolData data: rs){
					if(data.mKey.equals("bkcardbankid")){
						serializer.startTag("", "bkcardbankid");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardbankid");
						
					}else if(data.mKey.equals("bkcardbank")){
						serializer.startTag("", "bkcardbank");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardbank");
						
					}else if(data.mKey.equals("bkcardno")){
						serializer.startTag("", "bkcardno");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardno");
						
					}else if(data.mKey.equals("bkcardbankman")){
						serializer.startTag("", "bkcardbankman");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardbankman");
						
					}else if(data.mKey.equals("bkcardbankphone")){
						serializer.startTag("", "bkcardbankphone");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardbankphone");
						
					}else if(data.mKey.equals("bkcardyxmonth")){
						serializer.startTag("", "bkcardyxmonth");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardyxmonth");
						
					}else if(data.mKey.equals("bkcardyxyear")){
						serializer.startTag("", "bkcardyxyear");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardyxyear");
						
					}else if(data.mKey.equals("bkcardcvv")){
						serializer.startTag("", "bkcardcvv");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardcvv");
						
					}else if(data.mKey.equals("bkcardidcard")){
						serializer.startTag("", "bkcardidcard");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardidcard");
						
					}else if(data.mKey.equals("bkcardcardtype")){
						serializer.startTag("", "bkcardcardtype");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardcardtype");
						
					}else if(data.mKey.equals("bkcardisdefault")){
						serializer.startTag("", "bkcardisdefault");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardisdefault");
						
					}else if(data.mKey.equals("bkcardfudefault")){
						serializer.startTag("", "bkcardfudefault");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardfudefault");
						
					}else if(data.mKey.equals("bkcardshoudefault")){
						serializer.startTag("", "bkcardshoudefault");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardshoudefault");
						
					}else if(data.mKey.equals("bkcardid")){
						serializer.startTag("", "bkcardid");
						serializer.text(data.mValue.trim());
						serializer.endTag("", "bkcardid");
						
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
}
