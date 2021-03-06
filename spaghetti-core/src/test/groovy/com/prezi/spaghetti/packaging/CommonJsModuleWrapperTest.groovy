package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.internal.Version
import com.prezi.spaghetti.packaging.internal.CommonJsModuleWrapper

class CommonJsModuleWrapperTest extends WrapperTestBase {
	def "CommonJS module"() {
		def originalScript = "/* Generated by Spaghetti */ module(function(Spaghetti){})"
		def result = new CommonJsModuleWrapper().wrap(mockParams("com.example.test", "1.0", ["com.example.alma", "com.example.bela"], ["React": "react", "\$": "jquery"], originalScript))

		expect:
		result == [
		        'module.exports=(function(){',
					'var __resolveDependency=function(module){',
						'if (global["spaghetti"]&&global["spaghetti"]["config"]&&global["spaghetti"]["config"]["paths"]&&global["spaghetti"]["config"]["paths"][module]){',
							'return global["spaghetti"]["config"]["paths"][module];',
						'}else{',
							'return module;',
						'}',
					'};',
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
										'"com.example.alma":require(__resolveDependency("com.example.alma")),',
										'"com.example.bela":require(__resolveDependency("com.example.bela"))',
									'}',
								'};',
							'})());',
						'};',
					'})(arguments);',
					'var $=require(__resolveDependency("jquery"));',
					'var React=require(__resolveDependency("react"));',
					'/* Generated by Spaghetti */ ',
					'return{',
						'"module":(function(){return module(function(Spaghetti){})\n})(),',
						'"version":"1.0",',
						'"spaghettiVersion":"' + Version.SPAGHETTI_VERSION + '"',
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
		def externals = [
				"react": "react"
		]
		def result = new CommonJsModuleWrapper().makeApplication(dependencyTree, "com.example.test", true, externals)

		expect:
		result == [
				'global["spaghetti"]={"config":{"paths":{"react":"react"}}};',
				'var mainModule=require("com.example.test")["module"];',
				'mainModule["main"]();\n',
		].join("")
	}
}
