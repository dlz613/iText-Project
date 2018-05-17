package il.co.aman.itextimpl;

public class ItextResult {
	String _errorCode, _errorMsg, _result;
	byte[] _streamOut = null;
	boolean _generateRow = false;
	
	public String errorCode() {
		return this._errorCode;
	}
	
	public String errorMsg() {
		return this._errorMsg;
	}
	
	public String result() {
		return this._result;
	}
	
	public byte[] streamOut() {
		return this._streamOut;
	}
	
	public boolean generateRow() {
		return this._generateRow;
	}
	
	public ItextResult(String ERROR_CODE, String ERROR_MSG, String RESULT, byte[] STREAM_OUT, boolean generateRow) {
		this._errorCode = ERROR_CODE;
		this._errorMsg = ERROR_MSG;
		this._result = RESULT;
		this._streamOut = STREAM_OUT;
		this._generateRow = generateRow;
	}

}
