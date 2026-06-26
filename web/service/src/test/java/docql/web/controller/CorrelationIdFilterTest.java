package docql.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorrelationIdFilterTest {

  private final CorrelationIdFilter filter = new CorrelationIdFilter();

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @Test
  void existingHeader_isReusedAndPropagated() throws Exception {
    String existingId = "my-existing-corr-id";
    var request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, existingId);
    var response = new MockHttpServletResponse();
    FilterChain chain = captureChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).isEqualTo(existingId);
    verify(chain).doFilter(request, response);
  }

  @Test
  void blankHeader_generatesNewUuid() throws Exception {
    var request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, "   ");
    var response = new MockHttpServletResponse();
    FilterChain chain = captureChain();

    filter.doFilter(request, response, chain);

    String assigned = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
    assertThat(assigned).isNotBlank();
    assertThat(assigned).isNotEqualTo("   ");
  }

  @Test
  void correlationId_isStoredInMdcDuringFilterChain() throws Exception {
    String existingId = "mdc-test-id";
    var request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, existingId);
    var response = new MockHttpServletResponse();

    final String[] capturedMdcValue = {null};
    FilterChain chain = (req, res) -> capturedMdcValue[0] = MDC.get(CorrelationIdFilter.MDC_KEY);

    filter.doFilter(request, response, chain);

    assertThat(capturedMdcValue[0]).isEqualTo(existingId);
  }

  @Test
  void correlationId_isRemovedFromMdcAfterRequest() throws Exception {
    var request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, "to-be-cleaned");
    var response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    filter.doFilter(request, response, chain);

    assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
  }

  @Test
  void correlationId_isRemovedFromMdcEvenWhenChainThrows() throws Exception {
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);
    doThrow(new RuntimeException("chain error")).when(chain).doFilter(any(), any());

    try {
      filter.doFilter(request, response, chain);
    } catch (RuntimeException ignored) {
      // expected
    }

    assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private FilterChain captureChain() throws Exception {
    FilterChain chain = mock(FilterChain.class);
    return chain;
  }
}
