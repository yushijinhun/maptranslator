package org.to2mbn.maptranslator.impl.model;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionSnapshot {

	public final Class<? extends Throwable> exceptionClass;
	public final String message;
	public final String stacktrace;

	public ExceptionSnapshot(Throwable exception) {
		exceptionClass = exception.getClass();
		message = exception.getMessage();
		stacktrace = ExceptionUtils.getStackTrace(exception);
	}

	public String getDisplayMessage() {
		return message == null ? "<" + exceptionClass.getCanonicalName() + ">" : message;
	}

}
