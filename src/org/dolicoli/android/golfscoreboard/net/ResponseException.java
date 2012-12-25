package org.dolicoli.android.golfscoreboard.net;

public class ResponseException extends Exception {

	private static final long serialVersionUID = 5687683610307447569L;

	private String errorMessage = null;

	public ResponseException(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
