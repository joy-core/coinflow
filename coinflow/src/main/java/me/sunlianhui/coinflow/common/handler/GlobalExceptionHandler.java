package me.sunlianhui.coinflow.common.handler;

import me.sunlianhui.coinflow.common.exception.BizException;
import me.sunlianhui.coinflow.common.web.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BizException.class)
	public R<Void> handleBizException(BizException ex) {
		return R.fail(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public R<Void> handleOtherExceptions(Exception ex) {
		ex.printStackTrace(); // For debugging purposes
		return R.fail("Internal server error");
	}
}
