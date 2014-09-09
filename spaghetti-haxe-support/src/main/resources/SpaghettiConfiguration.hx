extern class SpaghettiConfiguration {
	/**
	 * Returns the module version.
	 */
	public static function getVersion():String;

	/**
	 * Returns the Spaghetti version used to build the module.
	 */
	public static function getSpaghettiVersion():String;

	/**
	 * Returns the name of the module.
	 */
	public static function getName():String;

	/**
	 * Returns a URL pointing to this module's given resource.
	 */
	public static function getResourceUrl(resource:String):String;
}
