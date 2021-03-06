package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.internal.Version
import com.prezi.spaghetti.packaging.internal.AmdModuleWrapper

class AmdModuleWrapperTest extends WrapperTestBase {
	def "AMD module"() {
		def originalScript = "/* Generated by Spaghetti */ module(function(Spaghetti){})"
		def result = new AmdModuleWrapper().wrap(mockParams("com.example.test", "1.0", ["com.example.alma", "com.example.bela"], ["React": "react", "\$": "jquery"], originalScript))

		expect:
		result == [
		        'define(["jquery","react","require","com.example.alma","com.example.bela"],function(){',
					'var module=(function(dependencies){',
						'return function(init){',
							'return init.call({},(function(){',
								'var moduleUrl=dependencies[2]["toUrl"]("com.example.test.js");',
								'var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf("/"));',
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
										'"require":dependencies[2],',
										'"com.example.alma":dependencies[3],',
										'"com.example.bela":dependencies[4]',
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
				'});'
		].join("")
	}

	def "AMD application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def externals = [
				"react": "react"
		]
		def result = new AmdModuleWrapper().makeApplication(dependencyTree, "com.example.test", true, externals)

		expect:
		result == [
				'require["config"]({',
					'"baseUrl":".",',
					'"paths":{',
						'"com.example.alma":"modules/com.example.alma/com.example.alma",',
						'"com.example.bela":"modules/com.example.bela/com.example.bela",',
						'"com.example.test":"modules/com.example.test/com.example.test",',
						'"react":"react"',
					'}',
				'});',
		        'require(["com.example.test"],function(__mainModule){',
					'__mainModule["module"]["main"]();',
				'});',
				'\n'
		].join("")
	}
}
