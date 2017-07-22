/**
 *
 */
package dao;

/**
 * @author Cyclops-THSS
 *
 */
public interface Callback<T> {

	public default void run(T val) {
		if (val == null)
			this.empty(val);
		else
			this.handle(val);
	}

	public default void handle(T val) {
	}

	public default void empty(T val) {
	}

	public default void error(String msg) {
	}

	public static <T> void call(Callback<T> _callback, T parameter) {
		if (_callback != null) {
			try {
				_callback.run(parameter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
