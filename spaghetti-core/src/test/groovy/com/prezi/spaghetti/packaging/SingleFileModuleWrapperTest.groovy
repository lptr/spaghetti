package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.internal.Version
import com.prezi.spaghetti.packaging.internal.SingleFileModuleWrapper

class SingleFileModuleWrapperTest extends WrapperTestBase {
	def "Single file module"() {
		def originalScript = "/* Generated by Spaghetti */ module(function(Spaghetti){})"
		def result = new SingleFileModuleWrapper().wrap(mockParams("com.example.test", "1.0", ["com.example.alma", "com.example.bela"], ["React": "react", "\$": "jquery"], originalScript))

		expect:
		result == [
		        'function(){',
					'var module=(function(dependencies){',
						'return function(init){',
							'return init.call({},(function(){',
								'var baseUrl=__dirname;',
								'return{',
									'getSpaghettiVersion:function(){return "' + Version.SPAGHETTI_VERSION + '";},',
									'getModuleName:function(){',
										'return "com.example.test";',
									'},',
									'getModuleVersion:function(){return "1.0";},',
									'getResourceUrl:function(resource){',
										'if(resource.substr(0,1)!="/"){',
											'resource="/"+resource;',
										'}',
										'return baseUrl+resource;',
									'},',
									'"dependencies":{',
										'"com.example.alma":dependencies[2],',
										'"com.example.bela":dependencies[3]',
									'}',
								'};',
							'})());',
						'};',
					'})(arguments);',
					'var $=arguments[0];',
					'var React=arguments[1];',
					'/* Generated by Spaghetti */ ',
					'return{',
						'"module":(function(){return module(function(Spaghetti){})\n})(),',
						'"version":"1.0",',
						'"spaghettiVersion":"' + Version.SPAGHETTI_VERSION + '"',
					'};',
				'}'
		].join("")
	}

	def "Single file application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def result = new SingleFileModuleWrapper().makeApplication(dependencyTree, "com.example.test", true, ["react": "react"])

		expect:
		result == [
				'var mainModule=modules["com.example.test"]["module"];',
				'mainModule["main"]();'
		].join("")
	}
}
