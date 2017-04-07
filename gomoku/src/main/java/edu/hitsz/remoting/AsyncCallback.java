package edu.hitsz.remoting;

public interface AsyncCallback {

    public void operationComplete(final ResponseFuture responseFuture);
}
