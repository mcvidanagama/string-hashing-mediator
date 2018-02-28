package com.medium.mcvidanagama;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;

public class StringHasher extends AbstractMediator {

	private static final Log log = LogFactory.getLog(StringHasher.class);

	private String HASH_SALT = "HASH_SALT";
	private String HASHING_ALGORITHM = "SHA-512";
	private String STRING_TO_BE_HASHED = "STRING_TO_BE_HASHED";
	private String HASHED_VALUE = "HASHED_VALUE";

	public boolean mediate(MessageContext context) {

		// Reading hashing candidate and hash salt from message context and
		// concatenating them to make the string to be hashed
		String stringBeforeHash = (String) context.getProperty(STRING_TO_BE_HASHED)
				+ (String) context.getProperty(HASH_SALT);
		// By enabling debug logs for this class mediator we can check whether
		// the values have been passed properly
		if (log.isDebugEnabled()) {
			log.debug("Value need to be hashed : " + (String) context.getProperty(STRING_TO_BE_HASHED));
			log.debug("Hash Salt  : " + (String) context.getProperty(HASH_SALT));
			log.debug("String Before Hash : " + stringBeforeHash);
		}
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(HASHING_ALGORITHM);
			byte[] bytes = md.digest(stringBeforeHash.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			if (log.isDebugEnabled()) {
				log.debug("Hashed Value : " + sb.toString());
			}
			context.setProperty(HASHED_VALUE, sb.toString());
		} catch (NoSuchAlgorithmException e) {
			throw new SynapseException("Error occurred while getting algorithm.", e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new SynapseException("Error occurred while encoding.", e);
		}

		return true;
	}
}
