package tw.edu.ntu.csie.mhci.tapassist.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.util.Log;

public class Reflection {

	public static Object invokeHideMethod(Object obj, String methodName,
			Object... parameterTypes) {
		Class<? extends Object> cls = obj.getClass();
		return invokeHideMethod(cls, methodName, parameterTypes);
	}

	public static Object invokeHideMethod(Class cls, String methodName,
			Object... parameterTypes) {
		try {
			Class[] clss = new Class[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				clss[i] = parameterTypes[i].getClass();
			}

			//TODO(ggm) overloading problems
			
			Method[] methods = cls.getMethods();
			for (Method m : methods) {
				if (m.getName().equals(methodName)) {
					return m.invoke(null, parameterTypes);
				}
			}
			return null;

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
