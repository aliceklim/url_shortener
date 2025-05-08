package org.example.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

class Base62EncoderTest {
    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    void testEncode_SmallNumber() {
        String result = base62Encoder.encode(1);
        Assertions.assertEquals("y", result);
    }

    @Test
    void testEncode_LargeNumber() {
        long input = 123456789L;
        String result = base62Encoder.encode(input);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void testEncode_Zero() {
        String result = base62Encoder.encode(0);
        Assertions.assertEquals("", result);
    }
}
