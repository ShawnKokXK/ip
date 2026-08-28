package maggigorengayam.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import maggigorengayam.util.DateTimeUtil.ParsedDateTime;

public class DateTimeUtilTest {

    @Test
    public void parse_dateOnly_returnsDateWithNullTime() {
        ParsedDateTime result = DateTimeUtil.parse("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), result.date);
        assertNull(result.time);
    }

    @Test
    public void parse_dateWithTime_returnsDateAndTime() {
        ParsedDateTime result = DateTimeUtil.parse("2019-12-02 1800");

        assertEquals(LocalDate.of(2019, 12, 2), result.date);
        assertEquals(LocalTime.of(18, 0), result.time);
    }

    @Test
    public void parse_leadingAndTrailingWhitespace_trimmedAndParsed() {
        ParsedDateTime result = DateTimeUtil.parse("  2019-12-02  ");

        assertEquals(LocalDate.of(2019, 12, 2), result.date);
        assertNull(result.time);
    }

    @Test
    public void parse_extraInternalWhitespaceBetweenDateAndTime_trimmedAndParsed() {
        ParsedDateTime result = DateTimeUtil.parse("2019-12-02  1800");

        assertEquals(LocalDate.of(2019, 12, 2), result.date);
        assertEquals(LocalTime.of(18, 0), result.time);
    }

    /**
     * DateTimeFormatter.ofPattern defaults to ResolverStyle.SMART, which
     * silently clamps an out-of-range day to the last valid day of the
     * month instead of rejecting it - so "2019-02-30" resolves to Feb 28,
     * not a thrown exception. This documents that actual (non-obvious)
     * behavior rather than the stricter behavior one might expect.
     */
    @Test
    public void parse_dayOverflowInMonth_clampsToLastValidDayOfMonth() {
        ParsedDateTime result = DateTimeUtil.parse("2019-02-30");

        assertEquals(LocalDate.of(2019, 2, 28), result.date);
        assertNull(result.time);
    }

    @Test
    public void parse_monthOutOfRange_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-13-01"));
    }

    @Test
    public void parse_hourOutOfRange_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-12-02 2500"));
    }

    @Test
    public void parse_nonNumericText_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("abcd"));
    }

    @Test
    public void parse_emptyString_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse(""));
    }

    @Test
    public void parse_unpaddedMonthAndDay_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-2-2"));
    }

    @Test
    public void parse_missingSpaceSeparatorBeforeTime_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-12-02T1800"));
    }

    @Test
    public void parse_timeShorterThanFourDigits_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-12-02 900"));
    }
}
