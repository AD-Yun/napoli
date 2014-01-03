package search.crawl.napoli.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SMTPMailSend {
	static final Logger LOGGER = LoggerFactory.getLogger(SMTPMailSend.class);
	
	private SMTPMailSend() {
	}
	
	public static MailSend build(String fromName, String fromAddress, Map<String, String>  toAddresses, String title) {
		return new MailSend(fromName, fromAddress, toAddresses, title);
	}
	public static MailSend build(String fromName, String fromAddress, String toName, String toAddress, String title) {
		return new MailSend(fromName, fromAddress, toName, toAddress, title);
	}
	public static MailSend build(Map<String, String> fromAddress, Map<String, String>  toAddresses, String title) {
		return new MailSend(fromAddress, toAddresses, title);
	}
	public static class MailSend {
		
		/* TODO :  configuration 읽어와야 하는 부분. IP,PORT */
		private String protocol = "stmp";
		private String smtpHost = "10.234.214.25";
		private int port = 25;
		
		/* content 내용 setting...*/
		private String fromName = "";
		private String fromAddress = "";
		private Map<String, String> toAddresses = null;
		private String title = "";
		private String contents = "";
		private Map<String, String> ccAddresses = null;
		private List<String> attatchFiles = null;
		private String contentType = "text/html; charset=ISO-8859-1";
			
		public MailSend() { 
			super();
		}
		public MailSend(Map<String, String> fromAddr, Map<String, String> toAddr, String title) { 
			Object[] fromNames = fromAddr.keySet().toArray();
			String name = (String) fromNames[0];
			this.fromName = name;
			this.fromAddress = fromAddr.get(name);
			this.toAddresses = toAddr;
			this.title = title;
		}		
		public MailSend(String fromName, String fromAddress, String toName, String toAddress, String title) {
			Map<String, String> toAddr = new HashMap<String, String>();
			toAddr.put(toName, toAddress);
			this.fromName = fromName;
			this.fromAddress = fromAddress;
			this.toAddresses = toAddr;
			this.title = title;
		}
		public MailSend(String fromName, String fromAddress, Map<String, String> toAddresses, String title) {
			this.fromName = fromName;
			this.fromAddress = fromAddress;
			this.toAddresses = toAddresses;
			this.title = title;
		}
		public MailSend content(String content) { 
			this.contents = content; 
			return this;
		}
		public MailSend ccAddresses(Map<String, String> ccAddr) { 
			this.ccAddresses = ccAddr; 
			return this;
		}
		public MailSend attatchFiles(List<String> attatchFiles) { 
			this.attatchFiles = attatchFiles; 
			return this;
		}
		public MailSend contentType(String contentType) { 
			this.contentType = contentType; 
			return this;
		}
		
		private InternetAddress[] getAddresssArray(Map<String, String> addr) { 
			Set<String> addrKey = addr.keySet();
			Iterator<String> it = addrKey.iterator();
			String name = "";
			int i = 0;
			
			InternetAddress[] retAddressArray = new InternetAddress[addr.size()];
			try { 
				while(it.hasNext()) { 
					name = it.next();
					retAddressArray[i] = new InternetAddress(addr.get(name), name);
					i++;
				}
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage());
			}
			return retAddressArray;
		}
		
		public void send() { 
			/* mail send..... */
			Properties props = new Properties();
			props.put("mail.transport.protocol", this.protocol);
			props.put("mail.smtp.host", this.smtpHost);
			props.put("mail.smtp.port", this.port);
			
			Session session = Session.getInstance(props);
			MimeMessage message = new MimeMessage(session);
			try { 
				// mail from address setting...
				message.setFrom(new InternetAddress(this.fromAddress, this.fromName));
				
				LOGGER.debug("FROM : " + this.fromName + ", "  + this.fromAddress);
				
				//multi Recipients...
				InternetAddress[] toAddr = getAddresssArray(this.toAddresses);
				message.addRecipients(Message.RecipientType.TO, toAddr);
				
				// if cc is not null, cc address setting.
				if (this.ccAddresses != null) { 
					InternetAddress[] ccAddr = getAddresssArray(this.ccAddresses);
					message.addRecipients(Message.RecipientType.CC, ccAddr);
				}
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage());
			} catch (MessagingException e) {
				LOGGER.error(e.getMessage());
			}
			
			try {
				// mail title setting.
				message.setSubject(this.title); 
				LOGGER.debug("TITLE " + this.title);
				
				// Create the message part 
				BodyPart messageBodyPart = new MimeBodyPart();
				// Fill the message 
				messageBodyPart.setContent(this.contents, this.contentType);
				messageBodyPart.setHeader("MIME-Version",  "1.0");
				messageBodyPart.setHeader("Content-Type", messageBodyPart.getContentType());
		
				// Create a multipar message 
				Multipart multipart = new MimeMultipart(); 
		
				// Set text message part 
				multipart.addBodyPart(messageBodyPart); 
		
				// if attatch files is not null ... attatch file append...
				if (attatchFiles != null) { 
					messageBodyPart = new MimeBodyPart(); 
					DataSource source = null;
					for(String filename : attatchFiles) {
						source  = new FileDataSource(filename); 
						messageBodyPart.setDataHandler(new DataHandler(source)); 
						messageBodyPart.setFileName(filename); 
						multipart.addBodyPart(messageBodyPart); 
					}
				}
				// Send the complete message parts 
				message.setContent(multipart); 
			} catch (MessagingException e) {
				LOGGER.error(e.getMessage());
			}
			try {
				//mail send.
				Transport transport = session.getTransport("smtp"); 
				transport.send(message);
			} catch (MessagingException e) {
				LOGGER.error(e.getMessage());
			}

		}
	}
}











