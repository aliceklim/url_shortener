package org.example.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final int ENCODING_FACTOR = 62;
    private static final String BASE_62_ALPHABET = "XyZ012abcDEFGHIJKLmnopQRstuVWXklMNOPghijSTUvw3456789rqABCdefzY";

    public String encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % ENCODING_FACTOR);
            sb.append(BASE_62_ALPHABET.charAt(remainder));
            number /= ENCODING_FACTOR;
        }
        return sb.reverse().toString();
    }
}
