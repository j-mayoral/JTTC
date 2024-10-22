package org.turing2javagenerics;

public abstract class Report<T> {

  private final T value;
  private final String error;

  private Report(T value, String error) {
    this.value = value;
    this.error = error;
  }

  public abstract boolean isOk();
  public abstract String getError();
  public abstract T get();

  public static class Ok<T> extends Report<T> {

    public Ok(T value) {super(value, null);}

    @Override public boolean isOk() {return true;}
    @Override public String getError() {return null;}
    @Override public T get() {return super.value;}
  }

  public static class Ko<T> extends Report<T> {

    public Ko(String error) {super(null, error);}

    @Override public boolean isOk() {return false;}
    @Override public String getError() {return super.error;}
    @Override public T get() {return null;}
  }

}
