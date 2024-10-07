//Created by Maurice H. at 08.10.2024
package eu.lotusgaming.mc.misc;

public class ReportData {
	
	long messageId;
	String messageJumpURL;
	
	public ReportData(long messageId, String jumpUrl) {
		this.messageId = messageId;
		this.messageJumpURL = jumpUrl;
	}

	public long getMessageId() {
		return messageId;
	}

	public String getMessageJumpURL() {
		return messageJumpURL;
	}
}