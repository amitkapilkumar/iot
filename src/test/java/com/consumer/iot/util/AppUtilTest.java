package com.consumer.iot.util;

import com.consumer.iot.exception.InvalidEnumValueException;
import com.consumer.iot.model.Level;
import com.consumer.iot.model.Mode;
import org.junit.Test;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.consumer.iot.util.AppUtil.CYCLE_PLUS_TRACKER;
import static com.consumer.iot.util.AppUtil.GENERAL_TRACKER;
import static com.consumer.iot.util.AppUtil.formatDate;
import static com.consumer.iot.util.AppUtil.getDate;
import static com.consumer.iot.util.AppUtil.getName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AppUtilTest {

    @Test
    public void testIsFileExists() {
        assertTrue(AppUtil.isFileExists("C:/Users/amitk/MyStuffs/data.csv"));
        assertFalse(AppUtil.isFileExists("C:/Users/amitk/MyStuffs/data1.csv"));
    }

    @Test
    public void testEnumLevel() {
        assertEquals(Level.fromValue("fULL"), Level.FULL);
        assertEquals(Level.fromValue("Critical"), Level.CRITICAL);
        assertEquals(Level.fromValue("hiGh"), Level.HIGH);
    }

    @Test(expected = InvalidEnumValueException.class)
    public void testNullForLevelWithInvalidEnumValueException() {
        Level.fromValue(null);
    }

    @Test(expected = InvalidEnumValueException.class)
    public void testEmptyForLevelWithInvalidEnumValueException() {
        Level.fromValue("");
    }

    @Test(expected = InvalidEnumValueException.class)
    public void testInvalidValueForLevelWithInvalidEnumValueException() {
        Level.fromValue("test");
    }

    @Test(expected = InvalidEnumValueException.class)
    public void testNullForModeWithInvalidEnumValueException() {
        Mode.fromValue(null);
    }

    @Test(expected = InvalidEnumValueException.class)
    public void testEmptyForModeWithInvalidEnumValueException() {
        Mode.fromValue("");
    }

    @Test(expected = InvalidEnumValueException.class)
    public void testInvalidValueForModeWithInvalidEnumValueException() {
        Mode.fromValue("test");
    }

    @Test
    public void testEnumMode() {
        assertEquals(Mode.fromValue("on"), Mode.ON);
        assertEquals(Mode.fromValue("ON"), Mode.ON);
        assertEquals(Mode.fromValue("oN"), Mode.ON);
        assertEquals(Mode.fromValue("OFF"), Mode.OFF);
        assertEquals(Mode.fromValue("oFf"), Mode.OFF);
        assertEquals(Mode.fromValue("off"), Mode.OFF);
    }

    @Test
    public void testGetName() {
        assertEquals(getName("WG11155631"), CYCLE_PLUS_TRACKER);
        assertEquals(getName("6911155631"), GENERAL_TRACKER);
    }

    @Test
    public void testDate() throws ParseException {
        Date date = Date.from(LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant());
        String strDate = formatDate(date);

        assertNotNull(strDate);

        Date actualDate = getDate(strDate);
        assertNotNull(actualDate);
    }
}
