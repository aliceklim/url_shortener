package org.example.config.base62encoder;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class Base62EncoderConfig {
    private Base62EncoderConfig(){}
    private static final int ENCODING_FACTOR = 62;
    private static final String BASE_62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
}
