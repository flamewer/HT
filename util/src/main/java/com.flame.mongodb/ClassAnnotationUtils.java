package com.flame.mongodb;
import java.lang.reflect.Field;

import org.springframework.data.annotation.Id;

/**
 * 类的字段合并工具类.. <br>
 */
public final class ClassAnnotationUtils {

	/**
	 * 私有构造函数. <br>
	 */
	private ClassAnnotationUtils() {
	}
	/**
	 * 获取PO的主键属性.. <br>
	 * @param boClass bo类型.
	 * @return 主键.
	 */
	public static Field getPrimaryKeyField(final Class< ? > boClass) {
		return getPrimaryKeyField(boClass.getSuperclass().getDeclaredFields());
	}
	
	/**
	 * 从字段列表中获取主键属性. <br>
	 * @author chenxiangbai 2012-9-27 下午4:31:33 <br> 
	 * @param fieldList 字段列表.
	 * @return 主键.
	 */
	public static Field getPrimaryKeyField(final Field[] fieldList) {
		Field primaryKeyField = null;
		for (Field field : fieldList) {
			if (field.isAnnotationPresent(Id.class)) {
				primaryKeyField = field;
				break;
			}
		}
		
		if (primaryKeyField != null) {
			return primaryKeyField;
		} else {
			throw new RuntimeException("没有找到主键属性");
		}
	}
	
	
	/**
	 * 获取PO对象的主键属性的值.. <br>
	 * @param cls 对象类型.
	 * @param <T> 对象类型.
	 * @param bo 业务对象.
	 * @return PO对象的主键属性的值.
	 */
	public static <T> String getKeyValue(final Class<T> cls, final T bo) {
		try {
			final Field primaryKeyField = ClassAnnotationUtils.getPrimaryKeyField(cls);
			primaryKeyField.setAccessible(true);
			return String.valueOf(primaryKeyField.get(bo));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取PO对象的主键属性的值.. <br>
	 * @param cls 对象类型.
	 * @param <T> 对象类型.
	 * @param bo 业务对象.
	 * @return PO对象的主键属性的值.
	 */
	public static <T> Object getFieldValue(final Class<T> cls, final T bo, final String fieldName) {
		try {
			final Field primaryKeyField = ClassAnnotationUtils.getPrimaryKeyField(cls);
			primaryKeyField.setAccessible(true);
			return primaryKeyField.get(bo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}