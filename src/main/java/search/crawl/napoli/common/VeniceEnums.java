package search.crawl.napoli.common;

import java.util.HashMap;
import java.util.Map;

public class VeniceEnums {
	
	public enum HTTP_METHOD_TYPE {
		GET, POST
	}
	
	private static Map<Integer, Errors> codeToErrorsMap;
	private static void initMapping() {
		codeToErrorsMap = new HashMap<Integer, Errors>();
		for(Errors e : Errors.values()) {
			codeToErrorsMap.put(e.error_code, e);
		}
	}
	
	public enum Errors {
		/* 
		 * error code 정의 
		 * 1001 ~ 1999 : urlfeeder 작업 진행시 발생한 error
		 * 2001 ~ 2999 : filter 작업 진행시 발생한 error
		 * 3001 ~ 3999 : grabber, htmlparser, thumnail 생성등 작업 진행시 발생한 error
		 * 4000 ~ 4999 : DB 작업 관련된 에러
		 * 8000 ~ 	  : 기타
		 * 
		 * DB 상의 상태값과는 관계없는 코드값임.
		 */
		NOT_EXIST_FILTER(2001, "Filter Class is not exist...."),
		FILTER_REGULAR_ERROR(2002, "Not matched filter regulra expression"),
		KNOW_ANSWER_REGULAR_FAIL(2003, "Not matched answer regulra expresstion in openknow filter"),
		DATE_EXTRACT_FAIL(2004, "datetime extract fail..."),
		ANSWER_DATE_EXTRACT_FAIL(2005, "datetime of answer extract fail..."),
		KNOW_SECTION_REGULAR_FAIL(2006, "Not matched section regulra expresstion in openknow filter"),
		KNOW_EXTRACT_BLOCK_FAIL(2007, "CutterBlock method error in openknow filter"),
		GET_NEXT_URL_FAILURE(2008, "Know List Filter GetNextUrl method Fail."),
		EXTRACTURL_IS_MALFORMEDURL(2011, "Extract Url is MalformedURL"),
		REGEXP_POSTPROCESSING_ABNORMALITY(2015, "Regexp Postprocessing is abnormality."),
		FILTER_CLASS_NOT_FOUNDED(2021, "Filter Class Not Found"),
		FILTER_CLASS_INSTANTIATIONEXCEPTION(2022, "Filter Class Not Found"),
		FILTER_CLASS_ILLEGALACCESSException(2023, "Filter Class Not Found"),
		SITEID_SETTING_FAILURE(2025, "SiteID setting is failure"),
		SIGNATURE_CREATE_FAIL(2030, "Signature creation is failure"),
		
		HASH_NUM_CREATE_FAILURE(3010, "hash number creation is failure"),
		UNKNOWN(8000, "A unknown error has occured.");
		
		private final int error_code;
		private final String message;
		
		Errors(int error_code, String message) {
			this.error_code = error_code;
			this.message = message;
		}
		
		public int getErrorCode() { 
			return this.error_code;
		}
		public String getMessage() { 
			return this.message;
		}
		
		public static Errors getErrors(int errorCode) {
			if(codeToErrorsMap == null) {
				initMapping();
			}
			if(codeToErrorsMap.get(errorCode) == null) { 
				return codeToErrorsMap.get(8000);
			} else { 
				return codeToErrorsMap.get(errorCode);
			}
		}
		
		@Override
		public String toString() { 
			final StringBuilder sb = new StringBuilder();
			sb.append("ErrorCode : ");
			sb.append(error_code);
			sb.append(", ErrorMessage : ");
			sb.append(message);
			return sb.toString();
				
		}
	}
}