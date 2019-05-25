package com.babenko.testkg.asserts;


import com.babenko.testkg.asserts.exception.AssertionErrorException;
import com.babenko.testkg.asserts.exception.ComparisonFailureException;

//TODO по-хорошему здесь надо как-то из переданного сообщения и параметров формировать красивый ответ и где-то выводить
public class Assert {
    /**
     * Указывает на то что бы тестовый метод завалился при этом выводя текстовое сообщение.
     *
     * @param message сообщение, которое будет записано в искллючение
     */
    public static void fail(String message) {
        throw new AssertionErrorException(message);
    }

    /**
     * Проверяет, что логическое условие истинно.
     *
     * @param message   сообщение, которое будет записано в искллючение
     * @param condition условие, которое должно быть истинное
     */
    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    /**
     * Проверяет, что два значения совпадают.
     * Примечание: для массивов проверяются ссылки, а не содержание массивов.
     *
     * @param message  сообщение, которое будет записано в искллючение
     * @param expected ожидаемое значение
     * @param actual   актуальное значение
     */
    public static void assertsEquals(String message, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new ComparisonFailureException(message);
        }
    }

    /**
     * Проверяет, что объект является пустым null.
     *
     * @param message сообщение, которое будет записано в искллючение
     * @param object  проверяется на null
     */
    public static void assertNull(String message, Object object) {
        if (object != null) {
            throw new ComparisonFailureException(message);
        }
    }

    /**
     * Проверяет, что объект не является null.
     *
     * @param message сообщение, которое будет записано в искллючение
     * @param object  проверяется на не равенство null
     */
    public static void assertNotNull(String message, Object object) {
        if (object == null) {
            throw new ComparisonFailureException(message);
        }
    }

    /**
     * Проверяет, что обе переменные относятся к одному объекту.
     *
     * @param message  сообщение, которое будет записано в искллючение
     * @param expected ожидаемое значение
     * @param actual   реальное значение
     */
    public static void assertSame(String message, Object expected, Object actual) {
        if (expected == actual) {
            throw new ComparisonFailureException(message);
        }
    }

    /**
     * Проверяет, что обе переменные относятся к разным объектам.
     *
     * @param message  сообщение, которое будет записано в искллючение
     * @param expected ожидаемое значение
     * @param actual   реальное значение
     */
    public static void assertNotSame(String message, Object expected, Object actual) {
        if (expected != actual) {
            throw new ComparisonFailureException(message);
        }
    }

}
