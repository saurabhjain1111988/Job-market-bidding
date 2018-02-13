package com.market.jobmarket.project.exception;

public class JobMarketException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JobMarketException(JobMarketExceptionReason reason, String errorMsg) {
		super(errorMsg);
		this.reason = reason;
	}

	public JobMarketException(JobMarketExceptionReason reason, String message, Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	public JobMarketException(JobMarketExceptionReason reason, RuntimeException cause) {
		super(cause);
		this.reason = reason;
	}

	public JobMarketException(JobMarketExceptionReason reason, String message, RuntimeException cause) {
		super(message, cause);
		this.reason = reason;
	}

	public JobMarketException(JobMarketExceptionReason reason, Throwable cause) {
		super(cause);
		this.reason = reason;
	}

	public JobMarketExceptionReason getReason() {
		return reason;
	}

	private JobMarketExceptionReason reason;

	@Override
	public String toString() {
		if (reason == null)
			return super.toString();

		return this.getClass().getName() + ": (Reason: " + reason.toString() + ") " + getMessage();
	}

}
