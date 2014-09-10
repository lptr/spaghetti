package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.Version

class AmdWrapperTest extends WrapperTestBase {
	def "AMD module"() {
		def originalScript = "/* Generated by Spaghetti */ function(SpaghettiConfiguration){}"
		def result = new AmdWrapper().wrap(mockParams("com.example.test", "1.0", ["com.example.alma", "com.example.bela"], originalScript))

		expect:
		result == [
		        'define(["require","com.example.alma","com.example.bela"],function(){',
					'/* Generated by Spaghetti */ ',
					'return{',
						'"module":(',
							'function(SpaghettiConfiguration){}\n)',
							'.call({},(function(args){',
								'var moduleUrl=args[0]["toUrl"]("com.example.test.js");',
								'var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf("/"));',
								'return{',
									'getSpaghettiVersion:function(){return "' + Version.SPAGHETTI_BUILD + '";},',
									'getName:function(){',
										'return "com.example.test";',
									'},',
									'getVersion:function(){return "1.0";},',
									'getResourceUrl:function(resource){',
										'if(resource.substr(0,1)!="/"){',
											'resource="/"+resource;',
										'}',
										'return baseUrl+resource;',
									'},',
									'"modules":{',
										'"require":args[0],',
										'"com.example.alma":args[1],',
										'"com.example.bela":args[2]',
									'}',
								'};',
							'})(arguments)),',
						'"version":"1.0",',
						'"spaghettiVersion":"' + Version.SPAGHETTI_BUILD + '"',
					'};',
				'});'
		].join("")
	}

	def "AMD application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def result = new AmdWrapper().makeApplication(dependencyTree, "com.example.test", true)

		expect:
		result == [
				'require["config"]({',
					'"baseUrl":".",',
					'"paths":{',
						'"com.example.alma": "modules/com.example.alma/com.example.alma",',
						'"com.example.bela": "modules/com.example.bela/com.example.bela",',
						'"com.example.test": "modules/com.example.test/com.example.test"',
					'}',
				'});',
		        'require(["com.example.test"],function(__mainModule){',
					'__mainModule["module"]["main"]();',
				'});',
				'\n'
		].join("")
	}
}
