package spaghetti.test

import spaghetti.test.dependency.DependencyModule
import spaghetti.test.dependency.Fruit
import spaghetti.test.dependency.Point2d

public native object TestModule {
	public fun addTwoNumbers(a:Int, b:Int): Int {
		return a + b
	}

	public fun getNextEnumValue(value:Fruit):Fruit {
		// return Fruit.fromValue(value.value() + 1);
		return value
	}

	public fun getValueOfTwo():String {
		return Numbers.TWO
	}

	public fun createPoint3dWithGivenValues(x:Int, y:Int, z:Int):Point3d {
		/*return {
			x: x,
			y: y,
			z: z
		};*/
		throw RuntimeException("no")
	}

	public fun getPointFromDependencyModule():Point2d {
		// return DependencyModule.getPoint();
		throw RuntimeException("no")
	}

	public fun getPointFromDependencyModuleViaCallback(x:Int, y:Int):Point2d {
		/*var returnedPoint:Point2d = null;
		spaghetti.test.dependency.DependencyModule.createPointViaCallback(x, y, fun (point:Point2d) {
			returnedPoint = point;
		});
		return returnedPoint;*/
		throw RuntimeException("no")
	}

	public fun returnPointViaCallback(x:Int, y:Int, z:Int, callback:(Point3d)->Unit) {
		//callback({x: x, y: y, z: z});
	}
}