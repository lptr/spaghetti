package prezi.test.client;

class Client {
	public static function main() {
		var layout = Modules.getLayout();
		var text = layout.createText(2);
		text.insert(0, "World");
		text.insert(0, "Hello ");
		trace(text.getRawText());
	}
}