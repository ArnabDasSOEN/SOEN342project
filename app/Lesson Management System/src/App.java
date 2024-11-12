import Database.DatabaseSetup;
import View.Console;

public class App {
	public static void main(String[] args) throws Exception {
		DatabaseSetup.resetDatabase();
		Console console = new Console();
		console.consoleMenu();
	}
}
