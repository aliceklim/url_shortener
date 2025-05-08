package org.example.controller;

import org.example.config.context.UserContext;
import org.example.dto.UrlDto;
import org.example.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    @MockBean
    private AuditorAware<String> auditorAware;

    @Test
    void testShortenUrl() throws Exception {
        String input = "{\"url\": \"https://example.com\"}";
        when(urlService.generateShortUrl(any(UrlDto.class))).thenReturn("https://magicurl.com/abc123");
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(content().string("https://magicurl.com/abc123"));

    }

    @Test
    void testGetOriginalUrl() throws Exception {
        when(urlService.getOriginalUrl("abc123")).thenReturn("https://example.com");

        mockMvc.perform(get("/url/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string("https://example.com"));

    }

}
