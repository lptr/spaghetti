package com.prezi.spaghetti.packaging

class CommonJsWrapperTest extends WrapperTestBase {
	def "CommonJS module"() {
		def originalScript = "/* Generated by Spaghetti */ function(SpaghettiConfiguration){}"
		def result = new CommonJsWrapper().wrap(mockParams("com.example.test", "1.0", ["com.example.alma", "com.example.bela"], originalScript))

		expect:
		result == [
		        'module.exports=(function(){',
					'/* Generated by Spaghetti */ ',
					'return{',
						'"module":',
							'(',
								'function(SpaghettiConfiguration){}\n',
							')',
							'.call({},(function(args){',
								'var baseUrl=__dirname;',
								'return{',
									'"baseUrl":baseUrl,',
									'"modules":{',
										'"com.example.alma":require("com.example.alma"),',
										'"com.example.bela":require("com.example.bela")',
									'},',
									'getName:function(){',
										'return "com.example.test";',
									'},',
									'getResourceUrl:function(resource){',
										'if(resource.substr(0,1)!="/"){',
											'resource="/"+resource;',
										'}',
										'return baseUrl+resource;',
									'}',
								'};',
							'})(arguments)),',
						'"version":"1.0"',
					'};',
				'})();'
		].join("")
	}

	def "CommonJS application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def result = new CommonJsWrapper().makeApplication(dependencyTree, "com.example.test", true)

		expect:
		result == [
				'require("com.example.test")["module"]["main"]();\n',
		].join("")
	}
}
