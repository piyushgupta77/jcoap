package org.ws4d.coap.messages;

import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.AbstractCoapMessage.CoapHeaderOption;
import org.ws4d.coap.messages.AbstractCoapMessage.CoapHeaderOptionType;

public class BasicCoapResponse extends AbstractCoapMessage implements CoapResponse {
	public enum CoapResponseCode {
		Created_201(65), 
		Deleted_202(66), 
		Valid_203(67), 
		Changed_204(68), 
		Content_205(69), 
		Bad_Request_400(128), 
		Unauthorized_401(129), 
		Bad_Option_402(130),
		Forbidden_403(131), 
		Not_Found_404(132), 
		Method_Not_Allowed_405(133), 
		Precondition_Failed_412(140), 
		Request_Entity_To_Large_413(141), 
		Unsupported_Media_Type_415(143), 
		Internal_Server_Error_500(160), 
		Not_Implemented_501(161),
		Bad_Gateway_502(162), 
		Service_Unavailable_503(163), 
		Gateway_Timeout_504(164), 
		Proxying_Not_Supported_505(165), 
		UNKNOWN(-1);

		private int code;

		private CoapResponseCode(int code) {
			this.code = code;
		}

		public static CoapResponseCode parseResponseCode(int codeValue) {
			switch (codeValue) {
			/* 32..63: reserved */
			/* 64 is not used anymore */
			// case 64:
			// this.code = ResponseCode.OK_200;
			// break;
			case 65:  return Created_201;
			case 66:  return Deleted_202;
			case 67:  return Valid_203;
			case 68:  return Changed_204;
			case 69:  return Content_205;
			case 128: return Bad_Request_400;
			case 129: return Unauthorized_401;
			case 130: return Bad_Option_402;
			case 131: return Forbidden_403;
			case 132: return Not_Found_404;
			case 133: return Method_Not_Allowed_405;
			case 140: return Precondition_Failed_412;
			case 141: return Request_Entity_To_Large_413;
			case 143: return Unsupported_Media_Type_415;
			case 160: return Internal_Server_Error_500;
			case 161: return Not_Implemented_501;
			case 162: return Bad_Gateway_502;
			case 163: return Service_Unavailable_503;
			case 164: return Gateway_Timeout_504;
			case 165: return Proxying_Not_Supported_505;
			default:
				if (codeValue >= 64 && codeValue <= 191) {
					return UNKNOWN;
				} else {
					throw new IllegalArgumentException("Invalid Response Code");
				}
			}
		}

		public int getValue() {
			return code;
		}

		@Override
		public String toString() {
			switch (this) {
			case Created_201:
				return "Created_201";
			case Deleted_202:
				return "Deleted_202";
			case Valid_203:
				return "Valid_203";
			case Changed_204:
				return "Changed_204";
			case Content_205:
				return "Content_205";
			case Bad_Request_400:
				return "Bad_Request_400";
			case Unauthorized_401:
				return "Unauthorized_401";
			case Bad_Option_402:
				return "Bad_Option_402";
			case Forbidden_403:
				return "Forbidden_403";
			case Not_Found_404:
				return "Not_Found_404";
			case Method_Not_Allowed_405:
				return "Method_Not_Allowed_405";
			case Precondition_Failed_412:
				return "Precondition_Failed_412";
			case Request_Entity_To_Large_413:
				return "Request_Entity_To_Large_413";
			case Unsupported_Media_Type_415:
				return "Unsupported_Media_Type_415";
			case Internal_Server_Error_500:
				return "Internal_Server_Error_500";
			case Not_Implemented_501:
				return "Not_Implemented_501";
			case Bad_Gateway_502:
				return "Bad_Gateway_502";
			case Service_Unavailable_503:
				return "Service_Unavailable_503";
			case Gateway_Timeout_504:
				return "Gateway_Timeout_504";
			case Proxying_Not_Supported_505:
				return "Proxying_Not_Supported_505";
			default:
				return "Unknown_Response_Code";
			}
		}
	}
	
	CoapResponseCode responseCode;

	public BasicCoapResponse(byte[] bytes, int length){
		this(bytes, length, 0);
	}
	
	public BasicCoapResponse(byte[] bytes, int length, int offset){
		serialize(bytes, length, offset);
		/* check if response code is valid, this function throws an error in case of an invalid argument */
		responseCode = CoapResponseCode.parseResponseCode(this.messageCodeValue);
		
		//TODO: check integrity of header options
	}

	/* token can be null */
	public BasicCoapResponse(CoapPacketType packetType, CoapResponseCode responseCode, int messageId, byte[] requestToken){
		this.version = 1;
		
		this.packetType = packetType;
		
		this.responseCode = responseCode;
		if (responseCode == CoapResponseCode.UNKNOWN){
			throw new IllegalArgumentException("UNKNOWN Response Code not allowed");
		}
		
		this.messageCodeValue = responseCode.getValue();
		this.messageId = messageId;		
		
		setToken(requestToken);
	}
	
	
	@Override
	public CoapResponseCode getResponseCode() {
		return responseCode;
	}

	@Override
	public void setMaxAge(int maxAge){
		if (options.optionExists(CoapHeaderOptionType.Max_Age)){
			throw new IllegalStateException("Max Age option already exists");
		}
		if (maxAge < 0){
			throw new IllegalStateException("Max Age MUST be an unsigned value");
		}
		options.addOption(CoapHeaderOptionType.Max_Age, long2CoapUint(maxAge));
	}
	
    @Override
    public long getMaxAge(){
    	CoapHeaderOption option = options.getOption(CoapHeaderOptionType.Max_Age);
    	if (option == null){
    		return -1; //TODO: return default Coap Max Age
    	}
      	return coapUint2Long((options.getOption(CoapHeaderOptionType.Uri_Port).getOptionData()));
    }
	
    @Override
    public void setETag(byte[] etag){
    	if (etag == null){
    		throw new IllegalArgumentException("etag MUST NOT be null");
    	}
    	if (etag.length < 1 || etag.length > 8){
    		throw new IllegalArgumentException("Invalid etag length");
    	}
    	options.addOption(CoapHeaderOptionType.Etag, etag);
    }
    
    @Override
    public byte[] getETag(){
    	CoapHeaderOption option = options.getOption(CoapHeaderOptionType.Etag);
    	if (option == null){
    		return null;
    	}
    	return option.getOptionData();
    }

	@Override
	public boolean isRequest() {
		return false;
	}

	@Override
	public boolean isResponse() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
	
    @Override
	public String toString() {
    	return packetType.toString() + ", " + responseCode.toString() + ", MsgId: " + getMessageID() +", #Options: " + options.getOptionCount(); 
	}


}