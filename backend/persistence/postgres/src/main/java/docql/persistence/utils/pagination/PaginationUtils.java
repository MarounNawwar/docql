package docql.persistence.utils.pagination;

import static java.util.Objects.isNull;
import static org.springframework.data.domain.Pageable.ofSize;
import static org.springframework.data.domain.Pageable.unpaged;

import org.springframework.data.domain.Pageable;

/**
 * Utility methods for converting limit/offset request parameters into Spring Data pagination types.
 */
public final class PaginationUtils {

  private PaginationUtils() {}

  /**
   * Creates a {@link Pageable} from optional limit and offset values.
   *
   * <p>When both arguments are {@code null}, this returns {@link Pageable#unpaged()}. A missing
   * limit defaults to {@link Integer#MAX_VALUE}, and a missing offset defaults to {@code 0}.
   *
   * @param limit the maximum number of items to return
   * @param offset the zero-based starting position
   * @return a matching {@link Pageable} instance
   */
  public static Pageable pagination(Integer limit, Integer offset) {
    if (isNull(limit) && isNull(offset)) {
      return unpaged();
    }
    if (isNull(limit)) {
      limit = Integer.MAX_VALUE;
    }
    if (isNull(offset)) {
      offset = 0;
    }
    int pageNumber = offset / limit;
    return ofSize(limit).withPage(pageNumber);
  }
}
