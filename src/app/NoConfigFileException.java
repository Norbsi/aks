package app;

public class NoConfigFileException extends RuntimeException {
	private static final long serialVersionUID = -8889498420981531686L;

	public NoConfigFileException(final Exception exception) {
		super(exception);
	};
}
