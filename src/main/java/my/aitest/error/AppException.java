package my.aitest.error;

public class AppException extends RuntimeException{
  public AppException(Throwable ex) {
    super(ex);
  }

  public AppException(String message, Throwable ex) {
    super(message, ex);
  }
}
