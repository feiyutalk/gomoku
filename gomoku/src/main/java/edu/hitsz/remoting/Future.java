package edu.hitsz.remoting;

public interface Future {

    boolean isSuccess();

	Throwable cause();
}
